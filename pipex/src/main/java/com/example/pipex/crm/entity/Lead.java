package com.example.pipex.crm.entity;

import jakarta.persistence.*;

import com.example.pipex.common.entity.Priority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

import com.example.pipex.common.entity.BaseEntity;

@Entity
@Table(name = "leads", indexes = {
        @Index(name = "idx_lead_account_id", columnList = "account_id"),
        @Index(name = "idx_lead_pipeline_id", columnList = "pipeline_id"),
        @Index(name = "idx_lead_stage_id", columnList = "stage_id"),
        @Index(name = "idx_lead_owner_user_id", columnList = "owner_user_id"),
        @Index(name = "idx_lead_priority", columnList = "priority"),
        @Index(name = "idx_lead_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_lead_email", columnList = "primary_email"),
        @Index(name = "idx_lead_phone", columnList = "primary_phone"),
        @Index(name = "idx_lead_created_at", columnList = "created_at"),
        @Index(name = "idx_lead_contact_id", columnList = "contact_id")
})
@SQLDelete(sql = "UPDATE leads SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lead extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "pipeline_id", nullable = false)
    private Long pipelineId;

    @Column(name = "stage_id", nullable = true)
    private Long stageId;

    @Column(name = "conversation_id", nullable = true)
    private Long conversationId;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "primary_phone", columnDefinition = "TEXT")
    private String primaryPhone;

    @Column(name = "primary_email", columnDefinition = "TEXT")
    private String primaryEmail;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "priority", columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.LOW;

    @Column(name = "score")
    @Builder.Default
    private Integer score = 0;

    @Column(name = "last_touch_at")
    private OffsetDateTime lastTouchAt;

    @Column(name = "first_response_at")
    private OffsetDateTime firstResponseAt;

    @Column(name = "sla_due_at")
    private OffsetDateTime slaDueAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_attributes", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> additionalAttributes = Map.of();

    /**
     * Add additional attribute
     */
    public void addAttribute(String key, Object value) {
        if (this.additionalAttributes == null || this.additionalAttributes.isEmpty()) {
            this.additionalAttributes = new java.util.HashMap<>();
        }
        this.additionalAttributes.put(key, value);
    }

    /**
     * Get additional attribute
     */
    public Object getAttribute(String key) {
        return this.additionalAttributes != null ? this.additionalAttributes.get(key) : null;
    }

    /**
     * Remove additional attribute
     */
    public void removeAttribute(String key) {
        if (this.additionalAttributes != null) {
            this.additionalAttributes.remove(key);
        }
    }
}
