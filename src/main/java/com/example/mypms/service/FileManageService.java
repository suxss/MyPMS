package com.example.mypms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@EnableAsync
public class FileManageService {
    @Value(value = "${file.save_path}")
    private String FILE_SAVE_PATH;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String getNewFileName(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String type = filename.substring(filename.lastIndexOf("."));
        return uuid + type;
    }

    @Async
    public void fileUpload(byte[] file, String name) throws IOException {
//        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        String name = UUID.randomUUID().toString();
        if (!new File(FILE_SAVE_PATH).exists())
            new File(FILE_SAVE_PATH).mkdirs();
        logger.info("file save path: " + FILE_SAVE_PATH + name);
        FileOutputStream out = new FileOutputStream(FILE_SAVE_PATH + name);
        out.write(file);
        out.flush();
        out.close();
//        return name + type;
    }
}
