package pt.unl.fct.di.adc.firstwebapp.util.logout;

public class LogoutResponse {
    public String status;
    public LogoutResponseData data;

    public LogoutResponse() {
        this.status = "success";
        this.data = new LogoutResponseData();
    }
}
