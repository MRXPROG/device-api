package com.example.device.api.service;

import com.example.device.api.dto.requests.DeviceFilterRequest;
import com.example.device.api.entity.Device;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.mapper.DeviceMapper;
import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.impl.QueryDeviceServiceImpl;
import com.example.device.api.utils.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.DeviceState;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryDeviceServiceImplTest {

    @Mock
    private DeviceRepository repository;

    @Mock
    private DeviceMapper mapper;

    @InjectMocks
    private QueryDeviceServiceImpl queryDeviceService;

    @Test
    void getDeviceById_Success() {
        Device device = sampleDevice();
        DeviceResponse response = sampleResponse();

        when(repository.findById(1L)).thenReturn(Optional.of(device));
        when(mapper.toResponse(device)).thenReturn(response);

        DeviceResponse actual = queryDeviceService.getDeviceById(1L);

        assertNotNull(actual);
        assertEquals(response, actual);
        verify(repository).findById(1L);
        verify(mapper).toResponse(device);
    }

    @Test
    void getDeviceById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> queryDeviceService.getDeviceById(1L));

        verify(repository).findById(1L);
        verifyNoInteractions(mapper);
    }


    @Test
    void getDeviceByBrandAndName_Success() {
        Device device = sampleDevice();
        DeviceResponse response = sampleResponse();

        when(repository.findByBrandAndName("Apple", "iPhone"))
                .thenReturn(Optional.of(device));
        when(mapper.toResponse(device)).thenReturn(response);

        DeviceResponse actual =
                queryDeviceService.getDeviceByBrandAndName(" Apple ", "  iPhone ");

        assertEquals(response, actual);
        verify(repository).findByBrandAndName("Apple", "iPhone");
    }

    @Test
    void getDeviceByBrandAndName_NotFound() {
        when(repository.findByBrandAndName("Apple", "iPhone"))
                .thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class,
                () -> queryDeviceService.getDeviceByBrandAndName("Apple", "iPhone"));
    }

    @Test
    void getDevices_Success() {
        DeviceFilterRequest request = new DeviceFilterRequest()
                .setBrand("Apple")
                .setName("iPhone")
                .setState(DeviceState.AVAILABLE)
                .setLimit(10)
                .setOffset(0);

        Device d1 = sampleDevice();
        DeviceResponse r1 = sampleResponse();

        Pageable pageable = PaginationUtils.offsetPagination(0, 10);

        when(repository.findFiltered("Apple", "iPhone", DeviceState.AVAILABLE, pageable))
                .thenReturn(List.of(d1));
        when(mapper.toResponse(d1)).thenReturn(r1);

        List<DeviceResponse> result = queryDeviceService.getDevices(request);

        assertEquals(1, result.size());
        assertEquals(r1, result.getFirst());

        verify(repository).findFiltered("Apple", "iPhone", DeviceState.AVAILABLE, pageable);
        verify(mapper).toResponse(d1);
    }

    @Test
    void getDevices_EmptyResults_NotFoundException() {
        DeviceFilterRequest request = new DeviceFilterRequest()
                .setBrand("Apple")
                .setName("iPhone")
                .setState(DeviceState.AVAILABLE)
                .setLimit(10)
                .setOffset(0);

        Pageable pageable = PaginationUtils.offsetPagination(0, 10);

        when(repository.findFiltered("Apple", "iPhone", DeviceState.AVAILABLE, pageable))
                .thenReturn(List.of());

        assertThrows(DeviceNotFoundException.class,
                () -> queryDeviceService.getDevices(request));

        verify(repository).findFiltered("Apple", "iPhone", DeviceState.AVAILABLE, pageable);
        verifyNoInteractions(mapper);
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
