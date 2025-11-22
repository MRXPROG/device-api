package com.example.device.api.service;

import com.example.device.api.dto.requests.PatchDeviceRequest;
import com.example.device.api.dto.requests.UpdateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.exception.ForbiddenOperationException;
import com.example.device.api.mapper.DeviceMapper;
import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.impl.CommandDeviceServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandDeviceServiceImplTest {

    @Mock
    private DeviceRepository repository;

    @Mock
    private DeviceMapper mapper;

    @Mock
    private DeviceValidator validator;

    @InjectMocks
    private CommandDeviceServiceImpl service;

    private Device existingDevice;

    @BeforeEach
    void setup() {
        existingDevice = new Device()
                .setId(10L)
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);
    }

    @Test
    void updateDevice_Success() {
        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("iPhone New")
                .setBrand("Apple")
                .setState(DeviceState.INACTIVE);

        DeviceResponse response = new DeviceResponse()
                .setId(10L)
                .setName("iPhone New")
                .setBrand("Apple")
                .setState(DeviceState.INACTIVE);

        when(repository.findById(10L)).thenReturn(Optional.of(existingDevice));
        when(mapper.toResponse(existingDevice)).thenReturn(response);

        DeviceResponse result = service.updateDevice(10L, req);

        assertEquals(response, result);
        assertEquals("iPhone New", existingDevice.getName());
        assertEquals("Apple", existingDevice.getBrand());
        assertEquals(DeviceState.INACTIVE, existingDevice.getState());

        verify(validator).ensureNotInUseForNameBrandChange(existingDevice, "iPhone New", "Apple");
        verify(validator).ensureNameBrandUnique(10L, "iPhone New", "Apple");
        verify(repository).save(existingDevice);
    }

    @Test
    void updateDevice_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> service.updateDevice(99L, new UpdateDeviceRequest()));

        verifyNoInteractions(validator);
    }

    @Test
    void updateDevice_InUseNameChange_Forbidden() {
        existingDevice.setState(DeviceState.IN_USE);

        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("New Name")
                .setBrand("Apple")
                .setState(DeviceState.IN_USE);

        when(repository.findById(10L)).thenReturn(Optional.of(existingDevice));
        doThrow(new ForbiddenOperationException("Cannot change"))
                .when(validator).ensureNotInUseForNameBrandChange(existingDevice, "New Name", "Apple");

        assertThrows(ForbiddenOperationException.class,
                () -> service.updateDevice(10L, req));

        verify(repository, never()).save(any());
    }

    @Test
    void updateDevice_NameBrandNotUnique() {
        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("iPhone X")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);

        when(repository.findById(10L)).thenReturn(Optional.of(existingDevice));

        doThrow(new DeviceAlreadyExistsException("iPhone X", "Apple"))
                .when(validator).ensureNameBrandUnique(10L, "iPhone X", "Apple");

        assertThrows(DeviceAlreadyExistsException.class,
                () -> service.updateDevice(10L, req));

        verify(repository, never()).save(any());
    }

    @Test
    void patchDevice_Success() {
        PatchDeviceRequest req = new PatchDeviceRequest()
                .setBrand("Apple Pro")
                .setState(DeviceState.INACTIVE);

        DeviceResponse response = new DeviceResponse()
                .setId(10L)
                .setName("iPhone")
                .setBrand("Apple Pro")
                .setState(DeviceState.INACTIVE);

        when(repository.findById(10L)).thenReturn(Optional.of(existingDevice));
        when(mapper.toResponse(existingDevice)).thenReturn(response);

        DeviceResponse result = service.patchDevice(10L, req);

        assertEquals("Apple Pro", existingDevice.getBrand());
        assertEquals(DeviceState.INACTIVE, existingDevice.getState());
        assertEquals(response, result);

        verify(repository).save(existingDevice);
    }

    @Test
    void patchDevice_NotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> service.patchDevice(99L, new PatchDeviceRequest()));
    }

    @Test
    void patchDevice_InUse_Forbidden() {
        existingDevice.setState(DeviceState.IN_USE);

        PatchDeviceRequest req = new PatchDeviceRequest()
                .setBrand("Samsung");

        when(repository.findById(10L)).thenReturn(Optional.of(existingDevice));

        doThrow(new ForbiddenOperationException("Device IN_USE"))
                .when(validator)
                .ensureNotInUseForNameBrandChange(existingDevice, null, "Samsung");

        assertThrows(ForbiddenOperationException.class,
                () -> service.patchDevice(10L, req));

        verify(repository, never()).save(any());
    }

    @Test
    void patchDevice_NameBrandUniqueCheckOnlyIfBothProvided() {
        PatchDeviceRequest req = new PatchDeviceRequest()
                .setName("NewName");

        when(repository.findById(10L)).thenReturn(Optional.of(existingDevice));

        service.patchDevice(10L, req);

        verify(validator)
                .ensureNotInUseForNameBrandChange(existingDevice, "NewName", null);

        verify(validator, never())
                .ensureNameBrandUnique(anyLong(), any(), any());
    }
}
