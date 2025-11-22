package com.example.device.api.service;

import com.example.device.api.dto.requests.PatchDeviceRequest;
import com.example.device.api.dto.requests.UpdateDeviceRequest;
import com.example.device.api.dto.responses.DeviceResponse;

/**
 * Service interface responsible for handling update operations (PUT and PATCH)
 * on Device resources.
 *
 * <p>This service enforces the following domain rules:</p>
 * <ul>
 *     <li><b>Name</b> and <b>brand</b> cannot be modified when the device is in state {@code IN_USE}</li>
 *     <li>The pair (brand + name) must remain unique across all devices</li>
 *     <li>The field {@code createdAt} cannot be updated</li>
 * </ul>
 *
 * <p>The service provides two types of update operations:</p>
 * <ul>
 *     <li><b>Full update (PUT)</b> — all updatable fields must be provided</li>
 *     <li><b>Partial update (PATCH)</b> — only provided fields are updated</li>
 * </ul>
 */
public interface CommandDeviceService {

    /**
     * Performs a <b>full update</b> of an existing device.
     *
     * <p>Domain rules enforced:</p>
     * <ul>
     *     <li>If the device does not exist, a {@link com.example.device.api.exception.DeviceNotFoundException}
     *     is thrown
     *     </li>
     *     <li>If the device is {@code IN_USE}, name and brand cannot be changed</li>
     *     <li>If the updated (brand + name) pair already exists, a
     *         {@link com.example.device.api.exception.DeviceAlreadyExistsException} is thrown</li>
     *     <li>{@code createdAt} is immutable and will not be changed even if provided</li>
     * </ul>
     *
     * @param id      ID of the device to update
     * @param request payload containing the full set of updated values
     * @return the updated {@link DeviceResponse}
     *
     * @throws com.example.device.api.exception.DeviceNotFoundException
     * if the device with the given ID does not exist
     * @throws com.example.device.api.exception.ForbiddenOperationException
     * if name/brand change is attempted while device is IN_USE
     * @throws com.example.device.api.exception.DeviceAlreadyExistsException
     * if another device already exists with the same brand + name
     */
    DeviceResponse updateDevice(Long id, UpdateDeviceRequest request);

    /**
     * Performs a <b>partial update</b> of an existing device.
     *
     * <p>Domain rules enforced:</p>
     * <ul>
     *     <li>Only provided fields are updated</li>
     *     <li>If the device does not exist, a {@link com.example.device.api.exception.DeviceNotFoundException }
     *     is thrown</li>
     *     <li>If the device is {@code IN_USE}, name and brand cannot be changed</li>
     *     <li>If both name and brand are provided and the pair already exists elsewhere,
     *         a {@link com.example.device.api.exception.DeviceAlreadyExistsException  } is thrown</li>
     * </ul>
     *
     * @param id      ID of the device to patch
     * @param request payload with optional updated fields
     * @return the updated {@link DeviceResponse}
     *
     * @throws com.example.device.api.exception.DeviceAlreadyExistsException
     * if the device with the given ID does not exist
     * @throws com.example.device.api.exception.ForbiddenOperationException
     * if name/brand change is attempted while device is IN_USE
     * @throws com.example.device.api.exception.DeviceAlreadyExistsException
     * if the updated brand + name combination already exists
     */
    DeviceResponse patchDevice(Long id, PatchDeviceRequest request);
}
