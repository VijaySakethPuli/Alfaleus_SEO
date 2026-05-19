package com.alfaleus.cms.service;

import com.alfaleus.cms.domain.Media;
import com.alfaleus.cms.domain.MediaType;
import com.alfaleus.cms.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MediaService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/webm", "video/ogg"
    );

    private final MediaRepository mediaRepository;
    private final Path fileStorageLocation;

    public MediaService(MediaRepository mediaRepository,
                        @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.mediaRepository = mediaRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory.", ex);
        }
    }

    // ─── IMAGE UPLOAD ────────────────────────────────────────────────────────

    public Media storeFile(MultipartFile file, String altText, String caption) {
        validateContentType(file, ALLOWED_IMAGE_TYPES, "Image");
        String savedPath = saveToFilesystem(file);

        Media media = new Media();
        media.setFilePathOrUrl("/uploads/" + savedPath);
        media.setMediaType(MediaType.IMAGE);
        media.setAltText(altText);
        media.setCaption(caption);
        return mediaRepository.save(media);
    }

    // ─── VIDEO FILE UPLOAD ───────────────────────────────────────────────────

    public Media storeVideoFile(MultipartFile file, String altText, String caption) {
        validateContentType(file, ALLOWED_VIDEO_TYPES, "Video");
        String savedPath = saveToFilesystem(file);

        Media media = new Media();
        media.setFilePathOrUrl("/uploads/" + savedPath);
        media.setMediaType(MediaType.VIDEO_LOCAL);
        media.setAltText(altText);
        media.setCaption(caption);
        return mediaRepository.save(media);
    }

    // ─── YOUTUBE EMBED ───────────────────────────────────────────────────────

    public Media storeYouTubeVideo(String youtubeUrl, String altText, String caption) {
        String videoId = extractYouTubeId(youtubeUrl);
        if (videoId == null) {
            throw new IllegalArgumentException("Invalid YouTube URL");
        }

        Media media = new Media();
        media.setFilePathOrUrl(videoId);
        media.setMediaType(MediaType.VIDEO_YOUTUBE);
        media.setAltText(altText);
        media.setCaption(caption);
        return mediaRepository.save(media);
    }

    // ─── GALLERY ─────────────────────────────────────────────────────────────

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private String saveToFilesystem(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        String newFileName = UUID.randomUUID() + ext;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation);
            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again.", ex);
        }
    }

    private void validateContentType(MultipartFile file, Set<String> allowed, String label) {
        String ct = file.getContentType();
        if (ct == null || !allowed.contains(ct)) {
            throw new IllegalArgumentException(label + " type not allowed: " + ct +
                    ". Allowed: " + String.join(", ", allowed));
        }
    }

    private String extractYouTubeId(String youtubeUrl) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Matcher matcher = Pattern.compile(pattern).matcher(youtubeUrl);
        return matcher.find() ? matcher.group() : null;
    }
}
