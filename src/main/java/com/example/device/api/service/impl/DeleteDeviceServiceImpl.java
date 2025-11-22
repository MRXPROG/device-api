package com.example.device.api.service.impl;

import com.example.device.api.entity.Device;
import com.example.device.api.exception.DeviceNotFoundException;

import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.DeleteDeviceService;
import com.example.device.api.service.DeviceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DeleteDeviceServiceImpl implements DeleteDeviceService {

    private final DeviceRepository repository;
    private final DeviceValidator validator;

    @Override
    public void deleteDevice(Long id) {
        log.info("Deleting device id={}", id);

        Device device = findDeviceOrThrow(id);

        validator.validateDeletable(device);

        repository.delete(device);

        log.info("Device id={} successfully deleted", id);
    }

    /**
     * Fetch device or throw 404.
     */
    private Device findDeviceOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }
}
