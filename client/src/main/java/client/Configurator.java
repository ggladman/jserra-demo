package client;

import client.model.SendMoneyRequest;

public class Configurator {

    public String getTeamName() {
        return "Default Team";  // "Superman";
    }

    public SendMoneyRequest buildSendMoneyRequest(String recipient, Number amount, String message) {
        SendMoneyRequest sendMoneyRequest = new SendMoneyRequest();

        sendMoneyRequest.setRecipient(recipient);
        sendMoneyRequest.setAmount(amount);
        //sendMoneyRequest.setMessage(message);

        return sendMoneyRequest;
    }
}
