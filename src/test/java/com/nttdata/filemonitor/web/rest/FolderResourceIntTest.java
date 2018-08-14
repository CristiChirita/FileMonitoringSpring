package com.nttdata.filemonitor.web.rest;

import com.nttdata.filemonitor.FileMonitoringApp;

import com.nttdata.filemonitor.domain.Folder;
import com.nttdata.filemonitor.repository.FolderRepository;
import com.nttdata.filemonitor.service.FolderService;
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

import java.util.List;


import static com.nttdata.filemonitor.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FolderResource REST controller.
 *
 * @see FolderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileMonitoringApp.class)
public class FolderResourceIntTest {

    private static final String DEFAULT_HASH = "AAAAAAAAAA";
    private static final String UPDATED_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private FolderRepository folderRepository;

    

    @Autowired
    private FolderService folderService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restFolderMockMvc;

    private Folder folder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FolderResource folderResource = new FolderResource(folderService);
        this.restFolderMockMvc = MockMvcBuilders.standaloneSetup(folderResource)
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
    public static Folder createEntity() {
        Folder folder = new Folder()
            .hash(DEFAULT_HASH)
            .name(DEFAULT_NAME);
        return folder;
    }

    @Before
    public void initTest() {
        folderRepository.deleteAll();
        folder = createEntity();
    }

    @Test
    public void createFolder() throws Exception {
        int databaseSizeBeforeCreate = folderRepository.findAll().size();

        // Create the Folder
        restFolderMockMvc.perform(post("/api/folders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isCreated());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeCreate + 1);
        Folder testFolder = folderList.get(folderList.size() - 1);
        assertThat(testFolder.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testFolder.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    public void createFolderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = folderRepository.findAll().size();

        // Create the Folder with an existing ID
        folder.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restFolderMockMvc.perform(post("/api/folders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllFolders() throws Exception {
        // Initialize the database
        folderRepository.save(folder);

        // Get all the folderList
        restFolderMockMvc.perform(get("/api/folders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(folder.getId())))
            .andExpect(jsonPath("$.[*].hash").value(hasItem(DEFAULT_HASH.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    

    @Test
    public void getFolder() throws Exception {
        // Initialize the database
        folderRepository.save(folder);

        // Get the folder
        restFolderMockMvc.perform(get("/api/folders/{id}", folder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(folder.getId()))
            .andExpect(jsonPath("$.hash").value(DEFAULT_HASH.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }
    @Test
    public void getNonExistingFolder() throws Exception {
        // Get the folder
        restFolderMockMvc.perform(get("/api/folders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateFolder() throws Exception {
        // Initialize the database
        folderService.save(folder);

        int databaseSizeBeforeUpdate = folderRepository.findAll().size();

        // Update the folder
        Folder updatedFolder = folderRepository.findById(folder.getId()).get();
        updatedFolder
            .hash(UPDATED_HASH)
            .name(UPDATED_NAME);

        restFolderMockMvc.perform(put("/api/folders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFolder)))
            .andExpect(status().isOk());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
        Folder testFolder = folderList.get(folderList.size() - 1);
        assertThat(testFolder.getHash()).isEqualTo(UPDATED_HASH);
        assertThat(testFolder.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    public void updateNonExistingFolder() throws Exception {
        int databaseSizeBeforeUpdate = folderRepository.findAll().size();

        // Create the Folder

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFolderMockMvc.perform(put("/api/folders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteFolder() throws Exception {
        // Initialize the database
        folderService.save(folder);

        int databaseSizeBeforeDelete = folderRepository.findAll().size();

        // Get the folder
        restFolderMockMvc.perform(delete("/api/folders/{id}", folder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Folder.class);
        Folder folder1 = new Folder();
        folder1.setId("id1");
        Folder folder2 = new Folder();
        folder2.setId(folder1.getId());
        assertThat(folder1).isEqualTo(folder2);
        folder2.setId("id2");
        assertThat(folder1).isNotEqualTo(folder2);
        folder1.setId(null);
        assertThat(folder1).isNotEqualTo(folder2);
    }
}
