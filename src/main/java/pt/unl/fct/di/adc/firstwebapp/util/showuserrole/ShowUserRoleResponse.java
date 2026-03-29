package pt.unl.fct.di.adc.firstwebapp.util.showuserrole;

public class ShowUserRoleResponse {
    public String status;
    public ShowUserRoleResponseData data;

    public ShowUserRoleResponse() { }

    public ShowUserRoleResponse(String username, String role) {
        this.status = "success";
        this.data = new ShowUserRoleResponseData(username, role);
    }
}
