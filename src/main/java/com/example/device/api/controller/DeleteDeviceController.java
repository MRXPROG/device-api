package com.example.device.api.controller;

import com.example.device.api.exception.dto.ErrorDetailsDto;
import com.example.device.api.service.DeleteDeviceService;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Handles DELETE operations for devices.
 * Provides safe deletion of devices while enforcing domain rules.
 */
@RestController
@RequestMapping("/device-api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DeleteDeviceController {

    private final DeleteDeviceService deleteService;

    /**
     * {@code DELETE /device-api/{id}} : Delete a device by ID.
     *
     * <p>Domain rules enforced:</p>
     * <ul>
     *     <li>Device must exist, otherwise 404 is returned</li>
     *     <li>Device cannot be deleted if its state is IN_USE (403)</li>
     * </ul>
     *
     * @param id ID of the device to delete
     * @return HTTP 204 No Content if deletion is successful
     */
    @Operation(
            operationId = "deleteDevice",
            summary = "Delete a device by ID",
            description = "Deletes a device unless it is currently in use.",
            tags = {"Devices"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Device successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Device is currently in use and cannot be deleted",
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
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetailsDto.class)
                    )
            )
    })
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {

        log.info("Request to delete device id={}", id);

        deleteService.deleteDevice(id);

        return ResponseEntity.noContent().build();
    }
}
