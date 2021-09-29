package com.test.captcha.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String BUYER = "ROLE_BUYER";

    public static final String SELLER = "ROLE_SELLER";

    private AuthoritiesConstants() {}
}
