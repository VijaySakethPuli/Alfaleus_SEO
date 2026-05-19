package com.alfaleus.cms.web;

import com.alfaleus.cms.domain.Article;
import com.alfaleus.cms.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PublicController {

    private final ArticleService articleService;

    public PublicController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String home(Model model, @RequestParam(value = "query", required = false) String query) {
        model.addAttribute("articles", articleService.searchPublishedArticles(query));
        model.addAttribute("searchQuery", query);
        return "public/home";
    }

    @GetMapping("/article/{slug}")
    public String viewArticle(@PathVariable String slug, Model model) {
        try {
            Article article = articleService.getArticleBySlug(slug);
            
            // Ensure article is published
            if (!article.getStatus().name().equals("PUBLISHED")) {
                return "redirect:/";
            }
            
            model.addAttribute("article", article);
            model.addAttribute("meta", article.getSeoMetadata());
            return "public/article";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> sitemap(HttpServletRequest request) {
        List<Article> publishedArticles = articleService.getPublishedArticles();
        
        // Get the base URL
        String baseUrl = request.getScheme() + "://" + request.getServerName() + 
                         ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort());

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Add home page
        xml.append("  <url>\n");
        xml.append("    <loc>").append(baseUrl).append("/").append("</loc>\n");
        xml.append("    <changefreq>daily</changefreq>\n");
        xml.append("    <priority>1.0</priority>\n");
        xml.append("  </url>\n");

        // Add articles
        for (Article article : publishedArticles) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(baseUrl).append("/article/").append(article.getSlug()).append("</loc>\n");
            
            String lastMod = article.getUpdatedAt() != null ? article.getUpdatedAt().format(formatter) : 
                            (article.getPublishedDate() != null ? article.getPublishedDate().format(formatter) : "");
            
            if (!lastMod.isEmpty()) {
                xml.append("    <lastmod>").append(lastMod).append("</lastmod>\n");
            }
            xml.append("    <changefreq>weekly</changefreq>\n");
            xml.append("    <priority>0.8</priority>\n");
            xml.append("  </url>\n");
        }

        xml.append("</urlset>");
        return ResponseEntity.ok(xml.toString());
    }

    @GetMapping(value = "/rss", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> rss(HttpServletRequest request) {
        List<Article> publishedArticles = articleService.getPublishedArticles();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + 
                         ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort());

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        xml.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("  <channel>\n");
        xml.append("    <title>Alfaleus Medical - Knowledge Base</title>\n");
        xml.append("    <link>").append(baseUrl).append("/</link>\n");
        xml.append("    <description>Clinical research, medical device insights, and patient education.</description>\n");
        xml.append("    <atom:link href=\"").append(baseUrl).append("/rss\" rel=\"self\" type=\"application/rss+xml\" />\n");

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

        for (Article article : publishedArticles) {
            xml.append("    <item>\n");
            xml.append("      <title><![CDATA[").append(article.getTitle()).append("]]></title>\n");
            xml.append("      <link>").append(baseUrl).append("/article/").append(article.getSlug()).append("</link>\n");
            xml.append("      <guid>").append(baseUrl).append("/article/").append(article.getSlug()).append("</guid>\n");
            if (article.getExcerpt() != null) {
                xml.append("      <description><![CDATA[").append(article.getExcerpt()).append("]]></description>\n");
            }
            if (article.getPublishedDate() != null) {
                java.time.ZonedDateTime zdt = article.getPublishedDate().atZone(java.time.ZoneId.of("UTC"));
                xml.append("      <pubDate>").append(zdt.format(formatter)).append("</pubDate>\n");
            }
            xml.append("    </item>\n");
        }

        xml.append("  </channel>\n");
        xml.append("</rss>");

        return ResponseEntity.ok(xml.toString());
    }
}
