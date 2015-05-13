package client;

import client.model.SendMoneyRequest;

public class Configurator {

    public String getTeamName() {
        //return "Default Team";  // "Superman";
        return null;
    }

    public SendMoneyRequest buildSendMoneyRequest(String recipient, String amount, String message) {
        SendMoneyRequest sendMoneyRequest = new SendMoneyRequest();

        sendMoneyRequest.setRecipient(recipient);
        sendMoneyRequest.setAmount(amount);
        //sendMoneyRequest.setMessage(message);

        return sendMoneyRequest;
    }
}
