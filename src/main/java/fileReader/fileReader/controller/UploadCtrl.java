package fileReader.fileReader.controller;


import fileReader.fileReader.storage.StorageServc;
import fileReader.fileReader.utils.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UploadCtrl {

    @Autowired
    StorageServc storageServc;
    DataStorage dataStorage;
    List<String> files = new ArrayList<String>();

    @PostMapping("/post")
    public ResponseEntity<String> handleFIleUpload(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageServc.store(file);
            files.add(file.getOriginalFilename());
            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }
    @GetMapping(value = "/getallfiles")
    public ResponseEntity<List<String>> getListFiles(Model model) {
        List<String> fileNames = DataStorage.getFiles().stream().map(fileName -> MvcUriComponentsBuilder.fromMethodName(UploadCtrl.class,
                "getFile", fileName.substring(fileName.length()-7)).build().toString()).collect(Collectors.toList());
        return ResponseEntity.ok().body(fileNames);
    }

    @GetMapping(value = "/getresults", produces = "application/json")
    @ResponseBody
    public ResponseEntity<HashMap<String, Integer>> getResults() throws InterruptedException {
        storageServc.launchThreads();
        return ResponseEntity.ok().body(dataStorage.getWordMap());
    }
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageServc.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
