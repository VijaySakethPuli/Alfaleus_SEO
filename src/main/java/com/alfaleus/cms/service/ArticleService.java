package com.alfaleus.cms.service;

import com.alfaleus.cms.domain.Article;
import com.alfaleus.cms.domain.ArticleStatus;
import com.alfaleus.cms.domain.SeoMetadata;
import com.alfaleus.cms.domain.User;
import com.alfaleus.cms.repository.ArticleRepository;
import com.alfaleus.cms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }
    
    public List<Article> getPublishedArticles() {
        return articleRepository.findByStatusOrderByPublishedDateDesc(ArticleStatus.PUBLISHED);
    }

    public List<Article> searchPublishedArticles(String query) {
        if (query == null || query.isBlank()) {
            return getPublishedArticles();
        }
        return articleRepository.findByStatusAndTitleContainingIgnoreCaseOrderByPublishedDateDesc(ArticleStatus.PUBLISHED, query.trim());
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid article Id:" + id));
    }

    public Article getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Article not found with slug:" + slug));
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Transactional
    public Article saveArticle(Article article, String authorEmail, String metaTitle, String metaDescription, String focusKeyword) {
        if (article.getId() == null) {
            User author = userRepository.findByEmail(authorEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
            article.setAuthor(author);
            article.setSlug(generateUniqueSlug(article.getTitle(), null));
            if (article.getStatus() == ArticleStatus.PUBLISHED) {
                article.setPublishedDate(LocalDateTime.now());
            }
        } else {
            Article existing = getArticleById(article.getId());
            article.setAuthor(existing.getAuthor());
            article.setCreatedAt(existing.getCreatedAt());
            if (!existing.getTitle().equals(article.getTitle())) {
                article.setSlug(generateUniqueSlug(article.getTitle(), article.getId()));
            } else {
                article.setSlug(existing.getSlug());
            }
            if (article.getStatus() == ArticleStatus.PUBLISHED && existing.getStatus() != ArticleStatus.PUBLISHED) {
                article.setPublishedDate(LocalDateTime.now());
            } else {
                article.setPublishedDate(existing.getPublishedDate());
            }
            
            if (existing.getSeoMetadata() != null) {
                SeoMetadata meta = existing.getSeoMetadata();
                meta.setMetaTitle(metaTitle);
                meta.setMetaDescription(metaDescription);
                meta.setFocusKeyword(focusKeyword);
                article.setSeoMetadata(meta);
            }
        }

        if (article.getSeoMetadata() == null) {
            SeoMetadata meta = new SeoMetadata();
            meta.setMetaTitle(metaTitle);
            meta.setMetaDescription(metaDescription);
            meta.setFocusKeyword(focusKeyword);
            article.setSeoMetadata(meta);
        }

        return articleRepository.save(article);
    }

    private String generateUniqueSlug(String title, Long excludeId) {
        String baseSlug = title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "").replaceAll("^-", "");
        String slug = baseSlug;
        int counter = 1;
        while (true) {
            java.util.Optional<Article> existing = articleRepository.findBySlug(slug);
            if (existing.isPresent() && !existing.get().getId().equals(excludeId)) {
                slug = baseSlug + "-" + counter;
                counter++;
            } else {
                break;
            }
        }
        return slug;
    }
}
