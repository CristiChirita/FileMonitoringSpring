package com.nttdata.filemonitor.web.rest;

import com.nttdata.filemonitor.FileMonitoringApp;

import com.nttdata.filemonitor.domain.Files;
import com.nttdata.filemonitor.repository.FilesRepository;
import com.nttdata.filemonitor.service.FilesService;
import com.nttdata.filemonitor.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;


import static com.nttdata.filemonitor.web.rest.TestUtil.sameInstant;
import static com.nttdata.filemonitor.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FilesResource REST controller.
 *
 * @see FilesResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileMonitoringApp.class)
public class FilesResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private FilesRepository filesRepository;

    

    @Autowired
    private FilesService filesService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restFilesMockMvc;

    private Files files;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FilesResource filesResource = new FilesResource(filesService);
        this.restFilesMockMvc = MockMvcBuilders.standaloneSetup(filesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Files createEntity() {
        Files files = new Files()
            .name(DEFAULT_NAME)
            .location(DEFAULT_LOCATION)
            .lastModified(DEFAULT_LAST_MODIFIED);
        return files;
    }

    @Before
    public void initTest() {
        filesRepository.deleteAll();
        files = createEntity();
    }

    @Test
    public void createFiles() throws Exception {
        int databaseSizeBeforeCreate = filesRepository.findAll().size();

        // Create the Files
        restFilesMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(files)))
            .andExpect(status().isCreated());

        // Validate the Files in the database
        List<Files> filesList = filesRepository.findAll();
        assertThat(filesList).hasSize(databaseSizeBeforeCreate + 1);
        Files testFiles = filesList.get(filesList.size() - 1);
        assertThat(testFiles.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFiles.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testFiles.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
    }

    @Test
    public void createFilesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = filesRepository.findAll().size();

        // Create the Files with an existing ID
        files.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restFilesMockMvc.perform(post("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(files)))
            .andExpect(status().isBadRequest());

        // Validate the Files in the database
        List<Files> filesList = filesRepository.findAll();
        assertThat(filesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllFiles() throws Exception {
        // Initialize the database
        filesRepository.save(files);

        // Get all the filesList
        restFilesMockMvc.perform(get("/api/files?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(files.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED))));
    }
    

    @Test
    public void getFiles() throws Exception {
        // Initialize the database
        filesRepository.save(files);

        // Get the files
        restFilesMockMvc.perform(get("/api/files/{id}", files.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(files.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.lastModified").value(sameInstant(DEFAULT_LAST_MODIFIED)));
    }
    @Test
    public void getNonExistingFiles() throws Exception {
        // Get the files
        restFilesMockMvc.perform(get("/api/files/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateFiles() throws Exception {
        // Initialize the database
        filesService.save(files);

        int databaseSizeBeforeUpdate = filesRepository.findAll().size();

        // Update the files
        Files updatedFiles = filesRepository.findById(files.getId()).get();
        updatedFiles
            .name(UPDATED_NAME)
            .location(UPDATED_LOCATION)
            .lastModified(UPDATED_LAST_MODIFIED);

        restFilesMockMvc.perform(put("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFiles)))
            .andExpect(status().isOk());

        // Validate the Files in the database
        List<Files> filesList = filesRepository.findAll();
        assertThat(filesList).hasSize(databaseSizeBeforeUpdate);
        Files testFiles = filesList.get(filesList.size() - 1);
        assertThat(testFiles.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFiles.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testFiles.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
    }

    @Test
    public void updateNonExistingFiles() throws Exception {
        int databaseSizeBeforeUpdate = filesRepository.findAll().size();

        // Create the Files

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFilesMockMvc.perform(put("/api/files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(files)))
            .andExpect(status().isBadRequest());

        // Validate the Files in the database
        List<Files> filesList = filesRepository.findAll();
        assertThat(filesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteFiles() throws Exception {
        // Initialize the database
        filesService.save(files);

        int databaseSizeBeforeDelete = filesRepository.findAll().size();

        // Get the files
        restFilesMockMvc.perform(delete("/api/files/{id}", files.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Files> filesList = filesRepository.findAll();
        assertThat(filesList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Files.class);
        Files files1 = new Files();
        files1.setId("id1");
        Files files2 = new Files();
        files2.setId(files1.getId());
        assertThat(files1).isEqualTo(files2);
        files2.setId("id2");
        assertThat(files1).isNotEqualTo(files2);
        files1.setId(null);
        assertThat(files1).isNotEqualTo(files2);
    }
}
