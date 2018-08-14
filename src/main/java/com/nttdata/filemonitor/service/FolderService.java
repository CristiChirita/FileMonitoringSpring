package com.nttdata.filemonitor.service;

import com.nttdata.filemonitor.domain.Folder;
import com.nttdata.filemonitor.repository.FolderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
/**
 * Service Implementation for managing Folder.
 */
@Service
public class FolderService {

    private final Logger log = LoggerFactory.getLogger(FolderService.class);

    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    /**
     * Save a folder.
     *
     * @param folder the entity to save
     * @return the persisted entity
     */
    public Folder save(Folder folder) {
        log.debug("Request to save Folder : {}", folder);        return folderRepository.save(folder);
    }

    /**
     * Get all the folders.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<Folder> findAll(Pageable pageable) {
        log.debug("Request to get all Folders");
        return folderRepository.findAll(pageable);
    }

    public List<Folder> findAll() {
        log.debug("Request to get all Folders");
        return folderRepository.findAll();
    }


    /**
     * Get one folder by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public Optional<Folder> findOne(String id) {
        log.debug("Request to get Folder : {}", id);
        return folderRepository.findById(id);
    }

    /**
     * Delete the folder by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Folder : {}", id);
        folderRepository.deleteById(id);
    }
}
