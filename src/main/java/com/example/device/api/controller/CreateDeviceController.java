package com.example.device.api.controller;

import com.example.device.api.dto.requests.CreateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;
import com.example.device.api.exception.dto.ErrorDetailsDto;
import com.example.device.api.service.CreateDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/device-api")
@RequiredArgsConstructor
@Slf4j
public class CreateDeviceController {

    private final CreateDeviceService createDeviceService;

    /**
     * POST /device-api : Create a new device.
     *
     * @param request DTO containing device creation data.
     * @return DeviceResponse with created device details.
     */
    @Operation(
            summary = "Create a new device",
            tags = {"Create"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device created",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorDetailsDto.class))),
            @ApiResponse(responseCode = "409", description = "Device already exists",
                    content = @Content(schema = @Schema(implementation = ErrorDetailsDto.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorDetailsDto.class)))
    })
    @PostMapping(path= "/devices", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponse> createDevice(
            @Valid @RequestBody CreateDeviceRequest request) {

        log.info("Creating device: name={}, brand={}, state={}",
                request.getName(), request.getBrand(),  request.getState());

        DeviceResponse response = createDeviceService.createDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
