package com.example.mypms.service;

import com.example.mypms.mapper.FileMangeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Service
@EnableAsync
public class FileManageService {
    @Value(value = "${file.save_path}")
    private String FILE_SAVE_PATH;
    @Autowired
    private FileMangeMapper fileMangeMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String getNewFileName(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String type = filename.substring(filename.lastIndexOf("."));
        return uuid + type;
    }

    @Async
    public void fileUpload(byte[] file, String name) throws IOException {
        if (!new File(FILE_SAVE_PATH).exists())
            new File(FILE_SAVE_PATH).mkdirs();
        logger.info("file save path: " + FILE_SAVE_PATH + name);
        FileOutputStream out = new FileOutputStream(FILE_SAVE_PATH + name);
        out.write(file);
        out.flush();
        out.close();
    }

    @Async
    public void deleteFile(String name) {
        File file = new File(FILE_SAVE_PATH + name);
        if (file.exists()) {
            file.delete();
        }
    }

    public ArrayList<Integer> getExpireContractPid() {
        return fileMangeMapper.getExpireContractPid();
    }

    public String getContractNameByPid(int pid) {
        return fileMangeMapper.getContractNameByPid(pid);
    }

    public int deleteExpireContract() {
        ArrayList<Integer> pids = getExpireContractPid();
        int count = 0;
        for (int pid : pids) {
            String name = getContractNameByPid(pid);
            deleteFile(name);
            count += fileMangeMapper.resetStatusByPid(pid);
        }
        return count;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyDeleteExpireContract() {
        int r = deleteExpireContract();
        logger.info("daily-check >> delete " + r + " expire contract");
    }
}
