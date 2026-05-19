package com.alfaleus.cms.repository;

import com.alfaleus.cms.domain.Article;
import com.alfaleus.cms.domain.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);
    List<Article> findByStatusOrderByPublishedDateDesc(ArticleStatus status);
    
    List<Article> findByStatusAndTitleContainingIgnoreCaseOrderByPublishedDateDesc(ArticleStatus status, String title);
}
