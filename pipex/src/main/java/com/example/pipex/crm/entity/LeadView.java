package com.example.pipex.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.Map;

import com.example.pipex.common.entity.BaseEntity;

/**
 * LeadView entity
 * Stores saved view definitions for leads (combines filter and group set)
 * Implements soft delete pattern
 */
@Entity
@Table(name = "lead_views", indexes = {
        @Index(name = "idx_lead_view_account_id", columnList = "account_id"),
        @Index(name = "idx_lead_view_owner_user_id", columnList = "owner_user_id"),
        @Index(name = "idx_lead_view_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_lead_view_name", columnList = "name"),
        @Index(name = "idx_lead_view_is_default", columnList = "account_id, is_default"),
        @Index(name = "idx_lead_view_pinned", columnList = "account_id, pinned")
})
@SQLDelete(sql = "UPDATE lead_views SET deleted_at = NOW() WHERE id = ?")
@SQLDelete(sql = "UPDATE lead_views SET deleted_at = NOW() WHERE id = ? AND deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadView extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "share_scope", nullable = false, columnDefinition = "TEXT")
    private String shareScope; // e.g., "private", "account", "global"

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "definition", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> definition;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "pinned", nullable = false)
    @Builder.Default
    private Boolean pinned = false;
}
