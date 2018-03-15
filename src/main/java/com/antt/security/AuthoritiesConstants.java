package com.antt.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String CREATE_USER = "ROLE_RIGHT_USER_CREATE";
    public static final String DELETE_USER = "ROLE_RIGHT_USER_DELETE";
    public static final String UPDATE_USER = "ROLE_RIGHT_USER_UPDATE";
    public static final String LIST_AUTHORITIES = "ROLE_RIGHT_AUTHORITY_LIST";
    public static final String UPDATE_AUTHORITY = "ROLE_RIGHT_AUTHORITY_UPDATE";
    public static final String CREATE_AUTHORITY = "ROLE_RIGHT_AUTHORITY_CREATE";

    private AuthoritiesConstants() {
    }
}
