package com.example.device.api.dto.responses;

import com.example.device.api.entity.DeviceState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Response DTO representing a device.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponse {

    private Long id;
    private String name;
    private String brand;
    private DeviceState state;
    private LocalDateTime createdAt;
}
