package com.example.pipex.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LeadTag entity - Many-to-many relationship between Leads and Tags
 * Uses composite key (lead_id, tag_id)
 * No soft delete - just a join table
 * Does not extend BaseEntity because it uses @IdClass with composite key
 */
@Entity
@Table(name = "lead_tags", indexes = {
        @Index(name = "idx_lead_tag_lead_id", columnList = "lead_id"),
        @Index(name = "idx_lead_tag_tag_id", columnList = "tag_id"),
        @Index(name = "idx_lead_tag_unique", columnList = "lead_id, tag_id", unique = true)
})
@IdClass(LeadTagId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadTags {

    @Id
    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Id
    @Column(name = "tag_id", nullable = false)
    private Long tagId;
}
