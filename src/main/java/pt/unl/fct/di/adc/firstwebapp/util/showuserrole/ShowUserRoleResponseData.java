package pt.unl.fct.di.adc.firstwebapp.util.showuserrole;

public class ShowUserRoleResponseData {
    public String username;
    public String role;

    public ShowUserRoleResponseData() { }

    public ShowUserRoleResponseData(String username, String role) {
        this.username = username;
        this.role = role;
    }
}