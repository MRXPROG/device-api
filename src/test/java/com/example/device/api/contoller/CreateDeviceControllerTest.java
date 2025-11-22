package com.example.device.api.contoller;

import com.example.device.api.controller.CreateDeviceController;
import com.example.device.api.dto.requests.CreateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.service.CreateDeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDeviceControllerTest {

    @Mock
    private CreateDeviceService createDeviceService;

    @InjectMocks
    private CreateDeviceController createDeviceController;

    @Test
    void createDevice_Success() {
        CreateDeviceRequest request = sampleRequest();
        DeviceResponse expected = sampleResponse();

        when(createDeviceService.createDevice(request)).thenReturn(expected);

        ResponseEntity<DeviceResponse> actual =
                createDeviceController.createDevice(request);

        assertNotNull(actual);
        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(expected, actual.getBody());

        verify(createDeviceService, times(1)).createDevice(request);
    }

    @Test
    void createDevice_AlreadyExists() {
        CreateDeviceRequest request = sampleRequest();

        when(createDeviceService.createDevice(request))
                .thenThrow(new DeviceAlreadyExistsException("iPhone", "Apple"));

        DeviceAlreadyExistsException ex = assertThrows(
                DeviceAlreadyExistsException.class,
                () -> createDeviceController.createDevice(request)
        );

        assertEquals("Device with name 'iPhone' and brand 'Apple' already exists", ex.getMessage());

        verify(createDeviceService, times(1)).createDevice(request);
    }

    @Test
    void createDevice_InvalidInput() {
        CreateDeviceRequest request = new CreateDeviceRequest()
                .setName("")
                .setBrand("A");

        when(createDeviceService.createDevice(request))
                .thenThrow(new IllegalArgumentException("Invalid device data"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createDeviceController.createDevice(request)
        );

        assertEquals("Invalid device data", ex.getMessage());
        verify(createDeviceService).createDevice(request);
    }

    @Test
    void createDevice_ServiceThrowsUnexpectedException() {
        CreateDeviceRequest request = sampleRequest();

        when(createDeviceService.createDevice(request))
                .thenThrow(new RuntimeException("Internal error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> createDeviceController.createDevice(request)
        );

        assertEquals("Internal error", ex.getMessage());
        verify(createDeviceService).createDevice(request);
    }


    private CreateDeviceRequest sampleRequest() {
        return new CreateDeviceRequest()
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.valueOf("AVAILABLE"));
    }

    private DeviceResponse sampleResponse() {
        return new DeviceResponse()
                .setId(1L)
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.valueOf("AVAILABLE"));
    }
}
