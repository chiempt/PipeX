package com.example.pipex.crm.entity;

// 1. Tạo IdClass (phải implements Serializable)
import java.io.Serializable;
import java.util.Objects;

public class LeadTagId implements Serializable {
    private Long leadId;
    private Long tagId;

    // constructor rỗng bắt buộc
    public LeadTagId() {
    }

    public LeadTagId(Long leadId, Long tagId) {
        this.leadId = leadId;
        this.tagId = tagId;
    }

    // equals() và hashCode() BẮT BUỘC
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LeadTagId that = (LeadTagId) o;
        return Objects.equals(leadId, that.leadId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leadId, tagId);
    }
}