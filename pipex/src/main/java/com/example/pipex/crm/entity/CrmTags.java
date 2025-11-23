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
 * CrmTags entity for CRM
 * Represents a tag for a lead
 * Implements soft delete pattern
 */

/**
 * Tag entity for CRM
 * Implements soft delete pattern
 */
@Entity
@Table(name = "crm_tags", indexes = {
        @Index(name = "idx_tag_account_id", columnList = "account_id"),
        @Index(name = "idx_tag_name", columnList = "name"),
        @Index(name = "idx_tag_deleted_at", columnList = "deleted_at")
})
@SQLDelete(sql = "UPDATE crm_tags SET deleted_at = NOW() WHERE id = ?")
@Data
@SQLDelete(sql = "UPDATE crm_tags SET deleted_at = NOW() WHERE id = ? AND deleted_at IS NULL")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTags extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;

    @Column(name = "owner_user_id", nullable = true)
    private Long ownerUserId;

}
