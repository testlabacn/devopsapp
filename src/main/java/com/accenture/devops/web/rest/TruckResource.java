package com.accenture.devops.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.accenture.devops.domain.Truck;
import com.accenture.devops.repository.TruckRepository;
import com.accenture.devops.repository.search.TruckSearchRepository;
import com.accenture.devops.web.rest.util.HeaderUtil;
import com.accenture.devops.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Truck.
 */
@RestController
@RequestMapping("/api")
public class TruckResource {

    private final Logger log = LoggerFactory.getLogger(TruckResource.class);
        
    @Inject
    private TruckRepository truckRepository;
    
    @Inject
    private TruckSearchRepository truckSearchRepository;
    
    /**
     * POST  /trucks : Create a new truck.
     *
     * @param truck the truck to create
     * @return the ResponseEntity with status 201 (Created) and with body the new truck, or with status 400 (Bad Request) if the truck has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trucks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Truck> createTruck(@Valid @RequestBody Truck truck) throws URISyntaxException {
        log.debug("REST request to save Truck : {}", truck);
        if (truck.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("truck", "idexists", "A new truck cannot already have an ID")).body(null);
        }
        Truck result = truckRepository.save(truck);
        truckSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/trucks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("truck", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trucks : Updates an existing truck.
     *
     * @param truck the truck to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated truck,
     * or with status 400 (Bad Request) if the truck is not valid,
     * or with status 500 (Internal Server Error) if the truck couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trucks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Truck> updateTruck(@Valid @RequestBody Truck truck) throws URISyntaxException {
        log.debug("REST request to update Truck : {}", truck);
        if (truck.getId() == null) {
            return createTruck(truck);
        }
        Truck result = truckRepository.save(truck);
        truckSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("truck", truck.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trucks : get all the trucks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trucks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/trucks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Truck>> getAllTrucks(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Trucks");
        Page<Truck> page = truckRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trucks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trucks/:id : get the "id" truck.
     *
     * @param id the id of the truck to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the truck, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/trucks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Truck> getTruck(@PathVariable Long id) {
        log.debug("REST request to get Truck : {}", id);
        Truck truck = truckRepository.findOne(id);
        return Optional.ofNullable(truck)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /trucks/:id : delete the "id" truck.
     *
     * @param id the id of the truck to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/trucks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTruck(@PathVariable Long id) {
        log.debug("REST request to delete Truck : {}", id);
        truckRepository.delete(id);
        truckSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("truck", id.toString())).build();
    }

    /**
     * SEARCH  /_search/trucks?query=:query : search for the truck corresponding
     * to the query.
     *
     * @param query the query of the truck search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/trucks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Truck>> searchTrucks(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Trucks for query {}", query);
        Page<Truck> page = truckSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/trucks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
