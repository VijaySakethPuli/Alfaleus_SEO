package com.alfaleus.cms.web;

import com.alfaleus.cms.domain.Media;
import com.alfaleus.cms.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // ─── GALLERY PAGE ─────────────────────────────────────────────────────────

    @GetMapping("/media")
    public String gallery(Model model) {
        model.addAttribute("mediaItems", mediaService.getAllMedia());
        return "admin/media-gallery";
    }

    // ─── REST API ENDPOINTS ───────────────────────────────────────────────────

    @PostMapping("/api/media/upload")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "alt_text", required = false, defaultValue = "") String altText,
                                         @RequestParam(value = "caption", required = false) String caption) {
        try {
            if (altText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "alt_text is required for accessibility"));
            }
            Media media = mediaService.storeFile(file, altText, caption);
            return ResponseEntity.ok(Map.of(
                    "url",     media.getFilePathOrUrl(),
                    "id",      media.getId(),
                    "altText", media.getAltText() != null ? media.getAltText() : ""
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/media/upload-video")
    @ResponseBody
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "alt_text", required = false, defaultValue = "") String altText,
                                         @RequestParam(value = "caption", required = false) String caption) {
        try {
            Media media = mediaService.storeVideoFile(file, altText, caption);
            return ResponseEntity.ok(Map.of(
                    "url",  media.getFilePathOrUrl(),
                    "id",   media.getId(),
                    "type", "video"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/media/youtube")
    @ResponseBody
    public ResponseEntity<?> embedYouTube(@RequestParam("url") String url,
                                          @RequestParam(value = "alt_text", required = false) String altText,
                                          @RequestParam(value = "caption", required = false) String caption) {
        try {
            Media media = mediaService.storeYouTubeVideo(url, altText, caption);
            return ResponseEntity.ok(Map.of(
                    "videoId", media.getFilePathOrUrl(),
                    "id",      media.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
