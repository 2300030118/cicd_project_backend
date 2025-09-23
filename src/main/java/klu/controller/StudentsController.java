package klu.controller;

import klu.model.Course;
import klu.model.Lesson;
import klu.repository.CourseRepository;
import klu.repository.LessonRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentsController {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    public StudentsController(CourseRepository courseRepository, LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("/courses/{courseId}/lessons")
    public List<Lesson> getLessons(@PathVariable Long courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    @GetMapping("/lessons/{lessonId}/download")
    public ResponseEntity<Resource> downloadLesson(@PathVariable Long lessonId) {
        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        if (lesson.isEmpty() || lesson.get().getResourcePath() == null) return ResponseEntity.notFound().build();
        File file = new File(lesson.get().getResourcePath());
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}





