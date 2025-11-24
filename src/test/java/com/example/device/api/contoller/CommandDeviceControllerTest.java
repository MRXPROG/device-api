package com.example.device.api.contoller;

import com.example.device.api.controller.CommandDeviceController;
import com.example.device.api.dto.requests.PatchDeviceRequest;
import com.example.device.api.dto.requests.UpdateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.exception.ForbiddenOperationException;
import com.example.device.api.service.CommandDeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandDeviceControllerTest {

    @Mock
    private CommandDeviceService commandDeviceService;

    @InjectMocks
    private CommandDeviceController controller;

    @Test
    void updateDevice_Success() {
        Long id = 1L;
        UpdateDeviceRequest request = new UpdateDeviceRequest()
                .setName("iPhone")
                .setBrand("Apple");

        DeviceResponse expected = new DeviceResponse()
                .setId(id)
                .setName("iPhone")
                .setBrand("Apple");

        when(commandDeviceService.updateDevice(id, request)).thenReturn(expected);

        ResponseEntity<DeviceResponse> response = controller.updateDevice(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(commandDeviceService).updateDevice(id, request);
    }

    @Test
    void updateDevice_Forbidden() {
        Long id = 5L;
        UpdateDeviceRequest request = new UpdateDeviceRequest();

        when(commandDeviceService.updateDevice(id, request))
                .thenThrow(new ForbiddenOperationException("Cannot change brand/name"));

        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> controller.updateDevice(id, request)
        );

        assertEquals("Cannot change brand/name", ex.getMessage());
        verify(commandDeviceService).updateDevice(id, request);
    }

    @Test
    void updateDevice_NotFound() {
        Long id = 99L;
        UpdateDeviceRequest request = new UpdateDeviceRequest();

        when(commandDeviceService.updateDevice(id, request))
                .thenThrow(new DeviceNotFoundException(id));

        DeviceNotFoundException ex = assertThrows(
                DeviceNotFoundException.class,
                () -> controller.updateDevice(id, request)
        );

        assertEquals("Device with id=99 was not found", ex.getMessage());
        verify(commandDeviceService).updateDevice(id, request);
    }

    @Test
    void updateDevice_Conflict() {
        Long id = 1L;
        UpdateDeviceRequest request = new UpdateDeviceRequest()
                .setName("iPhone")
                .setBrand("Apple");

        when(commandDeviceService.updateDevice(id, request))
                .thenThrow(new DeviceAlreadyExistsException("iPhone", "Apple"));

        DeviceAlreadyExistsException ex = assertThrows(
                DeviceAlreadyExistsException.class,
                () -> controller.updateDevice(id, request)
        );

        assertEquals(
                "Device with name 'iPhone' and brand 'Apple' already exists",
                ex.getMessage()
        );
        verify(commandDeviceService).updateDevice(id, request);
    }

    @Test
    void updateDevice_InternalError() {
        Long id = 1L;
        UpdateDeviceRequest request = new UpdateDeviceRequest();

        when(commandDeviceService.updateDevice(id, request))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.updateDevice(id, request)
        );

        assertEquals("DB error", ex.getMessage());
        verify(commandDeviceService).updateDevice(id, request);
    }

    @Test
    void patchDevice_Success() {
        Long id = 1L;
        PatchDeviceRequest request = new PatchDeviceRequest()
                .setName("iPhone 13");

        DeviceResponse expected = new DeviceResponse()
                .setId(id)
                .setName("iPhone 13")
                .setBrand("Apple");

        when(commandDeviceService.patchDevice(id, request))
                .thenReturn(expected);

        ResponseEntity<DeviceResponse> actual = controller.patchDevice(id, request);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(expected, actual.getBody());
        verify(commandDeviceService).patchDevice(id, request);
    }

    @Test
    void patchDevice_Forbidden() {
        Long id = 1L;
        PatchDeviceRequest request = new PatchDeviceRequest()
                .setBrand("Samsung");

        when(commandDeviceService.patchDevice(id, request))
                .thenThrow(new ForbiddenOperationException("Device is IN_USE"));

        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> controller.patchDevice(id, request)
        );

        assertEquals("Device is IN_USE", ex.getMessage());
        verify(commandDeviceService).patchDevice(id, request);
    }

    @Test
    void patchDevice_NotFound() {
        Long id = 200L;
        PatchDeviceRequest request = new PatchDeviceRequest();

        when(commandDeviceService.patchDevice(id, request))
                .thenThrow(new DeviceNotFoundException(id));

        DeviceNotFoundException ex = assertThrows(
                DeviceNotFoundException.class,
                () -> controller.patchDevice(id, request)
        );

        assertEquals("Device with id=200 was not found", ex.getMessage());
        verify(commandDeviceService).patchDevice(id, request);
    }

    @Test
    void patchDevice_Conflict() {
        Long id = 1L;
        PatchDeviceRequest request = new PatchDeviceRequest()
                .setName("iPhone")
                .setBrand("Apple");

        when(commandDeviceService.patchDevice(id, request))
                .thenThrow(new DeviceAlreadyExistsException("iPhone", "Apple"));

        DeviceAlreadyExistsException ex = assertThrows(
                DeviceAlreadyExistsException.class,
                () -> controller.patchDevice(id, request)
        );

        assertEquals(
                "Device with name 'iPhone' and brand 'Apple' already exists",
                ex.getMessage()
        );
        verify(commandDeviceService).patchDevice(id, request);
    }

    @Test
    void patchDevice_InternalError() {
        Long id = 1L;
        PatchDeviceRequest request = new PatchDeviceRequest();

        when(commandDeviceService.patchDevice(id, request))
                .thenThrow(new RuntimeException("Internal error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.patchDevice(id, request)
        );

        assertEquals("Internal error", ex.getMessage());
        verify(commandDeviceService).patchDevice(id, request);
    }
}
