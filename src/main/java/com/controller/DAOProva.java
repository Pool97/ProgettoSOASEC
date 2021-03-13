package com.controller;

import com.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

/* Da verificare l'utilità di questa classe*/

@Repository
public class DAOProva {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserEntity getUserDetails(String username){
        String userSQLQuery = "select * from users where email_id=?";
        List<UserEntity> list = jdbcTemplate.query(userSQLQuery, new String[]{username},
                (ResultSet rs, int rowNum) -> {
                    UserEntity user = new UserEntity();
                    user.setCountry(rs.getString("country"));
                    user.setEmail_id(rs.getString(username));
                    user.setFirst_name(rs.getString("first_name"));
                    user.setId(rs.getString("id"));
                    user.setLast_name(rs.getString("last_name"));
                    user.setMobile(rs.getString("mobile"));
                    user.setUser_type(rs.getString("user_type"));
                    user.setPassword(rs.getString("password"));
                    user.setIs_tfa_enabled(rs.getString("is2fa_enabled"));
                    user.setTfa_default_type(rs.getString("2fa_default_type"));
                    return user;
                } );
        return list.get(0);
    }
}
