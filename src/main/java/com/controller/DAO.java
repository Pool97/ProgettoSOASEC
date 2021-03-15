package com.controller;

import com.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;

import java.util.Collections;

/**
 * Un Data Access Object (DAO) per l'interfacciamento del server Java con il database MySQL.
 */

@Repository
public class DAO implements UserDetailsService {

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
        String query = "SELECT COUNT(*) FROM users WHERE 2fa_code = ? and idUsers = ? and 2fa_expire_time >= ?";
        int numberOfRows = jdbcTemplate.queryForObject(query, Integer.class, twoFAcode, userID, System.currentTimeMillis()/1000);
        return numberOfRows > 0;
    }

    /**
     * Verifica che l'userID dell'utente sia presente all'interno del database
     * @param userID ID dell'utente nel database
     * @return Presenza dell'utente nel database
     */
    public boolean checkUserID(String userID){
        String query = "SELECT COUNT(*) FROM users WHERE email_id = ?";
        int numberOfRows = jdbcTemplate.queryForObject(query, Integer.class, userID);
        return numberOfRows > 0;
    }

    /**
     * Ricerca nel database un determinato utente e genera un oggetto contenente vari dettagli sull'utente.
     * @param emailAddress L'indirizzo di posta elettronica è l'username utilizzato
     * @return Dettagli dell'utente
     */

    @Override
    public UserDetails loadUserByUsername(String emailAddress){
        String query = "SELECT idUsers, password, mobile, is_2fa_enabled, 2fa_default_type FROM users WHERE email_id = ?";

        try {
            UserDetails user = jdbcTemplate.queryForObject(query, (rs, rowNum) -> {
                String id = rs.getString("idUsers");
                String password = rs.getString("password");
                String mobile = rs.getString("mobile");
                String isTfaEnabled = rs.getString("is_2fa_enabled");
                String tfaDefaultType = rs.getString("2fa_default_type");
                return new UserEntity(emailAddress, password, Collections.emptyList(), mobile, isTfaEnabled, tfaDefaultType, id);
            }, emailAddress);
            return user;
        }catch(Exception e){
            System.out.println(emailAddress + " non presente nel DB!");
        }

        return null;
    }
}
