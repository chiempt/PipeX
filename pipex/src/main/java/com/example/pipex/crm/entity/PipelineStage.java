package com.example.pipex.crm.entity;

import com.example.pipex.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pipeline_stages", indexes = {
        @Index(name = "idx_pipeline_stage_account_id", columnList = "account_id"),
        @Index(name = "idx_pipeline_stage_pipeline_id", columnList = "pipeline_id"),
        @Index(name = "idx_pipeline_stage_position", columnList = "pipeline_id, position")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineStage extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "color", nullable = true)
    private String color;

    @Column(name = "code", nullable = true)
    private String code;

    @Column(name = "pipeline_id", nullable = false)
    private Long pipelineId;

    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;
}
