package com.nttdata.filemonitor.service.util.JobUtil;


import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.nttdata.filemonitor.domain.Files;
import com.nttdata.filemonitor.domain.Folder;
import com.nttdata.filemonitor.service.FilesService;
import com.nttdata.filemonitor.service.FolderService;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class MyTasklet implements Tasklet {

    @Autowired
    FilesService filesService;

    @Autowired
    FolderService folderService;

    @Value("${application.folder.location}")
    private String folderLocation;

    private File baseFile;

    private boolean login;

    public static FTPClient client = new FTPClient();


    @PostConstruct
    public void setUp() {
        baseFile = new File(folderLocation);
        try {
            client.connect("127.0.0.1");
            login = client.login("FileMonitoring", "pass");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        //System.out.println("Hello This is a sample example of spring batch");
        System.out.println();
        String data = "";
        //entities = filesService.findAll();
        List<Files> entities = new ArrayList<>();
        Stack<File> filesToVisit = new Stack<>();
        if (baseFile != null && baseFile.isDirectory())
            filesToVisit.push(baseFile);
        while (!filesToVisit.isEmpty()) {
            File current = filesToVisit.pop();
            if (current.isDirectory()) {
                List<File> files = expand(current);
                for (File file : files) {
                    filesToVisit.push(file);
                }
            } else {
                long lastModified = current.lastModified();
                Instant i = Instant.ofEpochMilli(lastModified);
                ZonedDateTime date = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
                String path = current.getAbsolutePath();
                Files entity = new Files().name(current.getName()).location(path).lastModified(date);
                entities.add(entity);
                data += current.getAbsolutePath() + current.lastModified();
            }
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte[] byteData = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            //Convert to hex; see https://www.mkyong.com/java/java-md5-hashing-example/
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        String md5Hash = sb.toString();
        List<Folder> folderList = folderService.findAll();
        Folder existing = null;
        for (Folder current : folderList) {
            if (current.getName().equals(folderLocation)) {
                existing = current;
                break;
            }
        }
        if (existing == null) {
            Folder folder = new Folder().hash(md5Hash).setFiles(entities).name(folderLocation);
            folderService.save(folder);
        }
        else {
            if (!existing.getHash().equals(md5Hash)) {
                List<String> modifiedFiles = new ArrayList<>();
                //Can compute file differences here
                for (Files file : entities) {
                    Files current = getExistingFile(file.getLocation(), existing.getFiles());
                    if (current == null) {
                        modifiedFiles.add(file.getLocation());
                    }
                    else {
                        if (!file.getLastModified().equals(current.getLastModified())) {
                            modifiedFiles.add(file.getLocation());
                        }
                    }
                }

                existing.setHash(md5Hash);
                existing.setFiles(entities);
                folderService.save(existing);

                String topic = "files";

                Message message = Message.builder()
                    .putData("Name", folderLocation)
                    .putData("Hash", md5Hash)
                    .putData("Modified files", modifiedFiles.toString())
                    .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setPriority(AndroidConfig.Priority.NORMAL)
                        .setNotification(AndroidNotification.builder()
                            .setTitle("File modified!")
                            .setBody("A file in " + folderLocation + " was modified")
                            .build())
                        .build())
                    .setTopic(topic)
                    .build();

                String response = FirebaseMessaging.getInstance().send(message, false);
                System.out.println("Sent message: " + response);
            }
        }
            if (login) {

                System.out.println("Connection established...");

                // Try to logout and return the respective boolean value

            } else {
                System.out.println("Connection fail...");
            }

        return RepeatStatus.FINISHED;
    }

    private List<File> expand(File file) {
        ArrayList<File> files = new ArrayList<>();
        if (file.isDirectory()) {
            String[] subnote = file.list();
            for (String filename : subnote) {
                files.add(new File(file, filename));
            }
        }
        return files;
    }

    private Files getExistingFile(String name, List<Files> filesList) {
        for (Files file : filesList) {
            if (file.getLocation().equals(name)) return file;
        }
        return null;
    }
}
