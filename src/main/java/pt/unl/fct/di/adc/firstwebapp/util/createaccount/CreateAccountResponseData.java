package pt.unl.fct.di.adc.firstwebapp.util.createaccount;

public class CreateAccountResponseData {
    public String username;
    public String role;

    public CreateAccountResponseData() { }

    public CreateAccountResponseData(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
