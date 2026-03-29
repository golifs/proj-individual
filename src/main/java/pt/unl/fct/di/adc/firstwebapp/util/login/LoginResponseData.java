package pt.unl.fct.di.adc.firstwebapp.util.login;

import pt.unl.fct.di.adc.firstwebapp.util.auth.AuthToken;

public class LoginResponseData {
    public AuthToken token;

    public LoginResponseData() { }

    public LoginResponseData(AuthToken token) {
        this.token = token;
    }
}
