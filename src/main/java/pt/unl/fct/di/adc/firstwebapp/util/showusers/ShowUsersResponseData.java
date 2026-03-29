package pt.unl.fct.di.adc.firstwebapp.util.showusers;

import java.util.List;

import pt.unl.fct.di.adc.firstwebapp.util.UserInfo;

public class ShowUsersResponseData {
    public List<UserInfo> users;

    public ShowUsersResponseData() { }

    public ShowUsersResponseData(List<UserInfo> users) {
        this.users = users;
    }
}
