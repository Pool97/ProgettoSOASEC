package com.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;

public class UserEntity extends User {
    private String email_id;
    private String password;
    private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
    private String id;
    private String first_name;
    private String last_name;
    private String mobile;
    private String country;
    private String user_type;

    //aggiunti gli attributi 2FA dell'utente
    private String is_tfa_enabled;
    private String tfa_default_type;


    public UserEntity(String username, String password, Collection<? extends GrantedAuthority> authorities, String mobile, String is_tfa_enabled, String tfa_default_type, String id) {
        super(username, password, authorities);
        this.email_id = username;
        this.password = password;
        this.mobile = mobile;
        this.is_tfa_enabled = is_tfa_enabled;
        this.tfa_default_type = tfa_default_type;
        this.id = id;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Collection<GrantedAuthority> getGrantedAuthoritiesList() {
        return grantedAuthoritiesList;
    }

    public void setGrantedAuthoritiesList(Collection<GrantedAuthority> grantedAuthoritiesList) {
        this.grantedAuthoritiesList = grantedAuthoritiesList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getIs_tfa_enabled() {
        return is_tfa_enabled;
    }

    public void setIs_tfa_enabled(String is_tfa_enabled) {
        this.is_tfa_enabled = is_tfa_enabled;
    }

    public String getTfa_default_type() {
        return tfa_default_type;
    }

    public void setTfa_default_type(String tfa_default_type) {
        this.tfa_default_type = tfa_default_type;
    }


}
