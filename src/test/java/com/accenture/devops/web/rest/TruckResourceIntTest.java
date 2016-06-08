package com.accenture.devops.web.rest;

import com.accenture.devops.DevopsApp;
import com.accenture.devops.domain.Truck;
import com.accenture.devops.repository.TruckRepository;
import com.accenture.devops.repository.search.TruckSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TruckResource REST controller.
 *
 * @see TruckResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DevopsApp.class)
@WebAppConfiguration
@IntegrationTest
public class TruckResourceIntTest {

    private static final String DEFAULT_TRUCK_CODE = "AAAAA";
    private static final String UPDATED_TRUCK_CODE = "BBBBB";
    private static final String DEFAULT_PLATE = "AAAAA";
    private static final String UPDATED_PLATE = "BBBBB";

    @Inject
    private TruckRepository truckRepository;

    @Inject
    private TruckSearchRepository truckSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTruckMockMvc;

    private Truck truck;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TruckResource truckResource = new TruckResource();
        ReflectionTestUtils.setField(truckResource, "truckSearchRepository", truckSearchRepository);
        ReflectionTestUtils.setField(truckResource, "truckRepository", truckRepository);
        this.restTruckMockMvc = MockMvcBuilders.standaloneSetup(truckResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        truckSearchRepository.deleteAll();
        truck = new Truck();
        truck.setTruckCode(DEFAULT_TRUCK_CODE);
        truck.setPlate(DEFAULT_PLATE);
    }

    @Test
    @Transactional
    public void createTruck() throws Exception {
        int databaseSizeBeforeCreate = truckRepository.findAll().size();

        // Create the Truck

        restTruckMockMvc.perform(post("/api/trucks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(truck)))
                .andExpect(status().isCreated());

        // Validate the Truck in the database
        List<Truck> trucks = truckRepository.findAll();
        assertThat(trucks).hasSize(databaseSizeBeforeCreate + 1);
        Truck testTruck = trucks.get(trucks.size() - 1);
        assertThat(testTruck.getTruckCode()).isEqualTo(DEFAULT_TRUCK_CODE);
        assertThat(testTruck.getPlate()).isEqualTo(DEFAULT_PLATE);

        // Validate the Truck in ElasticSearch
        Truck truckEs = truckSearchRepository.findOne(testTruck.getId());
        assertThat(truckEs).isEqualToComparingFieldByField(testTruck);
    }

    @Test
    @Transactional
    public void checkTruckCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = truckRepository.findAll().size();
        // set the field null
        truck.setTruckCode(null);

        // Create the Truck, which fails.

        restTruckMockMvc.perform(post("/api/trucks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(truck)))
                .andExpect(status().isBadRequest());

        List<Truck> trucks = truckRepository.findAll();
        assertThat(trucks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPlateIsRequired() throws Exception {
        int databaseSizeBeforeTest = truckRepository.findAll().size();
        // set the field null
        truck.setPlate(null);

        // Create the Truck, which fails.

        restTruckMockMvc.perform(post("/api/trucks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(truck)))
                .andExpect(status().isBadRequest());

        List<Truck> trucks = truckRepository.findAll();
        assertThat(trucks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTrucks() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        // Get all the trucks
        restTruckMockMvc.perform(get("/api/trucks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(truck.getId().intValue())))
                .andExpect(jsonPath("$.[*].truckCode").value(hasItem(DEFAULT_TRUCK_CODE.toString())))
                .andExpect(jsonPath("$.[*].plate").value(hasItem(DEFAULT_PLATE.toString())));
    }

    @Test
    @Transactional
    public void getTruck() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);

        // Get the truck
        restTruckMockMvc.perform(get("/api/trucks/{id}", truck.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(truck.getId().intValue()))
            .andExpect(jsonPath("$.truckCode").value(DEFAULT_TRUCK_CODE.toString()))
            .andExpect(jsonPath("$.plate").value(DEFAULT_PLATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTruck() throws Exception {
        // Get the truck
        restTruckMockMvc.perform(get("/api/trucks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTruck() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);
        truckSearchRepository.save(truck);
        int databaseSizeBeforeUpdate = truckRepository.findAll().size();

        // Update the truck
        Truck updatedTruck = new Truck();
        updatedTruck.setId(truck.getId());
        updatedTruck.setTruckCode(UPDATED_TRUCK_CODE);
        updatedTruck.setPlate(UPDATED_PLATE);

        restTruckMockMvc.perform(put("/api/trucks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTruck)))
                .andExpect(status().isOk());

        // Validate the Truck in the database
        List<Truck> trucks = truckRepository.findAll();
        assertThat(trucks).hasSize(databaseSizeBeforeUpdate);
        Truck testTruck = trucks.get(trucks.size() - 1);
        assertThat(testTruck.getTruckCode()).isEqualTo(UPDATED_TRUCK_CODE);
        assertThat(testTruck.getPlate()).isEqualTo(UPDATED_PLATE);

        // Validate the Truck in ElasticSearch
        Truck truckEs = truckSearchRepository.findOne(testTruck.getId());
        assertThat(truckEs).isEqualToComparingFieldByField(testTruck);
    }

    @Test
    @Transactional
    public void deleteTruck() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);
        truckSearchRepository.save(truck);
        int databaseSizeBeforeDelete = truckRepository.findAll().size();

        // Get the truck
        restTruckMockMvc.perform(delete("/api/trucks/{id}", truck.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean truckExistsInEs = truckSearchRepository.exists(truck.getId());
        assertThat(truckExistsInEs).isFalse();

        // Validate the database is empty
        List<Truck> trucks = truckRepository.findAll();
        assertThat(trucks).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTruck() throws Exception {
        // Initialize the database
        truckRepository.saveAndFlush(truck);
        truckSearchRepository.save(truck);

        // Search the truck
        restTruckMockMvc.perform(get("/api/_search/trucks?query=id:" + truck.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(truck.getId().intValue())))
            .andExpect(jsonPath("$.[*].truckCode").value(hasItem(DEFAULT_TRUCK_CODE.toString())))
            .andExpect(jsonPath("$.[*].plate").value(hasItem(DEFAULT_PLATE.toString())));
    }
}
