package com.example.device.api.contoller;

import com.example.device.api.controller.DeleteDeviceController;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.exception.ForbiddenOperationException;
import com.example.device.api.service.DeleteDeviceService;
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
class DeleteDeviceControllerTest {

    @Mock
    private DeleteDeviceService deleteService;

    @InjectMocks
    private DeleteDeviceController deleteDeviceController;

    @Test
    void deleteDevice_Success() {
        Long id = 10L;

        doNothing().when(deleteService).deleteDevice(id);

        ResponseEntity<Void> response = deleteDeviceController.deleteDevice(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(deleteService, times(1)).deleteDevice(id);
    }

    @Test
    void deleteDevice_NotFound() {
        Long id = 99L;

        doThrow(new DeviceNotFoundException(id))
                .when(deleteService).deleteDevice(id);

        DeviceNotFoundException ex = assertThrows(
                DeviceNotFoundException.class,
                () -> deleteDeviceController.deleteDevice(id)
        );

        assertEquals("Device with id=99 was not found", ex.getMessage());
        verify(deleteService, times(1)).deleteDevice(id);
    }

    @Test
    void deleteDevice_Forbidden_InUse() {
        Long id = 55L;

        doThrow(new ForbiddenOperationException("Cannot delete device in use"))
                .when(deleteService).deleteDevice(id);

        ForbiddenOperationException ex = assertThrows(
                ForbiddenOperationException.class,
                () -> deleteDeviceController.deleteDevice(id)
        );

        assertEquals("Cannot delete device in use", ex.getMessage());
        verify(deleteService, times(1)).deleteDevice(id);
    }

    @Test
    void deleteDevice_InternalError() {
        Long id = 5L;

        doThrow(new RuntimeException("Internal failure"))
                .when(deleteService).deleteDevice(id);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> deleteDeviceController.deleteDevice(id)
        );

        assertEquals("Internal failure", ex.getMessage());
        verify(deleteService, times(1)).deleteDevice(id);
    }
}
