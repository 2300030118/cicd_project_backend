package klu.controller;

import klu.model.NavigationItem;
import klu.model.User;
import klu.repository.NavigationItemRepository;
import klu.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin
public class MenuController {
    private final NavigationItemRepository navRepo;
    private final UserRepository userRepo;

    public MenuController(NavigationItemRepository navRepo, UserRepository userRepo) {
        this.navRepo = navRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/nav")
    public List<NavigationItem> nav() {
        return navRepo.findAll();
    }

    @GetMapping("/profile/{userId}")
    public Optional<User> profile(@PathVariable Long userId) {
        return userRepo.findById(userId);
    }

    @GetMapping("/about")
    public Map<String, String> about() {
        Map<String, String> map = new HashMap<>();
        map.put("title", "About This App");
        map.put("content", "A learning and entertainment portal for families.");
        return map;
    }

    @PostMapping("/support")
    public Map<String, String> support(@RequestBody Map<String, String> body) {
        return Map.of("status", "received", "subject", body.getOrDefault("subject", ""));
    }
}





