package com.example.device.api.ct;

import com.example.device.api.dto.requests.PatchDeviceRequest;
import com.example.device.api.dto.requests.UpdateDeviceRequest;
import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ImportAutoConfiguration(MockMvcAutoConfiguration.class)
class CommandDeviceControllerComponentTest extends BaseComponentTest {

    private static final String URL = "/device-api";

    @Autowired(required = false)
    MockMvc mockMvc;
    @Autowired
    DeviceRepository repo;
    @Autowired
    ObjectMapper mapper;

    @AfterEach
    void cleanDb() {
        repo.deleteAll();
    }

    @Test
    void updateDevice_success() throws Exception {
        Device dev = repo.save(new Device()
                .setName("OldName")
                .setBrand("OldBrand")
                .setState(DeviceState.AVAILABLE)
        );

        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("NewName")
                .setBrand("NewBrand")
                .setState(DeviceState.IN_USE);

        mockMvc.perform(put(URL + "/" + dev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.brand").value("NewBrand"));
    }

    @Test
    void updateDevice_notFound() throws Exception {
        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("ValidName")
                .setBrand("ValidBrand")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(put(URL + "/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDevice_forbiddenWhenInUseChangingNameBrand() throws Exception {
        Device dev = repo.save(new Device()
                .setName("Locked")
                .setBrand("LockedBrand")
                .setState(DeviceState.IN_USE)
        );

        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("NewName")
                .setBrand("NewBrand")
                .setState(DeviceState.IN_USE);

        mockMvc.perform(put(URL + "/" + dev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateDevice_conflictSameBrandAndNameExists() throws Exception {
        repo.save(new Device()
                .setName("ExistingName")
                .setBrand("ExistingBrand")
                .setState(DeviceState.AVAILABLE)
        );

        Device target = repo.save(new Device()
                .setName("OldName")
                .setBrand("OldBrand")
                .setState(DeviceState.AVAILABLE)
        );

        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("ExistingName")
                .setBrand("ExistingBrand")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(put(URL + "/" + target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void patchDevice_success() throws Exception {
        Device dev = repo.save(new Device()
                .setName("Original")
                .setBrand("OriginalBrand")
                .setState(DeviceState.AVAILABLE)
        );

        PatchDeviceRequest req = new PatchDeviceRequest();
        req.setName("UpdatedName");

        mockMvc.perform(patch(URL + "/" + dev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"));
    }

    @Test
    void patchDevice_notFound() throws Exception {
        PatchDeviceRequest req = new PatchDeviceRequest();
        req.setName("New");

        mockMvc.perform(patch(URL + "/98765")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchDevice_forbiddenWhenInUseChangingNameBrand() throws Exception {
        Device dev = repo.save(new Device()
                .setName("Locked")
                .setBrand("LockedBrand")
                .setState(DeviceState.IN_USE)
        );

        PatchDeviceRequest req = new PatchDeviceRequest();
        req.setName("NewName");

        mockMvc.perform(patch(URL + "/" + dev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchDevice_conflictBrandNameExists() throws Exception {
        repo.save(new Device()
                .setName("ConflictName")
                .setBrand("ConflictBrand")
                .setState(DeviceState.AVAILABLE)
        );

        Device dev = repo.save(new Device()
                .setName("Base")
                .setBrand("BaseBrand")
                .setState(DeviceState.AVAILABLE)
        );

        PatchDeviceRequest req = new PatchDeviceRequest();
        req.setName("ConflictName");
        req.setBrand("ConflictBrand");

        mockMvc.perform(patch(URL + "/" + dev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateDevice_createdAtIgnored() throws Exception {
        Device dev = repo.save(new Device()
                .setName("Old")
                .setBrand("OldBrand")
                .setState(DeviceState.AVAILABLE));

        LocalDateTime original = dev.getCreatedAt();

        UpdateDeviceRequest req = new UpdateDeviceRequest()
                .setName("New")
                .setBrand("NewBrand")
                .setState(DeviceState.AVAILABLE);
        mockMvc.perform(put(URL + "/" + dev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        Device updated = repo.findById(dev.getId()).orElseThrow();
        assertThat(updated.getCreatedAt()).isEqualTo(original);
    }

    @Test
    void patchDevice_invalidCharacters_badRequest() throws Exception {
        Device dev = repo.save(new Device()
                .setName("Valid")
                .setBrand("ValidBrand")
                .setState(DeviceState.AVAILABLE));

        PatchDeviceRequest req = new PatchDeviceRequest();
        req.setName("@@@");

        mockMvc.perform(patch(URL + "/" + dev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
