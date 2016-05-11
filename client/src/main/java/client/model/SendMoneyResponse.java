package client.model;

public class SendMoneyResponse {

    private String sender;
    private String recipient;
    private String amount;
    private String message;
    private String flags;

    public String getSender() {
        return sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(final String flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return "SendMoneyResponse [sender=" + sender +
                ", recipient=" + recipient +
                ", amount=" + amount +
                ", message=" + message + "]";
    }
}
