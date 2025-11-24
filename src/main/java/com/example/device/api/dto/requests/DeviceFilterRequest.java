package com.example.device.api.dto.requests;

import com.example.device.api.entity.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Request DTO for filtering device fetch queries.
 * Used for GET /device-api endpoint.
 */
@Data

@Accessors(chain = true)
public class DeviceFilterRequest {

    /**
     * Optional brand filter.
     */
    @Size(min = 3, max = 255, message = "Brand must be at least 3 characters")
    @Schema(description = "OPTIONAL: device brand filter",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String brand;

    @Size(min = 3, max = 255, message = "Name must be at least 3 characters")
    @Schema(description = "OPTIONAL: device name filter",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String name;

    /**
     * Optional device state filter.
     * Values: AVAILABLE, IN_USE, INACTIVE.
     */
    @Schema(
            description = "Device state (optional)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            allowableValues = {"AVAILABLE", "IN_USE", "INACTIVE"}
    )
    private DeviceState state;

    /**
     * Limit for number of items returned.
     * Defaults to 100.
     */
    @Min(value = 1, message = "Limit must be at least 1")
    @Schema(description = "Max number of records to return",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
            , example = "100", defaultValue = "100")
    private Integer limit = 100;

    /**
     * Offset for skipping a number of items.
     * Defaults to 0.
     */
    @Min(value = 0, message = "Offset cannot be negative")
    @Schema(description = "Offset for pagination", example = "0",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            defaultValue = "0")
    private Integer offset = 0;
}
