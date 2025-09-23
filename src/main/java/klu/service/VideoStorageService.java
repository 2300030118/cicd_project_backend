package klu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class VideoStorageService {

    private final Path storageRoot;

    public VideoStorageService(@Value("${app.storage.location}") String storageLocation) throws IOException {
        this.storageRoot = Paths.get(storageLocation).toAbsolutePath().normalize();
        Files.createDirectories(this.storageRoot);
    }

    public String store(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String newFilename = UUID.randomUUID() + "-" + originalFilename;
        Path target = storageRoot.resolve(newFilename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target.toString();
    }

    public Resource loadAsResource(String storedPath) {
        Path path = Paths.get(storedPath);
        return new FileSystemResource(path);
    }

    public ResponseEntity<Resource> buildStreamResponse(Resource resource, List<HttpRange> ranges) throws IOException {
        long contentLength = resource.contentLength();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
        if (ranges == null || ranges.isEmpty()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .body(resource);
        }
        HttpRange range = ranges.get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = end - start + 1;
        headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + contentLength);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(rangeLength))
                .body(resource);
    }
}





