package com.example.device.api.controller;

import com.example.device.api.dto.requests.PatchDeviceRequest;
import com.example.device.api.dto.requests.UpdateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.exception.dto.ErrorDetailsDto;
import com.example.device.api.service.CommandDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Handles update operations (PUT, PATCH) for device resources.
 */
@RestController
@RequestMapping("/device-api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommandDeviceController {

    private final CommandDeviceService commandService;

    /**
     * {@code PUT /device-api/{id}} : Fully update an existing device.
     *
     * <p>Rules enforced:</p>
     * <ul>
     *     <li>Name & brand CANNOT be updated if the device is IN_USE</li>
     *     <li>Brand + name pair must remain unique</li>
     *     <li>createdAt cannot be modified</li>
     * </ul>
     *
     * @param id device ID
     * @param request update payload containing ALL updatable fields
     * @return updated device response
     */
    @Operation(
            operationId = "updateDevice",
            summary = "Fully update an existing device",
            tags = {"Command"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeviceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Device is in-use and cannot change name/brand",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Device not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Device with the same brand + name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            )
    })
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponse> updateDevice(
            @PathVariable Long id,
            @RequestBody UpdateDeviceRequest request
    ) {
        log.info("PUT update device id={}", id);
        return ResponseEntity.ok(commandService.updateDevice(id, request));
    }

    /**
     * {@code PATCH /device-api/{id}} : Partially update an existing device.
     *
     * <p>Rules enforced:</p>
     * <ul>
     *     <li>Only provided fields are updated</li>
     *     <li>Name & brand cannot change if the device is IN_USE</li>
     *     <li>Brand + name must remain unique</li>
     * </ul>
     *
     * @param id device ID
     * @param request partial update payload
     * @return updated device response
     */
    @Operation(
            operationId = "patchDevice",
            summary = "Partially update an existing device",
            tags = {"Command"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device patched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeviceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Device is in-use and cannot change name/brand",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Device not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Device with the same brand + name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            )
    })
    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponse> patchDevice(
            @PathVariable Long id,
            @RequestBody PatchDeviceRequest request
    ) {
        log.info("PATCH update device id={}", id);
        return ResponseEntity.ok(commandService.patchDevice(id, request));
    }
}
