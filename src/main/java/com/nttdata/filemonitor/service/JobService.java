package com.nttdata.filemonitor.service;

import com.nttdata.filemonitor.domain.Job;
import com.nttdata.filemonitor.repository.MongoJobRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
/**
 * Service Implementation for managing Job.
 */
@Service
public class JobService {

    private final Logger log = LoggerFactory.getLogger(JobService.class);

    private final MongoJobRepo jobRepository;

    public JobService(MongoJobRepo jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Save a job.
     *
     * @param job the entity to save
     * @return the persisted entity
     */
    public Job save(Job job) {
        log.debug("Request to save Job : {}", job);        return jobRepository.save(job);
    }

    /**
     * Get all the jobs.
     *
     * @return the list of entities
     */
    public List<Job> findAll() {
        log.debug("Request to get all Jobs");
        List<Job> list = jobRepository.findAll();
        int size = list.size();
        return list.subList(size - 5, size);
    }


    /**
     * Get one job by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public Optional<Job> findOne(String id) {
        log.debug("Request to get Job : {}", id);
        return jobRepository.findById(id);
    }

    /**
     * Delete the job by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Job : {}", id);
        jobRepository.deleteById(id);
    }
}
