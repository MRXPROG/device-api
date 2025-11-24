package com.example.device.api.ct;

import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.repository.DeviceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ImportAutoConfiguration(org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration.class)
class DeleteDeviceControllerComponentTest extends BaseComponentTest {

    private static final String URL = "/device-api/";

    @Autowired(required = false)
    MockMvc mockMvc;
    @Autowired
    DeviceRepository repo;

    @AfterEach
    void cleanDb() {
        repo.deleteAll();
    }

    @Test
    void deleteDevice_success() throws Exception {

        Device entity = new Device()
                .setName("Pixel 7")
                .setBrand("Google")
                .setState(DeviceState.AVAILABLE);

        entity = repo.save(entity);

        mockMvc.perform(delete(URL + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDevice_notFound() throws Exception {

        long nonExistingId = 999999L;

        mockMvc.perform(delete(URL + nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDevice_inUse_forbidden() throws Exception {

        Device entity = new Device()
                .setName("MacBook Pro")
                .setBrand("Apple")
                .setState(DeviceState.IN_USE);

        entity = repo.save(entity);

        mockMvc.perform(delete(URL + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteDevice_forbiddenWhenInUse() throws Exception {
        Device dev = repo.save(new Device()
                .setName("Locked")
                .setBrand("LockedBrand")
                .setState(DeviceState.IN_USE));

        mockMvc.perform(delete(URL + "/" + dev.getId()))
                .andExpect(status().isForbidden());
    }
}
