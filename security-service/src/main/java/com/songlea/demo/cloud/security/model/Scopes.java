package com.songlea.demo.cloud.security.model;

/**
 * Scopes
 */
public enum Scopes {

    REFRESH_TOKEN;

    public String authority() {
        return "ROLE_" + this.name();
    }
}
