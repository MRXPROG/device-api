package com.example.device.api.mapper;

import com.example.device.api.dto.requests.CreateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.entity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between Device entities and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceMapper {

    /**
     * Converts CreateDeviceRequest to Device entity.
     *
     * @param request DTO containing device creation fields.
     * @return Device entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Device toEntity(CreateDeviceRequest request);

    /**
     * Converts Device entity to DeviceResponse DTO.
     *
     * @param device Device entity.
     * @return DeviceResponse DTO.
     */
    DeviceResponse toResponse(Device device);
}
