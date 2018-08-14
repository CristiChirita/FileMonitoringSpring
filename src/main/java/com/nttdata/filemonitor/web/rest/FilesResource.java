package com.nttdata.filemonitor.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nttdata.filemonitor.domain.Files;
import com.nttdata.filemonitor.service.FilesService;
import com.nttdata.filemonitor.web.rest.errors.BadRequestAlertException;
import com.nttdata.filemonitor.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Files.
 */
@RestController
@RequestMapping("/api")
public class FilesResource {

    private final Logger log = LoggerFactory.getLogger(FilesResource.class);

    private static final String ENTITY_NAME = "files";

    private final FilesService filesService;

    public FilesResource(FilesService filesService) {
        this.filesService = filesService;
    }

    /**
     * POST  /files : Create a new files.
     *
     * @param files the files to create
     * @return the ResponseEntity with status 201 (Created) and with body the new files, or with status 400 (Bad Request) if the files has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/files")
    @Timed
    public ResponseEntity<Files> createFiles(@RequestBody Files files) throws URISyntaxException {
        log.debug("REST request to save Files : {}", files);
        if (files.getId() != null) {
            throw new BadRequestAlertException("A new files cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Files result = filesService.save(files);
        return ResponseEntity.created(new URI("/api/files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /files : Updates an existing files.
     *
     * @param files the files to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated files,
     * or with status 400 (Bad Request) if the files is not valid,
     * or with status 500 (Internal Server Error) if the files couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/files")
    @Timed
    public ResponseEntity<Files> updateFiles(@RequestBody Files files) throws URISyntaxException {
        log.debug("REST request to update Files : {}", files);
        if (files.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Files result = filesService.save(files);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, files.getId().toString()))
            .body(result);
    }

    /**
     * GET  /files : get all the files.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of files in body
     */
    @GetMapping("/files")
    @Timed
    public List<Files> getAllFiles() {
        log.debug("REST request to get all Files");
        return filesService.findAll();
    }

    /**
     * GET  /files/:id : get the "id" files.
     *
     * @param id the id of the files to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the files, or with status 404 (Not Found)
     */
    @GetMapping("/files/{id}")
    @Timed
    public ResponseEntity<Files> getFiles(@PathVariable String id) {
        log.debug("REST request to get Files : {}", id);
        Optional<Files> files = filesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(files);
    }

    /**
     * DELETE  /files/:id : delete the "id" files.
     *
     * @param id the id of the files to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/files/{id}")
    @Timed
    public ResponseEntity<Void> deleteFiles(@PathVariable String id) {
        log.debug("REST request to delete Files : {}", id);
        filesService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
