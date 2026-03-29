package pt.unl.fct.di.adc.firstwebapp.util.login;

import pt.unl.fct.di.adc.firstwebapp.util.auth.AuthToken;

public class LoginResponse {
    public String status;
    public LoginResponseData data;

    public LoginResponse() { }

    public LoginResponse(AuthToken token) {
        this.status = "success";
        this.data = new LoginResponseData(token);
    }
}