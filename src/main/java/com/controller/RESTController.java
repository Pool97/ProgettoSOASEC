package com.controller;

import com.google.gson.JsonObject;
import com.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     *
     * @return OTP generato
     */
    private String generateOTP(){
        return String.valueOf(new Random().nextInt(9999) + 1000);
    }

    /**
     * Endpoint che viene triggerato tramite una chiamata HTTP di tipo PUT. Al triggering, avvia la procedura
     * di invio dell'OTP alla mail specificata nella richiesta HTTP.
     *
     * @param userID ID dell'utente presente nel database
     * @param emailAddress Indirizzo e-mail dell'utente
     * @return Risposta HTTP che riassume all'utente l'esito della procedura.
     */

    @RequestMapping(value= ENDPOINT_EMAIL, method= RequestMethod.PUT)
    public ResponseEntity<Object> sendOTPViaEmail(@PathVariable(PATH_VARIABLE_USER) String userID, @PathVariable(PATH_VARIABLE_MAIL)String emailAddress){
        String twoFACode = generateOTP();
        boolean isEmailSent = emailService.sendEmail(emailAddress, twoFACode);

        if (isEmailSent) {
            dao.update2FAProperties(userID, twoFACode);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint che viene triggerato tramite una chiamata HTTP di tipo PUT. Al triggering, avvia la procedura
     * di invio dell'OTP al numero di telefono specificato nella richiesta HTTP.
     *
     * @param userID ID dell'utente presente nel database
     * @param mobileNumber Numero di telefono dell'utente
     * @return Risposta HTTP che riassume all'utente l'esito della procedura
     */

    @RequestMapping(value=ENDPOINT_SMS, method= RequestMethod.PUT)
    public ResponseEntity<Object> sendOTPViaSMS(@PathVariable(PATH_VARIABLE_USER) String userID, @PathVariable(PATH_VARIABLE_SMS)String mobileNumber){
        String twoFACode = generateOTP();

        smsService.sendSMS(mobileNumber, twoFACode);
        dao.update2FAProperties(userID, twoFACode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Endpoint che viene triggerato tramite una chiamata HTTP di tipo PUT. Al triggering, avvia la procedura
     * di verifica dell'OTP fornito dall'utente alla web-app.
     *
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
     * Verifica che le credenziali dell'utente fornite nel form HTML siano effettivamente presenti nel DB. Il server
     * Tomcat fornisce una risposta contenente i dettagli dell'utente in formato JSON se l'utente esiste, che verrà in
     * seguito parsata lato client dal file intermediary.js.
     *
     * @return Risposta JSON con i dettagli dell'utente
     */

    @RequestMapping(value=ENDPOINT_CREDENTIAL, method = RequestMethod.POST)
    public ResponseEntity<Object> verifyCredentials(@RequestBody String requestData)
    {
        String[] credentials = translateRequestInCredentials(requestData);
        UserEntity user = (UserEntity) dao.loadUserByUsername(credentials[0]);

        if(user == null || !credentials[1].equals(user.getPassword()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(prepareUserDetailsInJSON(user), HttpStatus.OK);

    }

    /**
     * La richiesta POST HTTP proveniente dal client è una stringa della forma username=xxx%40xxx&pass=yyyy, dove
     * %40 è il percent-encoding del carattere @. Il seguente metodo è incaricato di ottenere dalla precedente
     * stringa un'array di stringhe di lunghezza due, contenente rispettivamente email e password inserite dall'utente
     * nel form HTML.
     *
     * @param requestData Contenuto della richiesta POST
     * @return Array contenente le credenziali dell'utente
     */

    private String[] translateRequestInCredentials(String requestData){
        String[] credentialArray = requestData.split("&");
        credentialArray[0] = credentialArray[0].replace("%40", "@").substring(9);
        credentialArray[1] = credentialArray[1].substring(5);
        return credentialArray;
    }

    /**
     * Crea un oggetto JSON da inviare al client, contenente tutti i dettagli dell'utente per poter effettuare
     * una richiesta HTTP a uno degli endpoint preposti per la 2FA.
     *
     * @param user Utente
     * @return Oggetto JSON contenente i dettagli dell'utente
     */

    private JsonObject prepareUserDetailsInJSON(UserEntity user){
        JsonObject userDetails = new JsonObject();
        userDetails.addProperty("userID", user.getId());
        userDetails.addProperty("emailAddress", user.getEmailAddress());
        userDetails.addProperty("mobileNumber", user.getMobile());
        userDetails.addProperty("tfaDefaultType", user.getTfaDefaultType());
        return userDetails;
    }
}
