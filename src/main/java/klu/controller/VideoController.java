package klu.controller;

import klu.model.Video;
import klu.repository.VideoRepository;
import klu.service.VideoStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin
public class VideoController {
    private final VideoRepository videoRepository;
    private final VideoStorageService storageService;

    public VideoController(VideoRepository videoRepository, VideoStorageService storageService) {
        this.videoRepository = videoRepository;
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Video> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category
    ) throws IOException {
        String storedPath = storageService.store(file);
        Video video = new Video();
        video.setTitle(title != null ? title : file.getOriginalFilename());
        video.setDescription(description);
        video.setCategory(category);
        video.setFilePath(storedPath);
        return ResponseEntity.ok(videoRepository.save(video));
    }

    @GetMapping
    public List<Video> all(@RequestParam(required = false) String category) {
        return category == null ? videoRepository.findAll() : videoRepository.findByCategory(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> get(@PathVariable Long id) {
        Optional<Video> video = videoRepository.findById(id);
        return video.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> stream(@PathVariable Long id, @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) return ResponseEntity.notFound().build();
        Resource resource = storageService.loadAsResource(video.getFilePath());
        List<HttpRange> ranges = rangeHeader != null ? HttpRange.parseRanges(rangeHeader) : null;
        return storageService.buildStreamResponse(resource, ranges);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) return ResponseEntity.notFound().build();
        Resource resource = storageService.loadAsResource(video.getFilePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + video.getTitle())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}





