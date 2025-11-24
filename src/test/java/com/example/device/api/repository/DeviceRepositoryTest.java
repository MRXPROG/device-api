package com.example.device.api.repository;

import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository repository;

    private Device d1, d2, d3;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        d1 = repository.save(new Device()
                .setName("iPhone 15")
                .setBrand("Apple")
                .setState(DeviceState.AVAILABLE));

        d2 = repository.save(new Device()
                .setName("Galaxy S23")
                .setBrand("Samsung")
                .setState(DeviceState.INACTIVE));

        d3 = repository.save(new Device()
                .setName("iPhone 14")
                .setBrand("Apple")
                .setState(DeviceState.INACTIVE));
    }

    @Test
    void testSave() {
        Device device = new Device()
                .setName("Pixel 9")
                .setBrand("Google")
                .setState(DeviceState.AVAILABLE);

        Device saved = repository.save(device);

        assertNotNull(saved.getId());
    }

    @Test
    void testExistsByNameAndBrand_Exists() {
        assertTrue(repository.existsByNameAndBrand("iPhone 15", "Apple"));
    }

    @Test
    void testExistsByNameAndBrand_NotExists() {
        assertFalse(repository.existsByNameAndBrand("Non-existing", "Apple"));
    }

    @Test
    void testFindByBrandAndName_Found() {
        Optional<Device> result = repository.findByBrandAndName("Apple", "iPhone 15");

        assertTrue(result.isPresent());
        assertEquals("Apple", result.get().getBrand());
        assertEquals("iPhone 15", result.get().getName());
    }

    @Test
    void testFindByBrandAndName_NotFound() {
        Optional<Device> result = repository.findByBrandAndName("Xiaomi", "13 Pro");

        assertFalse(result.isPresent());
    }

    @Test
    void findFiltered_ByBrand() {
        List<Device> list = repository.findFiltered(
                "Apple", null, null,
                PageRequest.of(0, 10)
        );

        assertEquals(2, list.size());
    }

    @Test
    void findFiltered_ByName() {
        List<Device> list = repository.findFiltered(
                null, "Galaxy S23", null,
                PageRequest.of(0, 10)
        );

        assertEquals(1, list.size());
        assertEquals("Samsung", list.getFirst().getBrand());
    }

    @Test
    void findFiltered_ByState() {
        List<Device> list = repository.findFiltered(
                null, null, DeviceState.INACTIVE,
                PageRequest.of(0, 10)
        );

        assertEquals(2, list.size());
    }

    @Test
    void findFiltered_ByBrandAndState() {
        List<Device> list = repository.findFiltered(
                "Apple", null, DeviceState.INACTIVE,
                PageRequest.of(0, 10)
        );

        assertEquals(1, list.size());
        assertEquals("iPhone 14", list.getFirst().getName());
    }

    @Test
    void findFiltered_NoFilters() {
        List<Device> list = repository.findFiltered(
                null, null, null,
                PageRequest.of(0, 10)
        );

        assertEquals(3, list.size());
    }

    @Test
    void findFiltered_Pagination() {
        List<Device> page1 = repository.findFiltered(
                null, null, null,
                PageRequest.of(0, 1)
        );

        List<Device> page2 = repository.findFiltered(
                null, null, null,
                PageRequest.of(1, 1)
        );

        assertEquals(1, page1.size());
        assertEquals(1, page2.size());
        assertNotEquals(page1.getFirst().getId(), page2.getFirst().getId());
    }
}