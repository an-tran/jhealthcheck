package com.antt.web.rest;

import com.antt.JhealthcheckApp;

import com.antt.domain.Right;
import com.antt.repository.RightRepository;
import com.antt.repository.search.RightSearchRepository;
import com.antt.web.rest.errors.ExceptionTranslator;

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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.antt.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RightResource REST controller.
 *
 * @see RightResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhealthcheckApp.class)
public class RightResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private RightRepository rightRepository;

    @Autowired
    private RightSearchRepository rightSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRightMockMvc;

    private Right right;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RightResource rightResource = new RightResource(rightRepository, rightSearchRepository);
        this.restRightMockMvc = MockMvcBuilders.standaloneSetup(rightResource)
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
    public static Right createEntity(EntityManager em) {
        Right right = new Right()
            .name(DEFAULT_NAME);
        return right;
    }

    @Before
    public void initTest() {
        rightSearchRepository.deleteAll();
        right = createEntity(em);
    }

    @Test
    @Transactional
    public void createRight() throws Exception {
        int databaseSizeBeforeCreate = rightRepository.findAll().size();

        // Create the Right
        restRightMockMvc.perform(post("/api/rights")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(right)))
            .andExpect(status().isCreated());

        // Validate the Right in the database
        List<Right> rightList = rightRepository.findAll();
        assertThat(rightList).hasSize(databaseSizeBeforeCreate + 1);
        Right testRight = rightList.get(rightList.size() - 1);
        assertThat(testRight.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Right in Elasticsearch
        Right rightEs = rightSearchRepository.findOne(testRight.getId());
        assertThat(rightEs).isEqualToComparingFieldByField(testRight);
    }

    @Test
    @Transactional
    public void createRightWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = rightRepository.findAll().size();

        // Create the Right with an existing ID
        right.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRightMockMvc.perform(post("/api/rights")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(right)))
            .andExpect(status().isBadRequest());

        // Validate the Right in the database
        List<Right> rightList = rightRepository.findAll();
        assertThat(rightList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rightRepository.findAll().size();
        // set the field null
        right.setName(null);

        // Create the Right, which fails.

        restRightMockMvc.perform(post("/api/rights")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(right)))
            .andExpect(status().isBadRequest());

        List<Right> rightList = rightRepository.findAll();
        assertThat(rightList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRights() throws Exception {
        // Initialize the database
        rightRepository.saveAndFlush(right);

        // Get all the rightList
        restRightMockMvc.perform(get("/api/rights?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(right.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getRight() throws Exception {
        // Initialize the database
        rightRepository.saveAndFlush(right);

        // Get the right
        restRightMockMvc.perform(get("/api/rights/{id}", right.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(right.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRight() throws Exception {
        // Get the right
        restRightMockMvc.perform(get("/api/rights/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRight() throws Exception {
        // Initialize the database
        rightRepository.saveAndFlush(right);
        rightSearchRepository.save(right);
        int databaseSizeBeforeUpdate = rightRepository.findAll().size();

        // Update the right
        Right updatedRight = rightRepository.findOne(right.getId());
        updatedRight
            .name(UPDATED_NAME);

        restRightMockMvc.perform(put("/api/rights")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRight)))
            .andExpect(status().isOk());

        // Validate the Right in the database
        List<Right> rightList = rightRepository.findAll();
        assertThat(rightList).hasSize(databaseSizeBeforeUpdate);
        Right testRight = rightList.get(rightList.size() - 1);
        assertThat(testRight.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Right in Elasticsearch
        Right rightEs = rightSearchRepository.findOne(testRight.getId());
        assertThat(rightEs).isEqualToComparingFieldByField(testRight);
    }

    @Test
    @Transactional
    public void updateNonExistingRight() throws Exception {
        int databaseSizeBeforeUpdate = rightRepository.findAll().size();

        // Create the Right

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRightMockMvc.perform(put("/api/rights")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(right)))
            .andExpect(status().isCreated());

        // Validate the Right in the database
        List<Right> rightList = rightRepository.findAll();
        assertThat(rightList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteRight() throws Exception {
        // Initialize the database
        rightRepository.saveAndFlush(right);
        rightSearchRepository.save(right);
        int databaseSizeBeforeDelete = rightRepository.findAll().size();

        // Get the right
        restRightMockMvc.perform(delete("/api/rights/{id}", right.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean rightExistsInEs = rightSearchRepository.exists(right.getId());
        assertThat(rightExistsInEs).isFalse();

        // Validate the database is empty
        List<Right> rightList = rightRepository.findAll();
        assertThat(rightList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRight() throws Exception {
        // Initialize the database
        rightRepository.saveAndFlush(right);
        rightSearchRepository.save(right);

        // Search the right
        restRightMockMvc.perform(get("/api/_search/rights?query=id:" + right.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(right.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Right.class);
        Right right1 = new Right();
        right1.setId(1L);
        Right right2 = new Right();
        right2.setId(right1.getId());
        assertThat(right1).isNotEqualTo(right2);
        right2.setId(2L);
        assertThat(right1).isNotEqualTo(right2);
        right1.setId(null);
        assertThat(right1).isNotEqualTo(right2);
    }
}
