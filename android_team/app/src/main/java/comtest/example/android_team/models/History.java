package comtest.example.android_team.models;

import java.io.Serializable;

public class History implements Serializable {

    public String homeNetworkSSID;
    public String localHubIP;
    public int localHubPort;

    public History(String homeNetworkSSID, String localHubIP, int localHubPort) {
        this.homeNetworkSSID = homeNetworkSSID;
        this.localHubIP = localHubIP;
        this.localHubPort = localHubPort;
    }

    public String getHomeNetworkSSID() {
        return homeNetworkSSID;
    }

    public String getLocalHubIP() {
        return localHubIP;
    }

    public int getLocalHubPort() {
        return localHubPort;
    }
}
