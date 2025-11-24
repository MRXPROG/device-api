package com.example.device.api.dto.requests;

import com.example.device.api.entity.DeviceState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UpdateDeviceRequest {

    @Schema(description = "Device name", example = "Name")
    @NotBlank(message = "Device name cannot be blank")
    @Size(min = 3, message = "Device name must have at least 3 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 _\\-]+$",
            message = "Device name contains invalid characters"
    )
    private String name;

    @Schema(description = "Brand of device", example = "Brand")
    @NotBlank(message = "Brand cannot be blank")
    @Size(min = 3, message = "Brand must have at least 3 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 _\\-]+$",
            message = "Brand contains invalid characters"
    )
    private String brand;

    @NotNull(message = "State is required")
    @Schema(
            description = "Device state (optional)",
            allowableValues = {"AVAILABLE", "IN_USE", "INACTIVE"}
    )
    private DeviceState state;

    @JsonIgnore
    private LocalDateTime createdAt;

}
