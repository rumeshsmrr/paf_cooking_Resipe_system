package com.cookingshare.backend.security.oauth2;

import com.cookingshare.backend.model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomOAuth2User implements OAuth2User {

    private final AppUser appUser;
    private final Map<String,Object> attributes;

    public CustomOAuth2User(AppUser appUser, Map<String,Object> attributes) {
        this.appUser    = appUser;
        this.attributes = attributes;
    }

    public AppUser getAppUser() { return appUser; }

    @Override
    public Map<String,Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return attributes.get("sub").toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // you could pull roles from AppUser if you like
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
