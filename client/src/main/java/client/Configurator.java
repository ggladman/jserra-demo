package client;

import client.model.SendMoneyRequest;

public class Configurator {

    public String getTeamName() {
        // TODO: Put your team name in between the quotes below.
        return "";
    }

    public SendMoneyRequest buildSendMoneyRequest(String recipient, Number amount, String message) {
        SendMoneyRequest sendMoneyRequest = new SendMoneyRequest();

        sendMoneyRequest.setRecipient(recipient);
        sendMoneyRequest.setAmount(amount);
        // TODO: uncomment to enable messages while sending
        //sendMoneyRequest.setMessage(message);

        return sendMoneyRequest;
    }
}
