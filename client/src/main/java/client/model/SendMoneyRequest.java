package client.model;

public class SendMoneyRequest {

    private String recipient;
    private Number amount;
    private String message;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(final Number amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
