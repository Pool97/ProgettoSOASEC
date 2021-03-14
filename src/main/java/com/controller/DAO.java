package com.controller;

import com.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

/**
 * Un Data Access Object (DAO) per l'interfacciamento del server Java con il database MySQL.
 */

@Repository
public class DAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Aggiorna le proprietà 2FA dell'utente quando richiede un codice OTP al login. L'aggiornamento prevede
     * l'impostare una scadenza sulla validità dell'OTP.
     * @param userID ID dell'utente nel database
     * @param twoFAcode OTP generato per l'utente
     */
    public void update2FAProperties(String userID, String twoFAcode) {
        jdbcTemplate.update("UPDATE users SET 2fa_code=?, 2fa_expire_time=? WHERE idUsers=?", twoFAcode, System.currentTimeMillis() / 1000 + 120, userID);
    }

    /**
     * Controlla che il codice OTP inserito dall'utente nella web-app corrisponda a quello generato dal server
     * e memorizzato nel database in corrispondenza della riga dell'utente.
     * @param userID ID dell'utente nel database
     * @param twoFAcode OTP inserito dall'utente nella web-app
     * @return Validità del codice inserito dall'utente
     */
    public boolean checkCode(String userID, String twoFAcode) {
        List<UserEntity> list = jdbcTemplate.query("SELECT COUNT(*) FROM users WHERE 2fa_code=? and email_id = ? and 2fa_expire_time >= ?", new Object[]{twoFAcode, userID, System.currentTimeMillis()/1000},
                (ResultSet rs, int rowNum) -> {
                    UserEntity user = new UserEntity();
                    return user;
                });
        return list.size() > 0;
    }

    /**
     * Verifica che l'userID dell'utente sia presente all'interno del database
     * @param userID ID dell'utente nel database
     * @return Presenza dell'utente nel database
     */
    public boolean checkUserID(String userID){
        List<UserEntity> list = jdbcTemplate.query("SELECT COUNT(*) FROM users WHERE email_id = ?", new String[]{userID},
                (ResultSet rs, int rowNum) -> {
                    UserEntity user = new UserEntity();
                    return user;
                });
        System.out.println(list.size());
        return list.size() > 0;
    }

}
