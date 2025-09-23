package klu.controller;

import klu.model.Video;
import klu.repository.VideoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fun")
@CrossOrigin
public class FunController {
    private final VideoRepository videoRepository;

    public FunController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping
    public Map<String, List<Video>> funHome() {
        Map<String, List<Video>> map = new HashMap<>();
        map.put("cartoons", videoRepository.findByCategory("cartoons"));
        map.put("games", videoRepository.findByCategory("games"));
        map.put("episodes", videoRepository.findByCategory("episodes"));
        return map;
    }
}





