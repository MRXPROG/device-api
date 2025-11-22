package com.example.device.api.service;

import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.exception.DeviceAlreadyExistsException;
import com.example.device.api.exception.ForbiddenOperationException;
import com.example.device.api.repository.DeviceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceValidatorTest {

    @Mock
    private DeviceRepository repository;

    private DeviceValidator validator;

    private Device device;

    @BeforeEach
    void setup() {
        validator = new DeviceValidator(repository);

        device = new Device()
                .setId(10L)
                .setName("iPhone")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);
    }

    @Test
    void ensureNotInUseForNameBrandChange_DeviceNotInUse_NoException() {
        device.setState(DeviceState.AVAILABLE);

        assertDoesNotThrow(() -> validator.ensureNotInUseForNameBrandChange(device, "New", "Brand"));
    }

    @Test
    void ensureNotInUseForNameBrandChange_NameChanged_Forbidden() {
        device.setState(DeviceState.IN_USE);

        assertThrows(
                ForbiddenOperationException.class,
                () -> validator.ensureNotInUseForNameBrandChange(device, "NewName", "Apple")
        );
    }

    @Test
    void ensureNotInUseForNameBrandChange_BrandChanged_Forbidden() {
        device.setState(DeviceState.IN_USE);

        assertThrows(
                ForbiddenOperationException.class,
                () -> validator.ensureNotInUseForNameBrandChange(device, "iPhone", "Samsung")
        );
    }

    @Test
    void ensureNotInUseForNameBrandChange_NoChanges_WhenInUse_NoException() {
        device.setState(DeviceState.IN_USE);

        assertDoesNotThrow(() ->
                validator.ensureNotInUseForNameBrandChange(device, "iPhone", "Apple")
        );
    }

    @Test
    void ensureNameBrandUnique_ExistingRecordWithDifferentId_Throws() {
        Device existing = new Device()
                .setId(20L)
                .setName("iPhone")
                .setBrand("Apple");

        when(repository.findByBrandAndName("Apple", "iPhone"))
                .thenReturn(Optional.of(existing));

        assertThrows(
                DeviceAlreadyExistsException.class,
                () -> validator.ensureNameBrandUnique(10L, "iPhone", "Apple")
        );
    }

    @Test
    void ensureNameBrandUnique_ExistingRecordSameId_NoException() {
        Device existing = new Device()
                .setId(10L)
                .setName("iPhone")
                .setBrand("Apple");

        when(repository.findByBrandAndName("Apple", "iPhone"))
                .thenReturn(Optional.of(existing));

        assertDoesNotThrow(() ->
                validator.ensureNameBrandUnique(10L, "iPhone", "Apple")
        );
    }

    @Test
    void ensureNameBrandUnique_NoRecordFound_NoException() {
        when(repository.findByBrandAndName("Apple", "iPhone"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
                validator.ensureNameBrandUnique(10L, "iPhone", "Apple")
        );
    }

    @Test
    void validateDeletable_ForbiddenWhenInUse() {
        device.setState(DeviceState.IN_USE);

        assertThrows(
                ForbiddenOperationException.class,
                () -> validator.validateDeletable(device)
        );
    }

    @Test
    void validateDeletable_NotInUse_NoException() {
        device.setState(DeviceState.AVAILABLE);

        assertDoesNotThrow(() -> validator.validateDeletable(device));
    }
}
