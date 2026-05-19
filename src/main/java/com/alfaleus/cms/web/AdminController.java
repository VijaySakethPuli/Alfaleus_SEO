package com.alfaleus.cms.web;

import com.alfaleus.cms.domain.Article;
import com.alfaleus.cms.domain.ArticleStatus;
import com.alfaleus.cms.repository.ArticleRepository;
import com.alfaleus.cms.service.ArticleService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ArticleService articleService;
    private final ArticleRepository articleRepository;

    public AdminController(ArticleService articleService, ArticleRepository articleRepository) {
        this.articleService = articleService;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping({"", "/"})
    public String root() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        model.addAttribute("totalCount",     articles.size());
        model.addAttribute("publishedCount", articles.stream().filter(a -> a.getStatus() == ArticleStatus.PUBLISHED).count());
        model.addAttribute("draftCount",     articles.stream().filter(a -> a.getStatus() == ArticleStatus.DRAFT).count());
        model.addAttribute("pendingCount",   articles.stream().filter(a -> a.getStatus() == ArticleStatus.PENDING_REVIEW).count());
        return "admin/dashboard";
    }

    @GetMapping("/article/create")
    public String createArticleForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("metaTitle", "");
        model.addAttribute("metaDescription", "");
        model.addAttribute("focusKeyword", "");
        return "admin/article-form";
    }

    @PostMapping("/article/save")
    public String saveArticle(@ModelAttribute("article") Article article,
                              @RequestParam("metaTitle") String metaTitle,
                              @RequestParam("metaDescription") String metaDescription,
                              @RequestParam(value = "focusKeyword", required = false, defaultValue = "") String focusKeyword,
                              Authentication authentication) {
        articleService.saveArticle(article, authentication.getName(), metaTitle, metaDescription, focusKeyword);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/article/{id}/edit")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        model.addAttribute("metaTitle",      article.getSeoMetadata() != null ? article.getSeoMetadata().getMetaTitle() : "");
        model.addAttribute("metaDescription",article.getSeoMetadata() != null ? article.getSeoMetadata().getMetaDescription() : "");
        model.addAttribute("focusKeyword",   article.getSeoMetadata() != null ? article.getSeoMetadata().getFocusKeyword() : "");
        return "admin/article-form";
    }

    @PostMapping("/article/{id}/delete")
    public String deleteArticle(@PathVariable Long id) {
        articleRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "redirect:/admin/dashboard";
    }
}
