package com.antt.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.antt.domain.Right;

import com.antt.repository.RightRepository;
import com.antt.repository.search.RightSearchRepository;
import com.antt.web.rest.errors.BadRequestAlertException;
import com.antt.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Right.
 */
@RestController
@RequestMapping("/api")
public class RightResource {

    private final Logger log = LoggerFactory.getLogger(RightResource.class);

    private static final String ENTITY_NAME = "right";

    private final RightRepository rightRepository;

    private final RightSearchRepository rightSearchRepository;

    public RightResource(RightRepository rightRepository, RightSearchRepository rightSearchRepository) {
        this.rightRepository = rightRepository;
        this.rightSearchRepository = rightSearchRepository;
    }

    /**
     * POST  /rights : Create a new right.
     *
     * @param right the right to create
     * @return the ResponseEntity with status 201 (Created) and with body the new right, or with status 400 (Bad Request) if the right has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/rights")
    @Timed
    public ResponseEntity<Right> createRight(@Valid @RequestBody Right right) throws URISyntaxException {
        log.debug("REST request to save Right : {}", right);
        if (right.getId() != null) {
            throw new BadRequestAlertException("A new right cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Right result = rightRepository.save(right);
        rightSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/rights/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /rights : Updates an existing right.
     *
     * @param right the right to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated right,
     * or with status 400 (Bad Request) if the right is not valid,
     * or with status 500 (Internal Server Error) if the right couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/rights")
    @Timed
    public ResponseEntity<Right> updateRight(@Valid @RequestBody Right right) throws URISyntaxException {
        log.debug("REST request to update Right : {}", right);
        if (right.getId() == null) {
            return createRight(right);
        }
        Right result = rightRepository.save(right);
        rightSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, right.getId().toString()))
            .body(result);
    }

    /**
     * GET  /rights : get all the rights.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of rights in body
     */
    @GetMapping("/rights")
    @Timed
    public List<Right> getAllRights() {
        log.debug("REST request to get all Rights");
        return rightRepository.findAll();
        }

    /**
     * GET  /rights/:id : get the "id" right.
     *
     * @param id the id of the right to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the right, or with status 404 (Not Found)
     */
    @GetMapping("/rights/{id}")
    @Timed
    public ResponseEntity<Right> getRight(@PathVariable Long id) {
        log.debug("REST request to get Right : {}", id);
        Right right = rightRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(right));
    }

    /**
     * DELETE  /rights/:id : delete the "id" right.
     *
     * @param id the id of the right to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/rights/{id}")
    @Timed
    public ResponseEntity<Void> deleteRight(@PathVariable Long id) {
        log.debug("REST request to delete Right : {}", id);
        rightRepository.delete(id);
        rightSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/rights?query=:query : search for the right corresponding
     * to the query.
     *
     * @param query the query of the right search
     * @return the result of the search
     */
    @GetMapping("/_search/rights")
    @Timed
    public List<Right> searchRights(@RequestParam String query) {
        log.debug("REST request to search Rights for query {}", query);
        return StreamSupport
            .stream(rightSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
