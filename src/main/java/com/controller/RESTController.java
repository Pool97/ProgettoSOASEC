package com.controller;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Random;

/**
 * Controller REST che prevede:
 *  - Invio dell'OTP tramite e-mail Google
 *  - Invio dell'OTP tramite SMS Twilio
 *  - Verifica dell'OTP inserito dall'utente al termine della procedura
 */

@RestController
public class RESTController {
    private final static String ENDPOINT_EMAIL = "/users/{userid}/emails/{emailid}/2fa";
    private final static String ENDPOINT_SMS = "/users/{userid}/mobilenumbers/{mobilenumber}/2fa";
    private final static String ENDPOINT_VERIFICATION = "/users/{userid}/codes/{2facode}";
    private final static String ENDPOINT_CREDENTIAL = "/intermediary";
    private final static String PATH_VARIABLE_USER = "userid";
    private final static String PATH_VARIABLE_MAIL = "emailid";
    private final static String PATH_VARIABLE_SMS = "mobilenumber";
    private final static String PATH_VARIABLE_OTP = "2facode";

    @Autowired
    EmailService emailService;
    @Autowired
    DAO dao;
    @Autowired
    SMSService smsService;

    /**
     * Generazione dell'OTP della two-factor authentication
     * @return OTP generato
     */
    private String generateOTP(){
        return String.valueOf(new Random().nextInt(9999) + 1000);
    }

    /**
     * Endpoint che viene triggerato tramite una chiamata HTTP di tipo PUT. Al triggering, avvia la procedura
     * di invio dell'OTP alla mail specificata nella richiesta HTTP.
     * @param userID ID dell'utente presente nel database
     * @param emailAddress Indirizzo e-mail dell'utente
     * @return Risposta HTTP che riassume all'utente l'esito della procedura.
     */

    @RequestMapping(value= ENDPOINT_EMAIL, method= RequestMethod.PUT)
    public ResponseEntity<Object> sendOTPViaEmail(@PathVariable(PATH_VARIABLE_USER) String userID, @PathVariable(PATH_VARIABLE_MAIL)String emailAddress){
        if(dao.checkUserID(emailAddress)) {
            String twoFACode = generateOTP();
            boolean isEmailSent = emailService.sendEmail(emailAddress, twoFACode);
            if (isEmailSent) {
                dao.update2FAProperties(userID, twoFACode);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint che viene triggerato tramite una chiamata HTTP di tipo PUT. Al triggering, avvia la procedura
     * di invio dell'OTP al numero di telefono specificato nella richiesta HTTP.
     * @param userID ID dell'utente presente nel database
     * @param mobileNumber Numero di telefono dell'utente
     * @return Risposta HTTP che riassume all'utente l'esito della procedura
     */

    @RequestMapping(value=ENDPOINT_SMS, method= RequestMethod.PUT)
    public ResponseEntity<Object> send2faCodeInSMS(@PathVariable(PATH_VARIABLE_USER) String userID, @PathVariable(PATH_VARIABLE_SMS)String mobileNumber){
        String twoFACode = generateOTP();
        smsService.sendSMS(mobileNumber, twoFACode);
        dao.update2FAProperties(userID, twoFACode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Endpoint che viene triggerato tramite una chiamata HTTP di tipo PUT. Al triggering, avvia la procedura
     * di verifica dell'OTP fornito dall'utente alla web-app.
     * @param userID ID dell'utente presente nel database
     * @param twoFACode OTP inserito dall'utente nella web-app
     * @return Risposta HTTP che riassume all'utente l'esito della two-factor authentication
     */

    @RequestMapping(value=ENDPOINT_VERIFICATION, method = RequestMethod.PUT)
    public ResponseEntity<Object> verifyOTP(@PathVariable(PATH_VARIABLE_USER) String userID, @PathVariable(PATH_VARIABLE_OTP)String twoFACode){
        boolean isValid = dao.checkCode(userID, twoFACode);
        if(isValid)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Risposta del server Tomcat sottoforma di json, il quale verrà in seguito parsato dal
     * file intermediary.js
     * @return Risposta json
     */

    @RequestMapping(value=ENDPOINT_CREDENTIAL, method = RequestMethod.POST)
    public ResponseEntity<Object> verifyCredentials()
    {
        JsonObject email_id = new JsonObject();
        email_id.addProperty("email_id","matteo.tabarelli@studenti.unimi.it");
        email_id.addProperty("id","1");
        email_id.addProperty("mobile","3209150845");
        email_id.addProperty("is_2fa_enabled","Y");
        email_id.addProperty("tfa_default_type", "email");

        return new ResponseEntity<Object>(email_id, HttpStatus.OK);
    }
}
