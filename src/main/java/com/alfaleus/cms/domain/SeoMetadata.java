package com.alfaleus.cms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "seo_metadata")
public class SeoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false, unique = true)
    private Article article;

    @Column(name = "meta_title", nullable = false, length = 60)
    private String metaTitle;

    @Column(name = "meta_description", nullable = false, length = 160)
    private String metaDescription;

    @Column(name = "focus_keyword", length = 100)
    private String focusKeyword;

    @Column(name = "canonical_url")
    private String canonicalUrl;

    public SeoMetadata() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }
    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    public String getFocusKeyword() { return focusKeyword; }
    public void setFocusKeyword(String focusKeyword) { this.focusKeyword = focusKeyword; }
    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }
}
