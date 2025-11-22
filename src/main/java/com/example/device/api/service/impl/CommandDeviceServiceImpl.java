package com.example.device.api.service.impl;

import com.example.device.api.dto.requests.PatchDeviceRequest;
import com.example.device.api.dto.requests.UpdateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.mapper.DeviceMapper;
import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.CommandDeviceService;
import com.example.device.api.service.DeviceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommandDeviceServiceImpl implements CommandDeviceService {

    private final DeviceRepository repository;
    private final DeviceMapper mapper;
    private final DeviceValidator validator;

    @Override
    public DeviceResponse updateDevice(Long id, UpdateDeviceRequest request) {

        Device device = findOrThrow(id);

        String newName = normalize(request.getName());
        String newBrand = normalize(request.getBrand());

        validator.ensureNotInUseForNameBrandChange(device, newName, newBrand);
        validator.ensureNameBrandUnique(id, newName, newBrand);

        applyFullUpdate(device, request, newName, newBrand);

        repository.save(device);

        log.info("Device id={} fully updated", id);
        return mapper.toResponse(device);
    }

    @Override
    public DeviceResponse patchDevice(Long id, PatchDeviceRequest request) {

        Device device = findOrThrow(id);

        String newName = normalize(request.getName());
        String newBrand = normalize(request.getBrand());
        DeviceState newState = request.getState();

        validator.ensureNotInUseForNameBrandChange(device, newName, newBrand);

        if (newName != null && newBrand != null) {
            validator.ensureNameBrandUnique(id, newName, newBrand);
        }

        applyPartialUpdate(device, newName, newBrand, newState);

        repository.save(device);

        log.info("Device id={} patched", id);
        return mapper.toResponse(device);
    }

    private Device findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private void applyFullUpdate(Device device, UpdateDeviceRequest req,
                                 String newName, String newBrand) {
        device.setName(newName);
        device.setBrand(newBrand);
        device.setState(req.getState());
    }

    private void applyPartialUpdate(Device device,
                                    String newName,
                                    String newBrand,
                                    DeviceState newState) {
        if (newName != null) device.setName(newName);
        if (newBrand != null) device.setBrand(newBrand);
        if (newState != null) device.setState(newState);
    }
}

