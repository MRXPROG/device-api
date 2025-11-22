package com.example.device.api.dto.requests;

import com.example.device.api.entity.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PatchDeviceRequest {

    @Schema(description = "Device name", example = "Name")
    @Size(min = 3, message = "Device name must have at least 3 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 _\\-]+$",
            message = "Device name contains invalid characters"
    )
    private String name;

    @Schema(description = "Brand of device", example = "Brand")
    @Size(min = 3, message = "Brand must have at least 3 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 _\\-]+$",
            message = "Brand contains invalid characters"
    )
    private String brand;

    private DeviceState state;
}
