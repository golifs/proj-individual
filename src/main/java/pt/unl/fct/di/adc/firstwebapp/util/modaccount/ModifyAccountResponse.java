package pt.unl.fct.di.adc.firstwebapp.util.modaccount;

public class ModifyAccountResponse {
    public String status;
    public ModifyAccountResponseData data;

    public ModifyAccountResponse() {
        this.status = "success";
        this.data = new ModifyAccountResponseData();
    }
}
