package com.example.device.api.exception;

import lombok.Getter;

/**
 * Thrown when an attempt is made to create a device
 * that already exists (same name + brand).
 */
@Getter
public class DeviceAlreadyExistsException extends RuntimeException {

    private final String name;
    private final String brand;

    public DeviceAlreadyExistsException(String name, String brand) {
        super(String.format("Device with name '%s' and brand '%s' already exists", name, brand));
        this.name = name;
        this.brand = brand;
    }

}
