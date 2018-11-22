package fileReader.fileReader.storage;

import fileReader.fileReader.fileScanner.FileScanner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class StorageServc {
    private final Path rootLoc = Paths.get("src/results/");
    private final Path rootLocResults = Paths.get("src/results/filterResults");

    public void store(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.rootLoc.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("FAIL!");
        }
    }
    public Resource loadFile(String fileName) {
        try {
            Path file = rootLocResults.resolve(fileName);
            UrlResource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("FAIL ");
            }
        } catch (Exception e) {
            throw new RuntimeException("FAIL " + e);
        }
    }
    public void launchThreads() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        FileScanner fileScanner1 = new FileScanner(rootLoc, 'a', 'g');
        executorService.execute(fileScanner1);
        FileScanner fileScanner2 = new FileScanner(rootLoc, 'h', 'n');
        executorService.execute(fileScanner2);
        FileScanner fileScanner3 = new FileScanner(rootLoc, 'o', 'u');
        executorService.execute(fileScanner3);
        FileScanner fileScanner4 = new FileScanner(rootLoc, 'v', 'z');
        executorService.execute(fileScanner4);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}
