package com.antt.web.rest;

import com.antt.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

/**
 * AuthorityResource controller
 */
@RestController
@RequestMapping("/api/")
public class AuthorityResource {

    private final Logger log = LoggerFactory.getLogger(AuthorityResource.class);

    //TODO:
    // getAuthories of current user: Not-DONE (UserResource.getAuthorities return all)
    // getUsersOfGroup

    /**
    * POST disableAuthority
    */
    @GetMapping("/authorities/{name}")
    @Secured(AuthoritiesConstants.DISABLE_AUTHORITY)
    public String disableAuthority(@PathVariable String name,
                                   @RequestParam(value="enabled", required = true) boolean enabled ) {
        //check user has right to disable this group
        //disabled group and its members
        return "disableAuthority";
    }

}
