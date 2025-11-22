package com.example.device.api.contoller;

import com.example.device.api.controller.QueryDeviceController;
import com.example.device.api.dto.requests.DeviceFilterRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.service.QueryDeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryDeviceControllerTest {

    @Mock
    private QueryDeviceService queryDeviceService;

    @InjectMocks
    private QueryDeviceController queryDeviceController;

    @Test
    void getDeviceById_Success() {
        Long id = 1L;
        DeviceResponse response = new DeviceResponse().setId(id).setName("iPhone").setBrand("Apple");

        when(queryDeviceService.getDeviceById(id)).thenReturn(response);

        ResponseEntity<DeviceResponse> actual = queryDeviceController.getDeviceById(id);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(response, actual.getBody());
        verify(queryDeviceService, times(1)).getDeviceById(id);
    }

    @Test
    void getDeviceById_NotFound() {
        Long id = 100L;

        when(queryDeviceService.getDeviceById(id))
                .thenThrow(new DeviceNotFoundException(id));

        DeviceNotFoundException ex = assertThrows(
                DeviceNotFoundException.class,
                () -> queryDeviceController.getDeviceById(id)
        );

        assertEquals("Device with id=100 was not found", ex.getMessage());
        verify(queryDeviceService, times(1)).getDeviceById(id);
    }

    @Test
    void getDevices_Success() {
        DeviceFilterRequest request = new DeviceFilterRequest()
                .setBrand("Apple")
                .setName("iPhone")
                .setState(DeviceState.AVAILABLE)
                .setLimit(10)
                .setOffset(0);

        List<DeviceResponse> expected = List.of(
                new DeviceResponse().setId(1L).setBrand("Apple").setName("iPhone")
        );

        when(queryDeviceService.getDevices(request)).thenReturn(expected);

        ResponseEntity<List<DeviceResponse>> actual =
                queryDeviceController.getDevices(request);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(expected, actual.getBody());
        verify(queryDeviceService, times(1)).getDevices(request);
    }

    @Test
    void getDevices_EmptyResults() {
        DeviceFilterRequest request = new DeviceFilterRequest();

        when(queryDeviceService.getDevices(request))
                .thenReturn(List.of());

        ResponseEntity<List<DeviceResponse>> actual =
                queryDeviceController.getDevices(request);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().isEmpty());
        verify(queryDeviceService, times(1)).getDevices(request);
    }

    @Test
    void getDevices_ServiceError() {
        DeviceFilterRequest request = new DeviceFilterRequest();

        when(queryDeviceService.getDevices(request))
                .thenThrow(new RuntimeException("DB failure"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> queryDeviceController.getDevices(request)
        );

        assertEquals("DB failure", ex.getMessage());
        verify(queryDeviceService, times(1)).getDevices(request);
    }

    @Test
    void getDeviceByBrandAndName_Success() {
        String brand = "Apple";
        String name = "iPhone";

        DeviceResponse expected = new DeviceResponse()
                .setId(1L).setBrand(brand).setName(name);

        when(queryDeviceService.getDeviceByBrandAndName(brand, name))
                .thenReturn(expected);

        ResponseEntity<DeviceResponse> actual =
                queryDeviceController.getDeviceByBrandAndName(brand, name);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(expected, actual.getBody());
        verify(queryDeviceService, times(1))
                .getDeviceByBrandAndName(brand, name);
    }

    @Test
    void getDeviceByBrandAndName_NotFound() {
        String brand = "Samsung";
        String name = "Galaxy";

        when(queryDeviceService.getDeviceByBrandAndName(brand, name))
                .thenThrow(new DeviceNotFoundException("Device with brand='Samsung' and name='Galaxy' not found"));

        DeviceNotFoundException ex = assertThrows(
                DeviceNotFoundException.class,
                () -> queryDeviceController.getDeviceByBrandAndName(brand, name)
        );

        assertEquals("Device with brand='Samsung' and name='Galaxy' not found", ex.getMessage());
        verify(queryDeviceService, times(1))
                .getDeviceByBrandAndName(brand, name);
    }

    @Test
    void getDeviceByBrandAndName_InternalError() {
        String brand = "Xiaomi";
        String name = "Mi 9";

        when(queryDeviceService.getDeviceByBrandAndName(brand, name))
                .thenThrow(new RuntimeException("Internal error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> queryDeviceController.getDeviceByBrandAndName(brand, name)
        );

        assertEquals("Internal error", ex.getMessage());
        verify(queryDeviceService, times(1))
                .getDeviceByBrandAndName(brand, name);
    }
}
