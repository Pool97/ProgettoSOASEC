package com.controller;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

/**
 * Servizio della business logic per l'invio dell'OTP mediante SMS.
 */

@Service
public class SMSService {
    private final static String ACCOUNT_SID = "ACf07dd7ad3c0bbee174fbe962409f3cf4";
    private final static String AUTH_ID = "ca7644edfdf344962b7659f95b12d92b";
    private final static String CONTENT_DESCR = "Your two Factor Authentication code is: ";
    private final static String TWILIO_PHONE_NUMBER = "+15136570836";

    static{
        Twilio.init(ACCOUNT_SID, AUTH_ID);
    }

    public boolean sendSMS(String mobileNumber, String twoFACode){
        Message.creator(new PhoneNumber(mobileNumber), new PhoneNumber(TWILIO_PHONE_NUMBER),
                CONTENT_DESCR + twoFACode).create();
        return true;
    }
}
