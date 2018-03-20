package com.antt.web.rest;

import com.antt.JhealthcheckApp;
import com.antt.domain.Authority;
import com.antt.domain.User;
import com.antt.repository.AuthorityRepository;
import com.antt.repository.RightRepository;
import com.antt.repository.UserRepository;
import com.antt.security.AuthoritiesConstants;
import com.antt.service.UserService;
import com.antt.service.dto.AuthorityDTO;
import com.antt.web.rest.errors.ExceptionTranslator;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Test class for the AuthorityResource REST controller.
 *
 * @see AuthorityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhealthcheckApp.class)
public class AuthorityResourceIntTest {

    private static final String DEFAULT_NAME = "ROLE_UNITTEST";
    private MockMvc restMockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RightRepository rightRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private User admin;
    private User user;
    private Authority adminGroup;

//    public AuthorityResourceIntTest(UserService userService, AuthorityRepository authorityRepository, UserRepository userRepository) {
//        this.userService = userService;
//        this.authorityRepository = authorityRepository;
//        this.userRepository = userRepository;
//    }


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        AuthorityResource authorityResource = new AuthorityResource(
            userService, userRepository, authorityRepository, rightRepository);
        restMockMvc = MockMvcBuilders
            .standaloneSetup(authorityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter)
            .build();

//        List<Authority> authorities = authorityRepository.findAll();
        adminGroup = authorityRepository.findOneByName("ROLE_ADMIN").get();
        Optional<User> opuser = userRepository.findOneByLogin("user");
        Optional<User> opadmin = userRepository.findOneByLogin("admin");
        this.user = opuser.get();
        this.admin = opadmin.get();
        user.getAuthorities().add(adminGroup);
        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(admin);
    }

    public static Authority createAuthority() {
        Authority auth = new Authority();
        auth.setName(DEFAULT_NAME + RandomStringUtils.randomAlphabetic(2));
        auth.setEnabled(true);

        return auth;
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    public void testCreateAuthority() throws Exception {
        AuthorityDTO authorityDTO = new AuthorityDTO();
        authorityDTO.setName(DEFAULT_NAME);
        authorityDTO.setEnabled(true);
        authorityDTO.setRights(new HashSet<>(Arrays.asList(
            AuthoritiesConstants.LIST_AUTHORITIES, AuthoritiesConstants.CREATE_AUTHORITY)));
        authorityDTO.setParent("ROLE_VN");

        restMockMvc.perform(post("/api/authorities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(authorityDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.owner.login").value("admin"))
            .andExpect(jsonPath("$.rights").isArray())
            .andExpect(jsonPath("$.rights.[*].name").value(contains(
                AuthoritiesConstants.LIST_AUTHORITIES,AuthoritiesConstants.CREATE_AUTHORITY)));

        List<Authority> ancestors = authorityRepository.finAllAncestor(DEFAULT_NAME);
        assertThat(ancestors.size()).isEqualTo(3);
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    @Transactional
    public void createWithDuplicateAuthorityName() throws Exception {
        long nAuthorityBeforeCreate = authorityRepository.count();
        AuthorityDTO authorityDTO = new AuthorityDTO();
        authorityDTO.setName("ROLE_ADMIN");
        authorityDTO.setEnabled(true);
        authorityDTO.setRights(new HashSet<>(Arrays.asList(
            AuthoritiesConstants.LIST_AUTHORITIES, AuthoritiesConstants.CREATE_AUTHORITY)));

        restMockMvc.perform(post("/api/authorities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(authorityDTO)))
            .andExpect(status().isBadRequest());

        long nAuthorityAfterCreate = authorityRepository.count();
        assertThat(nAuthorityAfterCreate).isEqualTo(nAuthorityBeforeCreate);
    }
    /**
    * Test disableAuthority
    */
    @Test
    @Transactional
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    public void testDisableAuthority() throws Exception {
        assertThat(adminGroup.isEnabled()).isTrue();
        assertThat(admin.isEnabled()).isTrue();
        assertThat(user.isEnabled()).isTrue();
        restMockMvc.perform(get("/api/authorities/ROLE_ADMIN/disable"))
            .andExpect(status().isOk());

        Authority updatedAuth = authorityRepository.findOne(adminGroup.getName());
        User updateUser = userRepository.findOne(user.getId());
        User updateAdmin= userRepository.findOne(admin.getId());
        assertThat(updatedAuth.isEnabled()).isFalse();
        assertThat(updateUser.isEnabled()).isFalse();
        assertThat(updateAdmin.isEnabled()).isFalse();

    }

    @Test
    @Transactional
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    public void testGetAuthorities() throws Exception {
        ResultActions resultActions = restMockMvc.perform(get("/api/authorities"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].name").value(hasItem("ROLE_HCM")));
        resultActions.andReturn().getResponse();
    }
}
