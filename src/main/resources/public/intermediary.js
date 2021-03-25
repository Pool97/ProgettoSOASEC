$(document).ready(function(){

    /**
     * Ad ogni tasto premuto quando l'utente digita nel campo email verifica se le condizioni per
     * poter effettuare il login sono soddisfatte.
     */

    $('#emailAddressTxt').keyup(function(){
        checkAllowLogin();
    });

    /**
     * Ad ogni tasto premuto quando l'utente digita nel campo password verifica se le condizioni per
     * poter effettuare il login sono soddisfatte.
     */

    $('#pwd').keyup(function(){
        checkAllowLogin();
    });

    /**
     * Effettua diversi controlli sui campi email e password del form HTML al fine di abilitare il pulsante di login e
     * concedere all'utente la possibilità di fornire il primo fattore al server.
     */

    function checkAllowLogin(){
        let emailAddress = $("#emailAddressTxt").val();
        let password = $("#pwd").val();

        if(emailAddress != null && password != null && emailAddress.length > 0 && password.length > 0 && validateEmail(emailAddress))
            $("#submitBtn").removeAttr("disabled");
        else if(!$("#submitBtn").attr("disabled"))
            $("#submitBtn").attr("disabled","disabled");
    }

    /**
     * Verifica che l'e-mail fornita dall'utente possa essere generata attraverso la seguente espressione regolare e, che
     * quindi, sia sintatticamente corretta.
     * @param emailAddress Indirizzo e-mail fornito dall'utente nel campo di testo
     * @returns {boolean} Validità sintattica dell'e-mail
     */

    function validateEmail(emailAddress){
        let filter = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
        return filter.test(emailAddress);
    }


    $("#submitBtn").click(function(){
        $("#alertCredentials").hide();
        $(".alert-danger").hide();
        provideCredentialsToServer($('#emailAddressTxt').val().trim(),$('#pwd').val());
    });

    /**
     * Apre un modal per l'inserimento di tutti i dettagli necessari affinché il nuovo utente possa effettuare la 2FA.
     * I dettagli verranno registrati nel DB (feature non ancora disponibile).
     */

    $("#signUpBtn").click(function(){
        $("#SignUpModal").modal('show');
    });

    /**
     * Re-invia un codice OTP tramite e-mail su richiesta dell'utente tramite il link "Resend code (Email)".
     */

    $("#sendEmailLink").click(function(){
        getTFACodeFromServer(sessionStorage.getItem("emailTFAUrl"), function() {
            $("#2fa_error").hide();
            const alertSuccess = $(".alert-success");
            alertSuccess.show();
            alertSuccess.html("Your code has been sent to your email address.");
        });
    });

    /**
     * Re-invia un codice OTP tramite SMS su richiesta dell'utente tramite il link "Resend code (Email)".
     */

    $("#sendSMSLink").click(function(){
        getTFACodeFromServer(sessionStorage.getItem("mobileTFAUrl"), function(){
            const alertSuccess = $(".alert-success");
            alertSuccess.show();
            $("#2fa_error").hide();
            alertSuccess.html("Your code has been sent to your mobile number.");
        });
    });

    /**
     * Verifica che il codice inserito dall'utente sia effettivamente quello generato dal server.
     */

    $("#verifyBtn").click(function(){
        verifyTFACode();
    });

});

/**
 * Fornisce le credenziali inserite dall'utente al server. Se sono corrette richiede al server
 * la generazione di un codice OTP che verrà inviato tramite la modalità preferita dall'utente.
 *
 * @param username Indirizzo e-mail dell'utente
 * @param password Password dell'utente
 */

function provideCredentialsToServer(username, password){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/intermediary",
        data: {
            'username': username,
            'pass': password
        },

        success: function(userDetails){
            $(".alert-danger").hide();
            let userDetailsFromJSON = JSON.parse(JSON.stringify(userDetails));

            const baseAddress = "http://localhost:8080/users/";
            const emailTFAUrl = baseAddress + userDetailsFromJSON.userID + "/emails/" + userDetailsFromJSON.emailAddress + "/2fa";
            const mobileTFAUrl = baseAddress + userDetailsFromJSON.userID + "/mobilenumbers/" + userDetailsFromJSON.mobileNumber + "/2fa";
            const verifyTFAUrl = baseAddress + userDetailsFromJSON.userID + "/codes/";

            sessionStorage.setItem("emailTFAUrl", emailTFAUrl);
            sessionStorage.setItem("mobileTFAUrl", mobileTFAUrl);
            sessionStorage.setItem("verifyTFAUrl", verifyTFAUrl);

            let endpointTFA;
            if(userDetailsFromJSON.tfaDefaultType == "sms")
                endpointTFA = mobileTFAUrl;
            else
                endpointTFA = emailTFAUrl;

            getTFACodeFromServer(endpointTFA, function(){
                $("#myModal").modal('show');
            });
        },

        error: function(){
            $(".alert-danger").show()
        }
    });
}

/**
 * Richiede al server la generazione di un codice OTP per l'utente. Ricevuta la risposta del server viene
 * eseguito il metodo di callback.
 */

function getTFACodeFromServer(endpointTFA, callbackMethod){
    $.ajax({
        type: "PUT",
        url: endpointTFA,
        success: function() {
            callbackMethod()
        },

        error: function() {
            const alertDanger = $(".alert-danger");
            alertDanger.show();
            alertDanger.html("Unable to send 2FA code. Please try again.");
        }
    });
}

/**
 * Richiede al server una verifica dell'OTP fornito dall'utente nel modal. In caso positivo l'utente
 * viene redirezionato sulla pagina admnistrator.html.
 */

function verifyTFACode(){
    $.ajax({
        type: "PUT",
        url: sessionStorage.getItem("verifyTFAUrl") + $("#tfa_code").val(),
        success: function(){
            window.location.href = "administrator.html"
        },

        error: function () {
            const alertDanger = $("#2fa_error");
            alertDanger.show();
            $(".alert-success").hide();
            alertDanger.html("Invalid code. Please try again.");
        }
    });
}