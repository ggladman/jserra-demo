package client;

import client.model.SendMoneyRequest;

public class Configurator {

    public String getTeamName() {
        return "Default Team";  // "Superman";
    }

    public String getMessage(SendMoneyRequest sendMoneyRequest) {
        return null;  //sendMoneyRequest.getMessage();
    }
}
