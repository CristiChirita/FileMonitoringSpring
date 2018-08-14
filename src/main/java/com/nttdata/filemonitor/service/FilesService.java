package com.nttdata.filemonitor.service;

import com.nttdata.filemonitor.domain.Files;
import com.nttdata.filemonitor.repository.FilesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
/**
 * Service Implementation for managing Files.
 */
@Service
public class FilesService {

    private final Logger log = LoggerFactory.getLogger(FilesService.class);

    private final FilesRepository filesRepository;

    public FilesService(FilesRepository filesRepository) {
        this.filesRepository = filesRepository;
    }

    /**
     * Save a files.
     *
     * @param files the entity to save
     * @return the persisted entity
     */
    public Files save(Files files) {
        log.debug("Request to save Files : {}", files);        return filesRepository.save(files);
    }

    /**
     * Get all the files.
     *
     * @return the list of entities
     */
    public List<Files> findAll() {
        log.debug("Request to get all Files");
        return filesRepository.findAll();
    }


    /**
     * Get one files by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public Optional<Files> findOne(String id) {
        log.debug("Request to get Files : {}", id);
        return filesRepository.findById(id);
    }

    /**
     * Delete the files by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Files : {}", id);
        filesRepository.deleteById(id);
    }
}
