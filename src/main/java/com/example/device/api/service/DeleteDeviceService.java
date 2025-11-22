package com.example.device.api.service;

public interface DeleteDeviceService {

    /**
     * Deletes a device by ID.
     *
     * @param id device ID
     * @throws com.example.device.api.exception.DeviceNotFoundException if no such device exists
     * @throws com.example.device.api.exception.ForbiddenOperationException if device is IN_USE
     */
    void deleteDevice(Long id);
}
