package com.example.device.api.controller;

import com.example.device.api.dto.requests.DeviceFilterRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.exception.dto.ErrorDetailsDto;
import com.example.device.api.service.QueryDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Handles all read-only operations for devices (GET endpoints).
 * Supports filtering, offset-based pagination, and direct lookups by ID or brand/name pair.
 */
@RestController
@RequestMapping("/device-api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class QueryDeviceController {

    private final QueryDeviceService queryDeviceService;

    /**
     * {@code GET /device-api/{id}} : Fetch a device by its ID.
     *
     * @param id ID of the device to fetch.
     * @return a {@link DeviceResponse} if found, or 404 error response.
     */
    @Operation(
            operationId = "getDeviceById",
            summary = "Fetch a device by ID",
            tags = {"Query"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeviceResponse.class)
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
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            )
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable Long id) {
        log.info("Fetching device by id={}", id);
        return ResponseEntity.ok(queryDeviceService.getDeviceById(id));
    }

    /**
     * {@code GET /device-api/devices} :
     * Fetch devices using optional filters (brand, name, state) and offset-based pagination.
     *
     * <p>If pagination parameters are not provided:
     * <ul>
     *     <li>limit = 100 (default)</li>
     *     <li>offset = 0 (default)</li>
     * </ul>
     *
     * @param request filtering + pagination parameters (brand, name, state, limit, offset)
     * @return list of devices matching filters and paging rules
     */
    @Operation(
            operationId = "getDevices",
            summary = "Fetch devices with optional filters and offset pagination",
            tags = {"Query"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of devices",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DeviceResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            )
    })
    @GetMapping(value = "/devices",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DeviceResponse>> getDevices(
            @ParameterObject DeviceFilterRequest request) {

        log.info(
                "Fetching devices with brand={}, name={}, state={}, limit={}, offset={}",
                request.getBrand(),
                request.getName(),
                request.getState(),
                request.getLimit(),
                request.getOffset()
        );

        return ResponseEntity.ok(queryDeviceService.getDevices(request));
    }

    /**
     * {@code GET /device-api/search} :
     * Fetch a device by a unique brand + name combination.
     *
     * @param brand device brand (required)
     * @param name  device name (required)
     * @return a single {@link DeviceResponse} or 404 if not found
     */
    @Operation(
            operationId = "getDeviceByBrandAndName",
            summary = "Fetch a device by brand and name (unique pair)",
            tags = {"Query"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeviceResponse.class)
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
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            )
    })
    @GetMapping(value = "/search", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponse> getDeviceByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name
    ) {
        log.info("Fetching device by brand={} and name={}", brand, name);
        return ResponseEntity.ok(queryDeviceService.getDeviceByBrandAndName(brand, name));
    }
}

