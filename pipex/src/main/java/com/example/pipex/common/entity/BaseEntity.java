package com.example.pipex.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * Base entity với các trường chung: id, createdAt, updatedAt
 * Tất cả entity nên extend từ đây nếu không có soft delete
 */
@MappedSuperclass
@Data
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    protected OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    protected OffsetDateTime updatedAt;

    @Column(name = "account_id", nullable = false)
    protected Long accountId;

    @Column(name = "created_by_user_id", nullable = true)
    protected Long createdByUserId;

    @Column(name = "updated_by_user_id", nullable = true)
    protected Long updatedByUserId;

    @Column(name = "deleted_by_user_id", nullable = true)
    protected Long deletedByUserId;
}
