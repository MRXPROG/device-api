package com.example.device.api.service;

import com.example.device.api.dto.requests.CreateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.mapper.DeviceMapper;
import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.impl.CreateDeviceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDeviceServiceImplTest {

    @Mock
    private DeviceRepository repository;

    @Mock
    private DeviceMapper mapper;

    @InjectMocks
    private CreateDeviceServiceImpl service;

    @Test
    void createDevice_Success() {
        CreateDeviceRequest request = sampleRequest();
        Device mappedDevice = sampleDevice();
        DeviceResponse expectedResponse = sampleResponse();

        when(repository.existsByNameAndBrand("iPhone", "Apple")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(mappedDevice);
        when(mapper.toResponse(mappedDevice)).thenReturn(expectedResponse);

        DeviceResponse actual = service.createDevice(request);

        assertNotNull(actual);
        assertEquals(expectedResponse, actual);

        verify(repository).existsByNameAndBrand("iPhone", "Apple");
        verify(repository).save(mappedDevice);
        verify(mapper).toEntity(request);
        verify(mapper).toResponse(mappedDevice);
    }

    @Test
    void createDevice_AlreadyExists() {
        CreateDeviceRequest request = sampleRequest();

        when(repository.existsByNameAndBrand("iPhone", "Apple")).thenReturn(true);

        assertThrows(DeviceAlreadyExistsException.class,
                () -> service.createDevice(request));

        verify(repository).existsByNameAndBrand("iPhone", "Apple");
        verifyNoInteractions(mapper);
        verify(repository, never()).save(any());
    }

    @Test
    void createDevice_NormalizesInput() {
        CreateDeviceRequest request = new CreateDeviceRequest()
                .setName("  iPhone  ")
                .setBrand("  Apple   ")
                .setState(DeviceState.AVAILABLE);

        Device mappedDevice = sampleDevice();
        DeviceResponse expectedResponse = sampleResponse();

        when(repository.existsByNameAndBrand("iPhone", "Apple")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(mappedDevice);
        when(mapper.toResponse(mappedDevice)).thenReturn(expectedResponse);

        DeviceResponse actual = service.createDevice(request);

        assertNotNull(actual);
        assertEquals(expectedResponse, actual);

        verify(repository).existsByNameAndBrand("iPhone", "Apple");
        verify(repository).save(mappedDevice);
    }

    private CreateDeviceRequest sampleRequest() {
        return new CreateDeviceRequest()
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);
    }

    private Device sampleDevice() {
        return new Device()
                .setId(1L)
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);
    }

    private DeviceResponse sampleResponse() {
        return new DeviceResponse()
                .setId(1L)
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);
    }
}

