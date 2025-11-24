package com.example.device.api.ct;

import com.example.device.api.dto.requests.CreateDeviceRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ImportAutoConfiguration(MockMvcAutoConfiguration.class)
class CreateDeviceControllerComponentTest extends BaseComponentTest {

    private static final String URL = "/device-api/devices";

    @Autowired(required = false)
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    DeviceRepository repo;

    @AfterEach
    void cleanDb() {
        repo.deleteAll();
    }


    @Test
    void createDevice_success() throws Exception {

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("Galaxy S22")
                .setBrand("Samsung")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void createDevice_invalidName_blank() throws Exception {

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("")
                .setBrand("Samsung")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDevice_invalidName_tooShort() throws Exception {

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("ab")
                .setBrand("Samsung")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDevice_invalidBrand_tooShort() throws Exception {

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("Galaxy S22")
                .setBrand("Sa")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDevice_invalidName_regexFail() throws Exception {

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("Bad@Name!")
                .setBrand("Samsung")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDevice_invalidState_null() throws Exception {

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("Galaxy S22")
                .setBrand("Samsung")
                .setState(null);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDevice_conflict_whenAlreadyExists() throws Exception {

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("iPhone X")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void createDevice_conflictDuplicateBrandName() throws Exception {
        repo.save(new Device()
                .setName("XPhone")
                .setBrand("BrandX")
                .setState(DeviceState.AVAILABLE));

        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("XPhone")
                .setBrand("BrandX")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void createDevice_invalidFields_badRequest() throws Exception {
        CreateDeviceRequest req = new CreateDeviceRequest()
                .setName("ab")
                .setBrand("Br")
                .setState(DeviceState.AVAILABLE);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}

