package com.accenture.devops.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.accenture.devops.domain.TrashBin;
import com.accenture.devops.repository.TrashBinRepository;
import com.accenture.devops.repository.search.TrashBinSearchRepository;
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
 * REST controller for managing TrashBin.
 */
@RestController
@RequestMapping("/api")
public class TrashBinResource {

    private final Logger log = LoggerFactory.getLogger(TrashBinResource.class);
        
    @Inject
    private TrashBinRepository trashBinRepository;
    
    @Inject
    private TrashBinSearchRepository trashBinSearchRepository;
    
    /**
     * POST  /trash-bins : Create a new trashBin.
     *
     * @param trashBin the trashBin to create
     * @return the ResponseEntity with status 201 (Created) and with body the new trashBin, or with status 400 (Bad Request) if the trashBin has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trash-bins",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TrashBin> createTrashBin(@Valid @RequestBody TrashBin trashBin) throws URISyntaxException {
        log.debug("REST request to save TrashBin : {}", trashBin);
        if (trashBin.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("trashBin", "idexists", "A new trashBin cannot already have an ID")).body(null);
        }
        TrashBin result = trashBinRepository.save(trashBin);
        trashBinSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/trash-bins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("trashBin", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trash-bins : Updates an existing trashBin.
     *
     * @param trashBin the trashBin to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated trashBin,
     * or with status 400 (Bad Request) if the trashBin is not valid,
     * or with status 500 (Internal Server Error) if the trashBin couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/trash-bins",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TrashBin> updateTrashBin(@Valid @RequestBody TrashBin trashBin) throws URISyntaxException {
        log.debug("REST request to update TrashBin : {}", trashBin);
        if (trashBin.getId() == null) {
            return createTrashBin(trashBin);
        }
        TrashBin result = trashBinRepository.save(trashBin);
        trashBinSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("trashBin", trashBin.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trash-bins : get all the trashBins.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trashBins in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/trash-bins",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TrashBin>> getAllTrashBins(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of TrashBins");
        Page<TrashBin> page = trashBinRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trash-bins");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trash-bins/:id : get the "id" trashBin.
     *
     * @param id the id of the trashBin to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the trashBin, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/trash-bins/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TrashBin> getTrashBin(@PathVariable Long id) {
        log.debug("REST request to get TrashBin : {}", id);
        TrashBin trashBin = trashBinRepository.findOne(id);
        return Optional.ofNullable(trashBin)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /trash-bins/:id : delete the "id" trashBin.
     *
     * @param id the id of the trashBin to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/trash-bins/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTrashBin(@PathVariable Long id) {
        log.debug("REST request to delete TrashBin : {}", id);
        trashBinRepository.delete(id);
        trashBinSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("trashBin", id.toString())).build();
    }

    /**
     * SEARCH  /_search/trash-bins?query=:query : search for the trashBin corresponding
     * to the query.
     *
     * @param query the query of the trashBin search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/trash-bins",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TrashBin>> searchTrashBins(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of TrashBins for query {}", query);
        Page<TrashBin> page = trashBinSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/trash-bins");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
