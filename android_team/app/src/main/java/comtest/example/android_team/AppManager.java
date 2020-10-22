package comtest.example.android_team;

import android.os.Handler;
import android.util.Log;

import comtest.example.android_team.network.HTTPNetworkService;

public class AppManager {
    private static final String TAG = "Info";
    private Handler handler;

    private HTTPNetworkService httpNetworkService;

    public void establishConnection(){
        httpNetworkService = new HTTPNetworkService(handler);

    }

    public void endConnection(){
        httpNetworkService.getWebSocketClient().close();
        Log.i(TAG,"C: Socket is closed!");
    }

    public void requestToServer(String request){
        httpNetworkService.getWebSocketClient().send(request);
    }

    public static AppManager instance = null;

    public static AppManager getInstance(){
        if (instance == null){
            instance = new AppManager();
        }
        return instance;
    }
}
