package com.alfaleus.cms.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "body_content", nullable = false, columnDefinition = "LONGTEXT")
    private String bodyContent;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "featured_image_url")
    private String featuredImageUrl;

    @Column(name = "featured_image_alt")
    private String featuredImageAlt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medically_reviewed_by_id")
    private User medicallyReviewedBy;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private SeoMetadata seoMetadata;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Article() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getBodyContent() { return bodyContent; }
    public void setBodyContent(String bodyContent) { this.bodyContent = bodyContent; }
    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
    public String getFeaturedImageUrl() { return featuredImageUrl; }
    public void setFeaturedImageUrl(String featuredImageUrl) { this.featuredImageUrl = featuredImageUrl; }
    public String getFeaturedImageAlt() { return featuredImageAlt; }
    public void setFeaturedImageAlt(String featuredImageAlt) { this.featuredImageAlt = featuredImageAlt; }

    /** Estimated reading time in minutes (200 wpm average). */
    public int getReadingTimeMinutes() {
        if (bodyContent == null || bodyContent.isBlank()) return 1;
        String text = bodyContent.replaceAll("<[^>]*>", " ");
        int words = text.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(words / 200.0));
    }
    public ArticleStatus getStatus() { return status; }
    public void setStatus(ArticleStatus status) { this.status = status; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public User getMedicallyReviewedBy() { return medicallyReviewedBy; }
    public void setMedicallyReviewedBy(User medicallyReviewedBy) { this.medicallyReviewedBy = medicallyReviewedBy; }
    public LocalDateTime getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDateTime publishedDate) { this.publishedDate = publishedDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public SeoMetadata getSeoMetadata() { return seoMetadata; }
    public void setSeoMetadata(SeoMetadata seoMetadata) { 
        this.seoMetadata = seoMetadata; 
        if(seoMetadata != null) {
            seoMetadata.setArticle(this);
        }
    }
}
