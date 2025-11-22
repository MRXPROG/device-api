package com.example.device.api.service;

import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.exception.ForbiddenOperationException;
import com.example.device.api.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceValidator {

    private final DeviceRepository repository;

    /**
     * Ensures device is NOT IN_USE when trying to update name or brand.
     */
    public void ensureNotInUseForNameBrandChange(Device device, String newName, String newBrand) {
        if (device.getState() != DeviceState.IN_USE) return;

        boolean nameChanged = newName != null && !newName.equals(device.getName());
        boolean brandChanged = newBrand != null && !newBrand.equals(device.getBrand());

        if (nameChanged) {
            throw new ForbiddenOperationException(
                    "Cannot change device name while device is IN_USE"
            );
        }
        if (brandChanged) {
            throw new ForbiddenOperationException(
                    "Cannot change device brand while device is IN_USE"
            );
        }
    }

    /**
     * Ensures that brand + name pair is unique.
     */
    public void ensureNameBrandUnique(Long updatingId, String name, String brand) {

        repository.findByBrandAndName(brand, name)
                .filter(existing -> !existing.getId().equals(updatingId))
                .ifPresent(existing -> {
                    throw new DeviceAlreadyExistsException(name, brand);
                });
    }

    /**
     * Ensure device is not IN_USE.
     */
    public void validateDeletable(Device device) {
        if (device.getState() == DeviceState.IN_USE) {
            log.warn("Cannot delete device id={} because state={}", device.getId(), device.getState());
            throw new ForbiddenOperationException(
                    "Device in state IN_USE cannot be deleted"
            );
        }
    }
}
