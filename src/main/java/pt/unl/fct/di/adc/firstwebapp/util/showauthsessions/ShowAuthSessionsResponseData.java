package pt.unl.fct.di.adc.firstwebapp.util.showauthsessions;

import java.util.List;

import pt.unl.fct.di.adc.firstwebapp.util.SessionInfo;

public class ShowAuthSessionsResponseData {
    public List<SessionInfo> sessions;

    public ShowAuthSessionsResponseData() { }

    public ShowAuthSessionsResponseData(List<SessionInfo> sessions) {
        this.sessions = sessions;
    }
}
