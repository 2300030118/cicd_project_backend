package klu.controller;

import klu.model.Video;
import klu.repository.VideoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seniors")
@CrossOrigin
public class SeniorsController {
    private final VideoRepository videoRepository;

    public SeniorsController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping("/categories")
    public Map<String, List<Video>> categories() {
        Map<String, List<Video>> map = new HashMap<>();
        map.put("movies", videoRepository.findByCategory("movies"));
        map.put("news", videoRepository.findByCategory("news"));
        map.put("shows", videoRepository.findByCategory("shows"));
        map.put("music", videoRepository.findByCategory("music"));
        return map;
    }
}





