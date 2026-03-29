package pt.unl.fct.di.adc.firstwebapp.util.createaccount;

public class CreateAccountResponse {
    public String status;
    public CreateAccountResponseData data;

    public CreateAccountResponse() { }

    public CreateAccountResponse(String username, String role) {
        this.status = "success";
        this.data = new CreateAccountResponseData(username, role);
    }
}
