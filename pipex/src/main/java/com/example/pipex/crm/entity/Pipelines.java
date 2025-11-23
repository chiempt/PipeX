package com.example.pipex.crm.entity;

import com.example.pipex.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Pipeline entity for CRM
 * Represents a sales pipeline with stages
 * Implements soft delete pattern
 */
@Entity
@Table(name = "pipelines", indexes = {
        @Index(name = "idx_pipeline_account_id", columnList = "account_id"),
        @Index(name = "idx_pipeline_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_pipeline_is_default", columnList = "account_id, is_default")
})
@SQLDelete(sql = "UPDATE pipelines SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pipelines extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;
}
