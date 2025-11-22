package com.example.device.api.ct;

import com.example.device.api.dto.requests.CreateDeviceRequest;
import com.example.device.api.entity.DeviceState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreateDeviceController.class)
class CreateDeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateDeviceService createDeviceService;

    @Test
    void createDevice_success() throws Exception {

        var request = new CreateDeviceRequest()
                .setName("iPhone X")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE);

        var response = new DeviceResponse(1L, "iPhone X", "Apple", DeviceState.AVAILABLE);

        when(createDeviceService.createDevice(any())).thenReturn(response);

        mockMvc.perform(
                        post("/device-api/devices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPhone X"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));
    }
}
