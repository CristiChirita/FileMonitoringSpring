package com.nttdata.filemonitor.service.util.JobUtil;


import com.nttdata.filemonitor.domain.Files;
import com.nttdata.filemonitor.domain.Folder;
import com.nttdata.filemonitor.service.FilesService;
import com.nttdata.filemonitor.service.FolderService;
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

    private List<Files> entities;


    @PostConstruct
    public void setUp() {
        baseFile = new File(folderLocation);
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
                StringBuilder contentBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(current.getAbsolutePath()))) {
                    String sCurrentLine;
//                    while ((sCurrentLine = br.readLine()) != null) {
//                        contentBuilder.append(sCurrentLine).append("\n");
//                    }
                    data += current.getAbsolutePath() + current.lastModified();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte[] byteData = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
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
                existing.setHash(md5Hash);
                existing.setFiles(entities);
                folderService.save(existing);
                //Can compute file differences here
            }
        }
        System.out.println(md5Hash);

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

    private void navigate(File file) {
        if (file.isDirectory()) {
            String[] subNote = file.list();
            for (String filename : subNote) {
                navigate(new File(file, filename));
            }
        } else {
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    contentBuilder.append(sCurrentLine).append("\n");
                }
                // data += contentBuilder.toString();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    //TODO: fix this
    private void checkFile(File file) {
        long lastModified = file.lastModified();
        Instant i = Instant.ofEpochMilli(lastModified);
        ZonedDateTime date = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        String path = file.getAbsolutePath();
        for (Files f : entities) {
            if (f.getLocation().equals(path)) {
                int index = entities.indexOf(f);
                //usage.set(index, true);
                if (f.getLastModified().equals(date)) {
                    //File is in db and was not modified since
                } else {
                    //File was modified since it was registered
                    System.out.println("Found modified file.");
                    f.setLastModified(date);
                    filesService.save(f);
                }
                return;
            }
        }
        //New file, register it
        System.out.println("New file found: registering...");
        Files entity = new Files().name(file.getName()).location(path).lastModified(date);
        filesService.save(entity);
    }
}
