package pt.unl.fct.di.adc.firstwebapp.util.changeuserrole;

public class ChangeUserRoleResponse {
    public String status;
    public ChangeUserRoleResponseData data;

    public ChangeUserRoleResponse() {
        this.status = "success";
        this.data = new ChangeUserRoleResponseData();
    }
}
