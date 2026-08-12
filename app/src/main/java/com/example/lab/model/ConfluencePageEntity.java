package com.example.lab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "confluence_pages")
public class ConfluencePageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private String status;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String tableJson;

    private Instant updatedAt = Instant.now();

    public ConfluencePageEntity() {}

    public ConfluencePageEntity(String title, String category, String status, String content, String tableJson) {
        this.title = title;
        this.category = category;
        this.status = status;
        this.content = content;
        this.tableJson = tableJson;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTableJson() { return tableJson; }
    public void setTableJson(String tableJson) { this.tableJson = tableJson; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
