package client;

import client.model.SendMoneyRequest;

import java.util.Random;

class Configurator {

    private final String teamName;
    private final boolean messagingEnabled;

    Configurator() {
        // TODO: Replace the empty quotes below with your team name.
        teamName = "";

        // TODO: set to true to enable messages while sending
        messagingEnabled = false;
    }

    boolean isMessagingEnabled() {
        return messagingEnabled;
    }

    String getTeamName() {
        return teamName;
    }

    SendMoneyRequest buildSendMoneyRequest(final String recipient, final Number amount, final String message) {
        final SendMoneyRequest sendMoneyRequest = new SendMoneyRequest();

        sendMoneyRequest.setRecipient(recipient);
        sendMoneyRequest.setAmount(amount);
        if (isMessagingEnabled()) {
            sendMoneyRequest.setMessage(message);
        }
        return sendMoneyRequest;
    }

    private String randomTeamName() {
        final Random random = new Random();
        return "Team" + (random.nextInt(900) + 100);
    }

}
