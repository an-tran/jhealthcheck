package com.antt.web.rest;

import com.antt.JhealthcheckApp;
import com.antt.domain.Authority;
import com.antt.domain.User;
import com.antt.repository.AuthorityRepository;
import com.antt.repository.UserRepository;
import com.antt.service.UserService;
import com.antt.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Test class for the AuthorityResource REST controller.
 *
 * @see AuthorityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhealthcheckApp.class)
public class AuthorityResourceIntTest {

    private MockMvc restMockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserRepository userRepository;

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

        AuthorityResource authorityResource = new AuthorityResource(userService, userRepository, authorityRepository);
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

}
