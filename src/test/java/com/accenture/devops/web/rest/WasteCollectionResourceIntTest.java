package com.accenture.devops.web.rest;

import com.accenture.devops.DevopsApp;
import com.accenture.devops.domain.WasteCollection;
import com.accenture.devops.repository.WasteCollectionRepository;
import com.accenture.devops.repository.search.WasteCollectionSearchRepository;

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.accenture.devops.domain.enumeration.WasteLevel;

/**
 * Test class for the WasteCollectionResource REST controller.
 *
 * @see WasteCollectionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DevopsApp.class)
@WebAppConfiguration
@IntegrationTest
public class WasteCollectionResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_SERVER_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_SERVER_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_SERVER_TIMESTAMP_STR = dateTimeFormatter.format(DEFAULT_SERVER_TIMESTAMP);

    private static final ZonedDateTime DEFAULT_TRUCK_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_TRUCK_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_TRUCK_TIMESTAMP_STR = dateTimeFormatter.format(DEFAULT_TRUCK_TIMESTAMP);

    private static final WasteLevel DEFAULT_WASTE_LEVEL = WasteLevel.EMPTY;
    private static final WasteLevel UPDATED_WASTE_LEVEL = WasteLevel.MEDIUM;

    @Inject
    private WasteCollectionRepository wasteCollectionRepository;

    @Inject
    private WasteCollectionSearchRepository wasteCollectionSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restWasteCollectionMockMvc;

    private WasteCollection wasteCollection;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WasteCollectionResource wasteCollectionResource = new WasteCollectionResource();
        ReflectionTestUtils.setField(wasteCollectionResource, "wasteCollectionSearchRepository", wasteCollectionSearchRepository);
        ReflectionTestUtils.setField(wasteCollectionResource, "wasteCollectionRepository", wasteCollectionRepository);
        this.restWasteCollectionMockMvc = MockMvcBuilders.standaloneSetup(wasteCollectionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        wasteCollectionSearchRepository.deleteAll();
        wasteCollection = new WasteCollection();
        wasteCollection.setServerTimestamp(DEFAULT_SERVER_TIMESTAMP);
        wasteCollection.setTruckTimestamp(DEFAULT_TRUCK_TIMESTAMP);
        wasteCollection.setWasteLevel(DEFAULT_WASTE_LEVEL);
    }

    @Test
    @Transactional
    public void createWasteCollection() throws Exception {
        int databaseSizeBeforeCreate = wasteCollectionRepository.findAll().size();

        // Create the WasteCollection

        restWasteCollectionMockMvc.perform(post("/api/waste-collections")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wasteCollection)))
                .andExpect(status().isCreated());

        // Validate the WasteCollection in the database
        List<WasteCollection> wasteCollections = wasteCollectionRepository.findAll();
        assertThat(wasteCollections).hasSize(databaseSizeBeforeCreate + 1);
        WasteCollection testWasteCollection = wasteCollections.get(wasteCollections.size() - 1);
        assertThat(testWasteCollection.getServerTimestamp()).isEqualTo(DEFAULT_SERVER_TIMESTAMP);
        assertThat(testWasteCollection.getTruckTimestamp()).isEqualTo(DEFAULT_TRUCK_TIMESTAMP);
        assertThat(testWasteCollection.getWasteLevel()).isEqualTo(DEFAULT_WASTE_LEVEL);

        // Validate the WasteCollection in ElasticSearch
        WasteCollection wasteCollectionEs = wasteCollectionSearchRepository.findOne(testWasteCollection.getId());
        assertThat(wasteCollectionEs).isEqualToComparingFieldByField(testWasteCollection);
    }

    @Test
    @Transactional
    public void checkServerTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = wasteCollectionRepository.findAll().size();
        // set the field null
        wasteCollection.setServerTimestamp(null);

        // Create the WasteCollection, which fails.

        restWasteCollectionMockMvc.perform(post("/api/waste-collections")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wasteCollection)))
                .andExpect(status().isBadRequest());

        List<WasteCollection> wasteCollections = wasteCollectionRepository.findAll();
        assertThat(wasteCollections).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTruckTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = wasteCollectionRepository.findAll().size();
        // set the field null
        wasteCollection.setTruckTimestamp(null);

        // Create the WasteCollection, which fails.

        restWasteCollectionMockMvc.perform(post("/api/waste-collections")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wasteCollection)))
                .andExpect(status().isBadRequest());

        List<WasteCollection> wasteCollections = wasteCollectionRepository.findAll();
        assertThat(wasteCollections).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWasteLevelIsRequired() throws Exception {
        int databaseSizeBeforeTest = wasteCollectionRepository.findAll().size();
        // set the field null
        wasteCollection.setWasteLevel(null);

        // Create the WasteCollection, which fails.

        restWasteCollectionMockMvc.perform(post("/api/waste-collections")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wasteCollection)))
                .andExpect(status().isBadRequest());

        List<WasteCollection> wasteCollections = wasteCollectionRepository.findAll();
        assertThat(wasteCollections).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWasteCollections() throws Exception {
        // Initialize the database
        wasteCollectionRepository.saveAndFlush(wasteCollection);

        // Get all the wasteCollections
        restWasteCollectionMockMvc.perform(get("/api/waste-collections?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(wasteCollection.getId().intValue())))
                .andExpect(jsonPath("$.[*].serverTimestamp").value(hasItem(DEFAULT_SERVER_TIMESTAMP_STR)))
                .andExpect(jsonPath("$.[*].truckTimestamp").value(hasItem(DEFAULT_TRUCK_TIMESTAMP_STR)))
                .andExpect(jsonPath("$.[*].wasteLevel").value(hasItem(DEFAULT_WASTE_LEVEL.toString())));
    }

    @Test
    @Transactional
    public void getWasteCollection() throws Exception {
        // Initialize the database
        wasteCollectionRepository.saveAndFlush(wasteCollection);

        // Get the wasteCollection
        restWasteCollectionMockMvc.perform(get("/api/waste-collections/{id}", wasteCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(wasteCollection.getId().intValue()))
            .andExpect(jsonPath("$.serverTimestamp").value(DEFAULT_SERVER_TIMESTAMP_STR))
            .andExpect(jsonPath("$.truckTimestamp").value(DEFAULT_TRUCK_TIMESTAMP_STR))
            .andExpect(jsonPath("$.wasteLevel").value(DEFAULT_WASTE_LEVEL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWasteCollection() throws Exception {
        // Get the wasteCollection
        restWasteCollectionMockMvc.perform(get("/api/waste-collections/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWasteCollection() throws Exception {
        // Initialize the database
        wasteCollectionRepository.saveAndFlush(wasteCollection);
        wasteCollectionSearchRepository.save(wasteCollection);
        int databaseSizeBeforeUpdate = wasteCollectionRepository.findAll().size();

        // Update the wasteCollection
        WasteCollection updatedWasteCollection = new WasteCollection();
        updatedWasteCollection.setId(wasteCollection.getId());
        updatedWasteCollection.setServerTimestamp(UPDATED_SERVER_TIMESTAMP);
        updatedWasteCollection.setTruckTimestamp(UPDATED_TRUCK_TIMESTAMP);
        updatedWasteCollection.setWasteLevel(UPDATED_WASTE_LEVEL);

        restWasteCollectionMockMvc.perform(put("/api/waste-collections")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWasteCollection)))
                .andExpect(status().isOk());

        // Validate the WasteCollection in the database
        List<WasteCollection> wasteCollections = wasteCollectionRepository.findAll();
        assertThat(wasteCollections).hasSize(databaseSizeBeforeUpdate);
        WasteCollection testWasteCollection = wasteCollections.get(wasteCollections.size() - 1);
        assertThat(testWasteCollection.getServerTimestamp()).isEqualTo(UPDATED_SERVER_TIMESTAMP);
        assertThat(testWasteCollection.getTruckTimestamp()).isEqualTo(UPDATED_TRUCK_TIMESTAMP);
        assertThat(testWasteCollection.getWasteLevel()).isEqualTo(UPDATED_WASTE_LEVEL);

        // Validate the WasteCollection in ElasticSearch
        WasteCollection wasteCollectionEs = wasteCollectionSearchRepository.findOne(testWasteCollection.getId());
        assertThat(wasteCollectionEs).isEqualToComparingFieldByField(testWasteCollection);
    }

    @Test
    @Transactional
    public void deleteWasteCollection() throws Exception {
        // Initialize the database
        wasteCollectionRepository.saveAndFlush(wasteCollection);
        wasteCollectionSearchRepository.save(wasteCollection);
        int databaseSizeBeforeDelete = wasteCollectionRepository.findAll().size();

        // Get the wasteCollection
        restWasteCollectionMockMvc.perform(delete("/api/waste-collections/{id}", wasteCollection.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean wasteCollectionExistsInEs = wasteCollectionSearchRepository.exists(wasteCollection.getId());
        assertThat(wasteCollectionExistsInEs).isFalse();

        // Validate the database is empty
        List<WasteCollection> wasteCollections = wasteCollectionRepository.findAll();
        assertThat(wasteCollections).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchWasteCollection() throws Exception {
        // Initialize the database
        wasteCollectionRepository.saveAndFlush(wasteCollection);
        wasteCollectionSearchRepository.save(wasteCollection);

        // Search the wasteCollection
        restWasteCollectionMockMvc.perform(get("/api/_search/waste-collections?query=id:" + wasteCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wasteCollection.getId().intValue())))
            .andExpect(jsonPath("$.[*].serverTimestamp").value(hasItem(DEFAULT_SERVER_TIMESTAMP_STR)))
            .andExpect(jsonPath("$.[*].truckTimestamp").value(hasItem(DEFAULT_TRUCK_TIMESTAMP_STR)))
            .andExpect(jsonPath("$.[*].wasteLevel").value(hasItem(DEFAULT_WASTE_LEVEL.toString())));
    }
}
