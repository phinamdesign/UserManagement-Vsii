package com.vsii.controller;

import com.vsii.model.DBFile;
import com.vsii.model.Mail;
import com.vsii.payload.UploadFileResponse;
import com.vsii.service.DBFileStorageService;
import com.vsii.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static com.vsii.model.Write.writeExcel;

@RestController
@RequestMapping("/api/auth")
public class FileController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private DBFileStorageService dbFileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {

        DBFile dbFile = dbFileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(dbFile.getId())
                .toUriString();

        Mail mail = new Mail();
        mail.setMailFrom("phinamh@gmail.com");
        mail.setMailTo("phinamh@gmail.com");
        mail.setMailSubject("[User Management] Upload file");
        mail.setMailContent("Upload file successfully !");
        mailService.sendEmail(mail);

        return new UploadFileResponse(dbFile.getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        // Load file from database
        DBFile dbFile = dbFileStorageService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getData()));
    }

//    @GetMapping("/export")
//    @Scheduled(fixedRate = 600000)
    public ResponseEntity<?> exportExcel() throws IOException {
        List<DBFile> dbFiles=(List<DBFile>) dbFileStorageService.findAll();
        final String excelFilePath = "D:/VSI/PVN-Project/fileUpload.xlsx";
        writeExcel(dbFiles, excelFilePath);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
