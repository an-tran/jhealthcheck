package com.antt.web.rest;

import com.antt.JhealthcheckApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        AuthorityResource authorityResourceResource = new AuthorityResource();
        restMockMvc = MockMvcBuilders
            .standaloneSetup(authorityResourceResource)
            .build();
    }

    /**
    * Test disableAuthority
    */
//    @Test
//    public void testDisableAuthority() throws Exception {
//        restMockMvc.perform(post("/api/authorities/disable-authority"))
//            .andExpect(status().isOk());
//    }

}
