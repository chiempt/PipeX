package com.example.pipex.common.entity;

public enum Priority {

    LOW("Thấp", "gray", 1),
    MEDIUM("Trung bình", "blue", 2),
    HIGH("Cao", "orange", 3),
    VERY_HIGH("Rất cao", "red", 4);

    private final String label; // Hiển thị tiếng Việt
    private final String color; // Màu trên UI + dashboard
    private final int weight; // Dùng để sort, tính score

    Priority(String label, String color, int weight) {
        this.label = label;
        this.color = color;
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }
}
