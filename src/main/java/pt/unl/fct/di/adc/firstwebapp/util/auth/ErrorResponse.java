package pt.unl.fct.di.adc.firstwebapp.util.auth;

public class ErrorResponse {
    public String status;
    public String data;

    public ErrorResponse() { }

    public ErrorResponse(String status, String data) {
        this.status = status;
        this.data = data;
    }
}
