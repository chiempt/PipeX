package com.example.pipex.crm.entity;

import com.example.pipex.common.entity.BaseEntity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * LeadNote entity
 * Represents notes/comments on a lead
 * No soft delete - notes are permanent records
 */
@Entity
@Table(name = "lead_notes", indexes = {
        @Index(name = "idx_lead_note_lead_id", columnList = "lead_id"),
        @Index(name = "idx_lead_note_user_id", columnList = "user_id"),
        @Index(name = "idx_lead_note_created_at", columnList = "created_at"),
        @Index(name = "idx_lead_note_account_id", columnList = "account_id")
})
@Data
@SQLDelete(sql = "UPDATE lead_notes SET deleted_at = NOW() WHERE id = ?")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadNote extends BaseEntity {

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;
}
