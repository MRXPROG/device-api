package com.example.device.api.service.impl;

import com.example.device.api.dto.requests.DeviceFilterRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceNotFoundException;
import com.example.device.api.mapper.DeviceMapper;
import com.example.device.api.repository.DeviceRepository;
import com.example.device.api.service.QueryDeviceService;
import com.example.device.api.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.device.api.utils.StringUtil.normalize;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryDeviceServiceImpl implements QueryDeviceService {

    private final DeviceRepository repository;
    private final DeviceMapper mapper;

    @Override
    public DeviceResponse getDeviceById(Long id) {
        log.info("Query: get device by id={}", id);

        Device device = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        return mapper.toResponse(device);
    }

    @Override
    public DeviceResponse getDeviceByBrandAndName(String brand, String name) {

        String normalizedBrand = normalize(brand);
        String normalizedName = normalize(name);

        log.info("Query: get device by brand='{}' and name='{}'",
                normalizedBrand, normalizedName);

        Device device = repository.findByBrandAndName(normalizedBrand, normalizedName)
                .orElseThrow(() ->
                        new DeviceNotFoundException(
                                "Device with brand='%s' and name='%s' not found"
                                        .formatted(normalizedBrand, normalizedName)
                        )
                );

        return mapper.toResponse(device);
    }

    @Override
    public List<DeviceResponse> getDevices(DeviceFilterRequest request) {

        String brand = normalize(request.getBrand());
        String name = normalize(request.getName());
        DeviceState state = request.getState();

        int limit = request.getLimit();
        int offset = request.getOffset();

        log.info("Query: filter devices brand='{}', name='{}', state={}, limit={}, offset={}",
                brand, name, state, limit, offset);

        Pageable pageable = PaginationUtils.offsetPagination(offset, limit);

        List<Device> devices = repository.findFiltered(brand, name, state, pageable);

        if (devices.isEmpty()) {
            throw new DeviceNotFoundException("No devices found matching filters");
        }
        return devices.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
