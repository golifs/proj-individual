package pt.unl.fct.di.adc.firstwebapp.util.changeuserpassword;

public class ChangeUserPasswordResponse {
    public String status;
    public ChangeUserPasswordResponseData data;

    public ChangeUserPasswordResponse() {
        this.status = "success";
        this.data = new ChangeUserPasswordResponseData();
    }
}
