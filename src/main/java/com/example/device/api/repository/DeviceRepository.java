package com.example.device.api.repository;

import com.example.device.api.entity.Device;
import com.example.device.api.entity.DeviceState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for managing Device entities.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * Checks if a device already exists based on name and brand.
     *
     * @param name  device name
     * @param brand device brand
     * @return true if a matching device exists
     */
    boolean existsByNameAndBrand(String name, String brand);

    /**
     * Fetch device by unique brand + name.
     */
    Optional<Device> findByBrandAndName(String brand, String name);

    @Query("""
        SELECT d FROM Device d
        WHERE (:brand IS NULL OR d.brand = :brand)
          AND (:name IS NULL OR d.name = :name)
          AND (:state IS NULL OR d.state = :state)
        """)
    List<Device> findFiltered(
            @Param("brand") String brand,
            @Param("name") String name,
            @Param("state") DeviceState state,
            Pageable pageable
    );
}
