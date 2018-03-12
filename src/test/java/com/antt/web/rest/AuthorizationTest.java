package com.antt.web.rest;

import com.antt.JhealthcheckApp;
import com.antt.domain.Authority;
import com.antt.domain.Right;
import com.antt.domain.User;
import com.antt.repository.UserRepository;
import com.antt.repository.search.UserSearchRepository;
import com.antt.security.AuthoritiesConstants;
import com.antt.service.MailService;
import com.antt.service.UserService;
import com.antt.service.mapper.UserMapper;
import com.antt.web.rest.errors.ExceptionTranslator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

/**
 * Created by antt on 3/9/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhealthcheckApp.class)

public class AuthorizationTest {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    private FilterChainProxy filterChainProxy;
    @Mock
    UserRepository mockUserrepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSearchRepository userSearchRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restUserMvc;
    private static final String DEFAULT_EMAIL = "johndoe@localhost";

    private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
    private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";

    private static final String DEFAULT_LANGKEY = "en";
    private static final String UPDATED_LANGKEY = "fr";
    private User user;
    @Autowired
    private EntityManager em;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserResource userResource = new UserResource(userRepository, userService, mailService, userSearchRepository);
        this.restUserMvc = MockMvcBuilders.webAppContextSetup(wac)
            .apply(springSecurity())
            .build();
    }

    @Before
    public void initTest() {
        user = createEntity(em);
        user.setLogin("admin");
        user.setEmail(DEFAULT_EMAIL);
//        userRepository.saveAndFlush(user);
    }

    public static User createEntity(EntityManager em) {
        User user = new User();
        user.setLogin("admin");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL);
        user.setFirstName("jon");
        user.setLastName("admin");
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        Authority authority = new Authority();
        authority.setName("ROLE_ADMIN");
        Right right = new Right();
        right.setName(AuthoritiesConstants.LIST_AUTHORITIES);
        authority.getRights().add(right);
        user.getAuthorities().add(authority);
        return user;
    }

    @Test
    public void testUnthorizedUserAccess() throws Exception {
        restUserMvc.perform(get("/api/users/authorities"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    public void testAuthorizedUserAccess() throws Exception {
        restUserMvc.perform(get("/api/users/authorities"))
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void testUnAuthorizedUserAccess2() throws Exception {
        restUserMvc.perform(get("/api/users/authorities"))
            .andExpect(status().is4xxClientError());
    }
}
