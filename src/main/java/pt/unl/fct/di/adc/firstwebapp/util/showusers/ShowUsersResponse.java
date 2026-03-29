package pt.unl.fct.di.adc.firstwebapp.util.showusers;

import java.util.List;

import pt.unl.fct.di.adc.firstwebapp.util.UserInfo;

public class ShowUsersResponse {
    public String status;
    public ShowUsersResponseData data;

    public ShowUsersResponse() { }

    public ShowUsersResponse(List<UserInfo> users) {
        this.status = "success";
        this.data = new ShowUsersResponseData(users);
    }
}
