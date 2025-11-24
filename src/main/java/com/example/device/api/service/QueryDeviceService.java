package com.example.device.api.service;

import com.example.device.api.dto.requests.DeviceFilterRequest;
import com.example.device.api.dto.responses.DeviceResponse;

import java.util.List;

public interface QueryDeviceService {

    /**
     * Fetch a single device by ID.
     *
     * @param id device ID
     * @return DeviceResponse
     * @throws com.example.device.api.exception.DeviceNotFoundException if no device exists with given ID
     */
    DeviceResponse getDeviceById(Long id);

    /**
     * Fetch devices using optional filters and offset pagination.
     *
     * @param request filtering and pagination parameters
     * @return list of devices
     */
    List<DeviceResponse> getDevices(DeviceFilterRequest request);

    /**
     * Fetch device by unique brand + name pair.
     *
     * @param brand device brand
     * @param name  device name
     * @return DeviceResponse
     */
    DeviceResponse getDeviceByBrandAndName(String brand, String name);
}

