package com.example.device.api.service.impl;

import com.example.device.api.dto.requests.CreateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.Device;
import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.mapper.DeviceMapper;
import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.CreateDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateDeviceServiceImpl implements CreateDeviceService {

    private final DeviceRepository repository;
    private final DeviceMapper mapper;

    @Override
    public DeviceResponse createDevice(CreateDeviceRequest request) {

        String name = normalize(request.getName());
        String brand = normalize(request.getBrand());

        log.info("Creating device: name={}, brand={}", name, brand);

        ensureNotExists(name, brand);

        Device device = buildDevice(request, name, brand);
        repository.save(device);

        log.info("Device created successfully with id={}", device.getId());
        return mapper.toResponse(device);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private void ensureNotExists(String name, String brand) {
        if (repository.existsByNameAndBrand(name, brand)) {
            log.warn("Device already exists: name={}, brand={}", name, brand);
            throw new DeviceAlreadyExistsException(name, brand);
        }
    }

    private Device buildDevice(CreateDeviceRequest request, String name, String brand) {
        Device device = mapper.toEntity(request);
        device.setName(name);
        device.setBrand(brand);
        return device;
    }
}
