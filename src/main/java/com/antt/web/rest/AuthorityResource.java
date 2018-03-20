package com.antt.web.rest;

import com.antt.domain.Authority;
import com.antt.domain.Right;
import com.antt.domain.User;
import com.antt.repository.AuthorityRepository;
import com.antt.repository.RightRepository;
import com.antt.repository.UserRepository;
import com.antt.security.AuthoritiesConstants;
import com.antt.security.SecurityUtils;
import com.antt.service.UserService;
import com.antt.service.dto.AuthorityDTO;
import com.antt.web.rest.errors.BadRequestAlertException;
import com.antt.web.rest.errors.ForbiddenResourceException;
import com.antt.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final RightRepository rightRepository;

    public AuthorityResource(UserService userService, UserRepository userRepository,
                             AuthorityRepository authorityRepository,
                             RightRepository rightRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.rightRepository = rightRepository;
    }


    //TODO:
    // getUsersOfGroup

    /***
     * Create a group. Owner of new group is the current login user
     * @param authorityDTO
     * @return new Authority
     */
    @PostMapping("/authorities")
    @Secured(AuthoritiesConstants.CREATE_AUTHORITY)
    @Transactional
    public ResponseEntity<Authority> createGroup(@Valid @RequestBody AuthorityDTO authorityDTO) throws URISyntaxException {
        if (authorityRepository.exists(authorityDTO.getName())) {
           throw new BadRequestAlertException("Group name already in use", "Authority", "error.createAuthority");
        }

        Authority newAuth = new Authority();
        Optional<User> user = getLoginUser();

        newAuth.setName(authorityDTO.getName());
        newAuth.setCreator(user.orElse(null));
        newAuth.setEnabled(authorityDTO.isEnabled());
        if(authorityDTO.getRights() != null) {
            Set<Right> rights = authorityDTO.getRights().stream()
                .map(rightRepository::findOneByName)
                .map(rightOptional -> rightOptional.orNull())
                .filter(r -> r != null)
                .collect(Collectors.toSet());
            newAuth.setRights(rights);
        }
        newAuth.setCreator(user.get());
        newAuth.setOwner(user.get());
        Authority savedAuth = authorityRepository.save(newAuth);

        // bug of H2 so that cannot use UNION statement. Using 2 insert statement
        int changed = authorityRepository.addAuthorityPathFor("ROLE_HCM", authorityDTO.getName());
        changed = authorityRepository.addSelfAuthorityPath(authorityDTO.getName());

        return ResponseEntity.created(new URI("/api/authorities/" + savedAuth.getName()))
            .headers(HeaderUtil.createAlert("authorities.created", savedAuth.getName()))
            .body(savedAuth);
    }

    @GetMapping("/authorities")
    @Secured(AuthoritiesConstants.LIST_AUTHORITIES)
    public ResponseEntity<List<Authority>> getCurrentUserGroup() {
        Optional<User> user = getLoginUser();

        List<Authority> authorities = user.map(u -> authorityRepository.findAllByOwner(u))
            .orElse(Collections.emptyList());

        return ResponseEntity.ok(authorities);
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
        Optional<User> user = getLoginUser();
        List<String> enabledAuthorities = user.map(u -> u.getAuthorities().stream()
                                                .filter(Authority::isEnabled)
                                                .map(Authority::getName)
                                                .collect(Collectors.toList()))
                                        .orElse(Collections.emptyList());
        if (!enabledAuthorities.contains(groupName)) {
            throw new ForbiddenResourceException("Don't have right to enable/disable group " + groupName);
        }
    }

    private Optional<User> getLoginUser() {
        String username = SecurityUtils.getCurrentUserLogin();
        return userRepository.findOneByLogin(username);
    }

}
