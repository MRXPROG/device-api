package com.example.device.api.service;

import com.example.device.api.dto.requests.CreateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;

public interface CreateDeviceService {

    /**
     * Creates a new device using the provided request data.
     *
     * <p>
     * The implementation should validate the device data,
     * ensure no duplicate device exists (same name and brand),
     * persist the new device entity to the database,
     * and return the created device information as a DTO.
     * </p>
     *
     * @param request the DTO containing fields required to create a device
     * @return {@link DeviceResponse} representing the newly created device
     */
    DeviceResponse createDevice(CreateDeviceRequest request);
}
