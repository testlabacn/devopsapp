package com.accenture.devops.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.accenture.devops.domain.WasteCollection;
import com.accenture.devops.repository.WasteCollectionRepository;
import com.accenture.devops.repository.search.WasteCollectionSearchRepository;
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
 * REST controller for managing WasteCollection.
 */
@RestController
@RequestMapping("/api")
public class WasteCollectionResource {

    private final Logger log = LoggerFactory.getLogger(WasteCollectionResource.class);
        
    @Inject
    private WasteCollectionRepository wasteCollectionRepository;
    
    @Inject
    private WasteCollectionSearchRepository wasteCollectionSearchRepository;
    
    /**
     * POST  /waste-collections : Create a new wasteCollection.
     *
     * @param wasteCollection the wasteCollection to create
     * @return the ResponseEntity with status 201 (Created) and with body the new wasteCollection, or with status 400 (Bad Request) if the wasteCollection has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/waste-collections",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WasteCollection> createWasteCollection(@Valid @RequestBody WasteCollection wasteCollection) throws URISyntaxException {
        log.debug("REST request to save WasteCollection : {}", wasteCollection);
        if (wasteCollection.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("wasteCollection", "idexists", "A new wasteCollection cannot already have an ID")).body(null);
        }
        WasteCollection result = wasteCollectionRepository.save(wasteCollection);
        wasteCollectionSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/waste-collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("wasteCollection", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /waste-collections : Updates an existing wasteCollection.
     *
     * @param wasteCollection the wasteCollection to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated wasteCollection,
     * or with status 400 (Bad Request) if the wasteCollection is not valid,
     * or with status 500 (Internal Server Error) if the wasteCollection couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/waste-collections",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WasteCollection> updateWasteCollection(@Valid @RequestBody WasteCollection wasteCollection) throws URISyntaxException {
        log.debug("REST request to update WasteCollection : {}", wasteCollection);
        if (wasteCollection.getId() == null) {
            return createWasteCollection(wasteCollection);
        }
        WasteCollection result = wasteCollectionRepository.save(wasteCollection);
        wasteCollectionSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("wasteCollection", wasteCollection.getId().toString()))
            .body(result);
    }

    /**
     * GET  /waste-collections : get all the wasteCollections.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of wasteCollections in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/waste-collections",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<WasteCollection>> getAllWasteCollections(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of WasteCollections");
        Page<WasteCollection> page = wasteCollectionRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/waste-collections");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /waste-collections/:id : get the "id" wasteCollection.
     *
     * @param id the id of the wasteCollection to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the wasteCollection, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/waste-collections/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WasteCollection> getWasteCollection(@PathVariable Long id) {
        log.debug("REST request to get WasteCollection : {}", id);
        WasteCollection wasteCollection = wasteCollectionRepository.findOne(id);
        return Optional.ofNullable(wasteCollection)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /waste-collections/:id : delete the "id" wasteCollection.
     *
     * @param id the id of the wasteCollection to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/waste-collections/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWasteCollection(@PathVariable Long id) {
        log.debug("REST request to delete WasteCollection : {}", id);
        wasteCollectionRepository.delete(id);
        wasteCollectionSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("wasteCollection", id.toString())).build();
    }

    /**
     * SEARCH  /_search/waste-collections?query=:query : search for the wasteCollection corresponding
     * to the query.
     *
     * @param query the query of the wasteCollection search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/waste-collections",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<WasteCollection>> searchWasteCollections(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of WasteCollections for query {}", query);
        Page<WasteCollection> page = wasteCollectionSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/waste-collections");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
