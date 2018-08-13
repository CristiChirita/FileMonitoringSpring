package com.nttdata.filemonitor.repository;

import com.nttdata.filemonitor.domain.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Job entity.
 */
@SuppressWarnings("unused")
public interface MongoJobRepo extends MongoRepository<Job, String> {

}
