package client.model;

public class ConfigurationResponse {
    private String teamName;
    private boolean enableMessaging;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public boolean isEnableMessaging() {
        return enableMessaging;
    }

    public void setEnableMessaging(final boolean enableMessaging) {
        this.enableMessaging = enableMessaging;
    }
}
