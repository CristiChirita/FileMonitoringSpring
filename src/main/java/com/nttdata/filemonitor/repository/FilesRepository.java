package com.nttdata.filemonitor.repository;

import com.nttdata.filemonitor.domain.Files;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Files entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FilesRepository extends MongoRepository<Files, String> {

}
