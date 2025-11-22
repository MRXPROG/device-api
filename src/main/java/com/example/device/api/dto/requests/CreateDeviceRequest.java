package com.example.device.api.dto.requests;

import com.example.device.api.entity.DeviceState;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * DTO used for creating a new device.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateDeviceRequest {

    @Schema(description = "Device name", example = "name")
    @NotBlank(message = "Device name cannot be blank")
    @Size(max = 255, min = 3, message = "Device name must have a valid length, max = 255")
    @Pattern(
            regexp = "^[A-Za-z0-9 _\\-]+$",
            message = "Device name contains invalid characters"
    )
    private String name;

    @Schema(description = "Brand of device", example = "Brand")
    @NotBlank(message = "Device brand cannot be blank")
    @Size(max = 255, min = 3, message = "Brand must have a valid length, max = 255")
    @Pattern(
            regexp = "^[A-Za-z0-9 _\\-]+$",
            message = "Brand contains invalid characters"
    )
    private String brand;

    @Schema(
            description = "Device state (optional)",
            allowableValues = {"AVAILABLE", "IN_USE", "INACTIVE"}
    )
    @NotNull(message = "Device state cannot be null")
    private DeviceState state;
}
