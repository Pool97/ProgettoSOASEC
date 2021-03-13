package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        jdbcTemplate.update("update users set 2fa_code=?, 2fa_expire_time=? where idUsers=?", twoFAcode, System.currentTimeMillis() / 1000 + 120, userID);
    }

    /**
     * Controlla che il codice OTP inserito dall'utente nella web-app corrisponda a quello generato dal server
     * e memorizzato nel database in corrispondenza della riga dell'utente.
     * @param userID ID dell'utente nel database
     * @param twoFAcode OTP inserito dall'utente nella web-app
     * @return Validità del codice inserito dall'utente
     */
    public boolean checkCode(String userID, String twoFAcode){
        return jdbcTemplate.queryForObject("select count(*) from users where 2fa_code=?" +
                "and idUsers = ? and 2fa_expire_time >=?", new Object[]{twoFAcode, userID , System.currentTimeMillis()/1000}, Integer.class) > 0;
    }

}
