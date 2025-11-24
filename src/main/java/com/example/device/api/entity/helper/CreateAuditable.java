package com.example.device.api.entity.helper;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base auditable entity with only creation timestamp.
 * TODO (future):
 *  - Add updatedAt, createdBy, updatedBy fields.
 *  - createdBy, updatedBy implement full auditing via JWT or Telegram username.
 */
@Getter
@Accessors(chain = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreateAuditable {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
