package client.model;

public class HttpResponseData {
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(final int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultBody() {
        return resultBody;
    }

    public void setResultBody(final String resultBody) {
        this.resultBody = resultBody;
    }

    private int resultCode;
    private String resultBody;
}
