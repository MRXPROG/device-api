package com.example.device.api.service;

import com.example.device.api.entity.Device;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.exception.ForbiddenOperationException;
import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.impl.DeleteDeviceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import com.example.device.api.entity.DeviceState;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteDeviceServiceImplTest {

    @Mock
    private DeviceRepository repository;

    @Mock
    private DeviceValidator validator;

    @InjectMocks
    private DeleteDeviceServiceImpl deleteService;

    @Test
    void deleteDevice_Success() {
        Device device = sampleDevice();

        when(repository.findById(1L)).thenReturn(Optional.of(device));

        doNothing().when(validator).validateDeletable(device);

        deleteService.deleteDevice(1L);

        verify(repository).findById(1L);
        verify(validator).validateDeletable(device);
        verify(repository).delete(device);
    }

    @Test
    void deleteDevice_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> deleteService.deleteDevice(1L));

        verify(repository).findById(1L);
        verifyNoInteractions(validator);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteDevice_InUse_Forbidden() {
        Device device = sampleDevice();
        device.setState(DeviceState.IN_USE);

        when(repository.findById(1L)).thenReturn(Optional.of(device));

        doThrow(new ForbiddenOperationException("Device in use"))
                .when(validator).validateDeletable(device);

        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> deleteService.deleteDevice(1L)
        );

        assertEquals("Device in use", ex.getMessage());

        verify(repository).findById(1L);
        verify(validator).validateDeletable(device);
        verify(repository, never()).delete(any());
    }

    private Device sampleDevice() {
        return new Device()
                .setId(1L)
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);
    }
}
