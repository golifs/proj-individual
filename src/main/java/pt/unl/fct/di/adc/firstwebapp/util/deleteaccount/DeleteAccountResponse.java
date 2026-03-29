package pt.unl.fct.di.adc.firstwebapp.util.deleteaccount;

public class DeleteAccountResponse {
    public String status;
    public DeleteAccountResponseData data;

    public DeleteAccountResponse() {
        this.status = "success";
        this.data = new DeleteAccountResponseData();
    }
}
