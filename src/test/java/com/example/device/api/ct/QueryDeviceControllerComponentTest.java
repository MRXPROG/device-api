package com.example.device.api.ct;

import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import com.example.device.api.repository.DeviceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ImportAutoConfiguration(MockMvcAutoConfiguration.class)
class QueryDeviceControllerComponentTest extends BaseComponentTest {

    private static final String URL = "/device-api";
    @Autowired(required = false)
    MockMvc mockMvc;
    @Autowired
    DeviceRepository repo;

    @AfterEach
    void cleanDb() {
        repo.deleteAll();
    }

    @Test
    void getDeviceById_success() throws Exception {
        Device d = repo.save(new Device()
                .setName("iPhone X")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE)
        );

        mockMvc.perform(get(URL + "/" + d.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone X"))
                .andExpect(jsonPath("$.brand").value("Apple"));
    }

    @Test
    void getDeviceById_notFound() throws Exception {
        mockMvc.perform(get(URL + "/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDevices_all() throws Exception {
        repo.save(new Device().setName("A1").setBrand("B1").setState(DeviceState.AVAILABLE));
        repo.save(new Device().setName("A2").setBrand("B2").setState(DeviceState.IN_USE));

        mockMvc.perform(get(URL + "/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getDevices_filterByBrand() throws Exception {
        repo.save(new Device().setName("N1").setBrand("Samsung").setState(DeviceState.AVAILABLE));
        repo.save(new Device().setName("N2").setBrand("Apple").setState(DeviceState.AVAILABLE));

        mockMvc.perform(get(URL + "/devices")
                        .param("brand", "Samsung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Samsung"));
    }

    @Test
    void getDevices_filterByState() throws Exception {
        repo.save(new Device().setName("D1").setBrand("X").setState(DeviceState.IN_USE));
        repo.save(new Device().setName("D2").setBrand("X").setState(DeviceState.AVAILABLE));

        mockMvc.perform(get(URL + "/devices")
                        .param("state", "IN_USE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].state").value("IN_USE"));
    }

    @Test
    void getDevices_pagination_limit_offset() throws Exception {
        for (int i = 1; i <= 5; i++) {
            repo.save(new Device()
                    .setName("D" + i)
                    .setBrand("B")
                    .setState(DeviceState.AVAILABLE));
        }

        mockMvc.perform(get(URL + "/devices")
                        .param("limit", "2")
                        .param("offset", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getDevices_emptyResult() throws Exception {
        mockMvc.perform(get(URL + "/devices")
                        .param("brand", "UnknownBrand"))
                .andExpect(status().isOk());
    }

    @Test
    void getDeviceByBrandAndName_success() throws Exception {
        repo.save(new Device()
                .setName("Pixel 7")
                .setBrand("Google")
                .setState(DeviceState.AVAILABLE));

        mockMvc.perform(get(URL + "/search")
                        .param("brand", "Google")
                        .param("name", "Pixel 7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Google"))
                .andExpect(jsonPath("$.name").value("Pixel 7"));
    }

    @Test
    void getDeviceByBrandAndName_notFound() throws Exception {
        mockMvc.perform(get(URL + "/search")
                        .param("brand", "Unknown")
                        .param("name", "Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDevices_pagination() throws Exception {
        repo.save(new Device().setName("A").setBrand("B").setState(DeviceState.AVAILABLE));
        repo.save(new Device().setName("C").setBrand("D").setState(DeviceState.AVAILABLE));
        repo.save(new Device().setName("E").setBrand("F").setState(DeviceState.AVAILABLE));

        mockMvc.perform(get(URL + "/devices")
                        .param("limit", "1")
                        .param("offset", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
