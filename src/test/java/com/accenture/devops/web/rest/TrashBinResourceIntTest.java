package com.accenture.devops.web.rest;

import com.accenture.devops.DevopsApp;
import com.accenture.devops.domain.TrashBin;
import com.accenture.devops.repository.TrashBinRepository;
import com.accenture.devops.repository.search.TrashBinSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TrashBinResource REST controller.
 *
 * @see TrashBinResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DevopsApp.class)
@WebAppConfiguration
@IntegrationTest
public class TrashBinResourceIntTest {

    private static final String DEFAULT_TRASH_BIN_CODE = "AAAAA";
    private static final String UPDATED_TRASH_BIN_CODE = "BBBBB";

    private static final Double DEFAULT_LATITUTE = 1D;
    private static final Double UPDATED_LATITUTE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final byte[] DEFAULT_BAR_CODE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BAR_CODE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_BAR_CODE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BAR_CODE_CONTENT_TYPE = "image/png";

    @Inject
    private TrashBinRepository trashBinRepository;

    @Inject
    private TrashBinSearchRepository trashBinSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTrashBinMockMvc;

    private TrashBin trashBin;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TrashBinResource trashBinResource = new TrashBinResource();
        ReflectionTestUtils.setField(trashBinResource, "trashBinSearchRepository", trashBinSearchRepository);
        ReflectionTestUtils.setField(trashBinResource, "trashBinRepository", trashBinRepository);
        this.restTrashBinMockMvc = MockMvcBuilders.standaloneSetup(trashBinResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        trashBinSearchRepository.deleteAll();
        trashBin = new TrashBin();
        trashBin.setTrashBinCode(DEFAULT_TRASH_BIN_CODE);
        trashBin.setLatitute(DEFAULT_LATITUTE);
        trashBin.setLongitude(DEFAULT_LONGITUDE);
        trashBin.setBarCode(DEFAULT_BAR_CODE);
        trashBin.setBarCodeContentType(DEFAULT_BAR_CODE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createTrashBin() throws Exception {
        int databaseSizeBeforeCreate = trashBinRepository.findAll().size();

        // Create the TrashBin

        restTrashBinMockMvc.perform(post("/api/trash-bins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trashBin)))
                .andExpect(status().isCreated());

        // Validate the TrashBin in the database
        List<TrashBin> trashBins = trashBinRepository.findAll();
        assertThat(trashBins).hasSize(databaseSizeBeforeCreate + 1);
        TrashBin testTrashBin = trashBins.get(trashBins.size() - 1);
        assertThat(testTrashBin.getTrashBinCode()).isEqualTo(DEFAULT_TRASH_BIN_CODE);
        assertThat(testTrashBin.getLatitute()).isEqualTo(DEFAULT_LATITUTE);
        assertThat(testTrashBin.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testTrashBin.getBarCode()).isEqualTo(DEFAULT_BAR_CODE);
        assertThat(testTrashBin.getBarCodeContentType()).isEqualTo(DEFAULT_BAR_CODE_CONTENT_TYPE);

        // Validate the TrashBin in ElasticSearch
        TrashBin trashBinEs = trashBinSearchRepository.findOne(testTrashBin.getId());
        assertThat(trashBinEs).isEqualToComparingFieldByField(testTrashBin);
    }

    @Test
    @Transactional
    public void checkTrashBinCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = trashBinRepository.findAll().size();
        // set the field null
        trashBin.setTrashBinCode(null);

        // Create the TrashBin, which fails.

        restTrashBinMockMvc.perform(post("/api/trash-bins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trashBin)))
                .andExpect(status().isBadRequest());

        List<TrashBin> trashBins = trashBinRepository.findAll();
        assertThat(trashBins).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLatituteIsRequired() throws Exception {
        int databaseSizeBeforeTest = trashBinRepository.findAll().size();
        // set the field null
        trashBin.setLatitute(null);

        // Create the TrashBin, which fails.

        restTrashBinMockMvc.perform(post("/api/trash-bins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trashBin)))
                .andExpect(status().isBadRequest());

        List<TrashBin> trashBins = trashBinRepository.findAll();
        assertThat(trashBins).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = trashBinRepository.findAll().size();
        // set the field null
        trashBin.setLongitude(null);

        // Create the TrashBin, which fails.

        restTrashBinMockMvc.perform(post("/api/trash-bins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trashBin)))
                .andExpect(status().isBadRequest());

        List<TrashBin> trashBins = trashBinRepository.findAll();
        assertThat(trashBins).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBarCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = trashBinRepository.findAll().size();
        // set the field null
        trashBin.setBarCode(null);

        // Create the TrashBin, which fails.

        restTrashBinMockMvc.perform(post("/api/trash-bins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(trashBin)))
                .andExpect(status().isBadRequest());

        List<TrashBin> trashBins = trashBinRepository.findAll();
        assertThat(trashBins).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTrashBins() throws Exception {
        // Initialize the database
        trashBinRepository.saveAndFlush(trashBin);

        // Get all the trashBins
        restTrashBinMockMvc.perform(get("/api/trash-bins?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(trashBin.getId().intValue())))
                .andExpect(jsonPath("$.[*].trashBinCode").value(hasItem(DEFAULT_TRASH_BIN_CODE.toString())))
                .andExpect(jsonPath("$.[*].latitute").value(hasItem(DEFAULT_LATITUTE.doubleValue())))
                .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
                .andExpect(jsonPath("$.[*].barCodeContentType").value(hasItem(DEFAULT_BAR_CODE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].barCode").value(hasItem(Base64Utils.encodeToString(DEFAULT_BAR_CODE))));
    }

    @Test
    @Transactional
    public void getTrashBin() throws Exception {
        // Initialize the database
        trashBinRepository.saveAndFlush(trashBin);

        // Get the trashBin
        restTrashBinMockMvc.perform(get("/api/trash-bins/{id}", trashBin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(trashBin.getId().intValue()))
            .andExpect(jsonPath("$.trashBinCode").value(DEFAULT_TRASH_BIN_CODE.toString()))
            .andExpect(jsonPath("$.latitute").value(DEFAULT_LATITUTE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.barCodeContentType").value(DEFAULT_BAR_CODE_CONTENT_TYPE))
            .andExpect(jsonPath("$.barCode").value(Base64Utils.encodeToString(DEFAULT_BAR_CODE)));
    }

    @Test
    @Transactional
    public void getNonExistingTrashBin() throws Exception {
        // Get the trashBin
        restTrashBinMockMvc.perform(get("/api/trash-bins/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrashBin() throws Exception {
        // Initialize the database
        trashBinRepository.saveAndFlush(trashBin);
        trashBinSearchRepository.save(trashBin);
        int databaseSizeBeforeUpdate = trashBinRepository.findAll().size();

        // Update the trashBin
        TrashBin updatedTrashBin = new TrashBin();
        updatedTrashBin.setId(trashBin.getId());
        updatedTrashBin.setTrashBinCode(UPDATED_TRASH_BIN_CODE);
        updatedTrashBin.setLatitute(UPDATED_LATITUTE);
        updatedTrashBin.setLongitude(UPDATED_LONGITUDE);
        updatedTrashBin.setBarCode(UPDATED_BAR_CODE);
        updatedTrashBin.setBarCodeContentType(UPDATED_BAR_CODE_CONTENT_TYPE);

        restTrashBinMockMvc.perform(put("/api/trash-bins")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTrashBin)))
                .andExpect(status().isOk());

        // Validate the TrashBin in the database
        List<TrashBin> trashBins = trashBinRepository.findAll();
        assertThat(trashBins).hasSize(databaseSizeBeforeUpdate);
        TrashBin testTrashBin = trashBins.get(trashBins.size() - 1);
        assertThat(testTrashBin.getTrashBinCode()).isEqualTo(UPDATED_TRASH_BIN_CODE);
        assertThat(testTrashBin.getLatitute()).isEqualTo(UPDATED_LATITUTE);
        assertThat(testTrashBin.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testTrashBin.getBarCode()).isEqualTo(UPDATED_BAR_CODE);
        assertThat(testTrashBin.getBarCodeContentType()).isEqualTo(UPDATED_BAR_CODE_CONTENT_TYPE);

        // Validate the TrashBin in ElasticSearch
        TrashBin trashBinEs = trashBinSearchRepository.findOne(testTrashBin.getId());
        assertThat(trashBinEs).isEqualToComparingFieldByField(testTrashBin);
    }

    @Test
    @Transactional
    public void deleteTrashBin() throws Exception {
        // Initialize the database
        trashBinRepository.saveAndFlush(trashBin);
        trashBinSearchRepository.save(trashBin);
        int databaseSizeBeforeDelete = trashBinRepository.findAll().size();

        // Get the trashBin
        restTrashBinMockMvc.perform(delete("/api/trash-bins/{id}", trashBin.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean trashBinExistsInEs = trashBinSearchRepository.exists(trashBin.getId());
        assertThat(trashBinExistsInEs).isFalse();

        // Validate the database is empty
        List<TrashBin> trashBins = trashBinRepository.findAll();
        assertThat(trashBins).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTrashBin() throws Exception {
        // Initialize the database
        trashBinRepository.saveAndFlush(trashBin);
        trashBinSearchRepository.save(trashBin);

        // Search the trashBin
        restTrashBinMockMvc.perform(get("/api/_search/trash-bins?query=id:" + trashBin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trashBin.getId().intValue())))
            .andExpect(jsonPath("$.[*].trashBinCode").value(hasItem(DEFAULT_TRASH_BIN_CODE.toString())))
            .andExpect(jsonPath("$.[*].latitute").value(hasItem(DEFAULT_LATITUTE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].barCodeContentType").value(hasItem(DEFAULT_BAR_CODE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].barCode").value(hasItem(Base64Utils.encodeToString(DEFAULT_BAR_CODE))));
    }
}
