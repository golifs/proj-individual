package pt.unl.fct.di.adc.firstwebapp.util.showauthsessions;

import java.util.List;

import pt.unl.fct.di.adc.firstwebapp.util.SessionInfo;

public class ShowAuthSessionsResponse {
    public String status;
    public ShowAuthSessionsResponseData data;

    public ShowAuthSessionsResponse() { }

    public ShowAuthSessionsResponse(List<SessionInfo> sessions) {
        this.status = "success";
        this.data = new ShowAuthSessionsResponseData(sessions);
    }
}
