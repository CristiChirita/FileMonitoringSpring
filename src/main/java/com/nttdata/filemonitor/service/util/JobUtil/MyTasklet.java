package com.nttdata.filemonitor.service.util.JobUtil;


import com.nttdata.filemonitor.domain.Files;
import com.nttdata.filemonitor.service.FilesService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class MyTasklet implements Tasklet {

    @Autowired
    FilesService filesService;

    @Value("${application.folder.location}")
    private String folderLocation;

    private File baseFile;

    private List<Files> entities;

    private List<Boolean> usage;

    @PostConstruct
    public void setUp() {
         baseFile = new File(folderLocation);
    }

    @Override
    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        //System.out.println("Hello This is a sample example of spring batch");
        System.out.print("\n");
        entities = filesService.findAll();
        usage = new ArrayList<>(Collections.nCopies(entities.size(), false));
        if (baseFile != null && baseFile.isDirectory())
            navigate(baseFile);
        for (Boolean elem : usage) {
            if (elem == false) {
                System.out.println("Found deleted entry, removing...");
                int index = usage.indexOf(elem);
                Files entity = entities.get(index);
                filesService.delete(entity.getId());
            }
        }
        return RepeatStatus.FINISHED;
    }

    private void navigate(File file) {
        //System.out.println(file.getAbsoluteFile());
        if (file.isDirectory()) {
            String[] subNote = file.list();
            for (String filename : subNote) {
                navigate(new File(file, filename));
            }
        } else {
            checkFile(file);
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
                //System.out.println("File found...");
                int index = entities.indexOf(f);
                usage.set(index, true);
                if (f.getLastModified().equals(date)) {
                    //File is in db and was not modified since
                }
                else {
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
