package com.example.device.api.exception;


public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(Long id) {
        super("Device with id=" + id + " was not found");
    }
    public DeviceNotFoundException(String message) {
        super(message);
    }
}
