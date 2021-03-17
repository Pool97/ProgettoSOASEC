package com.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;

public class UserEntity extends User {
    private String emailAddress;
    private String password;
    private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
    private String id;
    private String mobile;

    //aggiunto attributo 2FA che specifica la modalità di trasferimento dell'OTP preferita dall'utente
    private String tfaDefaultType;

    public final static String ID_COLUMN_DB = "idUsers";
    public final static String PASSWORD_COLUMN_DB = "password";
    public final static String MOBILE_NUM_COLUMN_DB = "mobile";
    public final static String TFA_DEFAULT_TYPE_COLUMN_DB = "2fa_default_type";

    public UserEntity(String username, String password, Collection<? extends GrantedAuthority> authorities, String mobile, String tfaDefaultType, String id) {
        super(username, password, authorities);
        this.emailAddress = username;
        this.password = password;
        this.mobile = mobile;
        this.tfaDefaultType = tfaDefaultType;
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTfaDefaultType() {
        return tfaDefaultType;
    }

    public void setTfaDefaultType(String tfaDefaultType) {
        this.tfaDefaultType = tfaDefaultType;
    }


}
