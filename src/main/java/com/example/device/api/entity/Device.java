package com.example.device.api.entity;

import com.example.device.api.entity.helper.CreateAuditable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Device entity stored in PostgreSQL.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Data
@Accessors(chain = true)
@Entity
@Table(name = "devices")
public class Device extends CreateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceState state;
}
