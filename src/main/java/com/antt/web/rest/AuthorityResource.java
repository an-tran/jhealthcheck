package com.antt.web.rest;

import com.antt.domain.Authority;
import com.antt.domain.User;
import com.antt.repository.AuthorityRepository;
import com.antt.repository.UserRepository;
import com.antt.security.AuthoritiesConstants;
import com.antt.security.SecurityUtils;
import com.antt.service.UserService;
import com.antt.web.rest.errors.ForbiddenResourceException;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AuthorityResource controller
 */
@RestController
@RequestMapping("/api")
public class AuthorityResource {

    private final Logger log = LoggerFactory.getLogger(AuthorityResource.class);
    private final UserService userService;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    public AuthorityResource(UserService userService, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    //TODO:
    // getAuthories of current user: Not-DONE (UserResource.getAuthorities return all)
    // getUsersOfGroup

    @PostMapping("/authorities")
    @Secured(AuthoritiesConstants.CREATE_AUTHORITY)
    @Transactional
    public ResponseEntity<Authority> createGroup(Authority authority) {
        throw new NotImplementedException();
    }

    @GetMapping("/authorities")
    @Secured(AuthoritiesConstants.LIST_AUTHORITIES)
    public ResponseEntity<Authority> getCurrentUserGroup() {
        throw new NotImplementedException();
    }


    /**
    * GET disable Authority
    */
    @GetMapping("/authorities/{groupName}/disable")
    @Secured(AuthoritiesConstants.UPDATE_AUTHORITY)
    @Transactional(propagation = Propagation.REQUIRED)
    public void disableAuthority(@PathVariable String groupName,
                                   @RequestParam(value="enabled", required = false) boolean enabled ) {
        //check user has right to disable this group
        currentUserCanChangeGroup(groupName);

        //disabled group and its members
//        List<Authority> disabledAuthorities = authorityRepository.disableGroupsByName(enabledAuthorities);
//
//        if (disabledAuthorities.size() != enabledAuthorities.size()) {
//            log.warn("Expect to disabled {} groups but actually {} groups changed", enabledAuthorities.size(), disabledAuthorities.size());
//        }
        int ret = authorityRepository.disableByName(groupName);
        List<Long> memberIds = authorityRepository.findUserIdByGroupName(groupName);
        userRepository.disableUsersById(memberIds);
//        authorityRepository.flush();
//        userRepository.flush();
//        Authority g =  authorityRepository.findOne(groupName);
//        System.out.println(g.isEnabled());
    }

    /**
     * GET disable Authority
     */
    @GetMapping("/authorities/{groupName}/enable")
    @Secured(AuthoritiesConstants.UPDATE_AUTHORITY)
    @Transactional(propagation = Propagation.REQUIRED)
    public void enableAuthority(@PathVariable String groupName,
                                 @RequestParam(value="enabled", required = false) boolean enabled ) {
        //check user has right to disable this group
        currentUserCanChangeGroup(groupName);

        int ret = authorityRepository.enableByName(groupName);
        List<Long> memberIds = authorityRepository.findUserIdByGroupName(groupName);
        userRepository.enableUsersById(memberIds);
    }

    private void currentUserCanChangeGroup(String groupName) {
        String username = SecurityUtils.getCurrentUserLogin();
        Optional<User> user = userRepository.findOneByLogin(username);
        List<String> enabledAuthorities = user.map(u -> u.getAuthorities().stream()
                                                .filter(Authority::isEnabled)
                                                .map(Authority::getName)
                                                .collect(Collectors.toList()))
                                        .orElse(Collections.emptyList());
        if (!enabledAuthorities.contains(groupName)) {
            throw new ForbiddenResourceException("Don't have right to enable/disable group " + groupName);
        }
    }

}
