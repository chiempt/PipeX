package com.example.pipex.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import com.example.pipex.common.entity.BaseEntity;

/**
 * Source entity for CRM
 * Represents lead/contact sources (e.g., website, referral, social media)
 * Implements soft delete pattern
 */
@Entity
@Table(name = "sources", indexes = {
        @Index(name = "idx_source_name", columnList = "name"),
        @Index(name = "idx_source_deleted_at", columnList = "deleted_at")
})
@SQLDelete(sql = "UPDATE sources SET deleted_at = NOW() WHERE id = ?")
@SQLDelete(sql = "UPDATE sources SET deleted_at = NOW() WHERE id = ? AND deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadSource extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;
}
