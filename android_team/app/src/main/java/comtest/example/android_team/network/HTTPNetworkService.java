package comtest.example.android_team.network;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import android.os.Handler;

import comtest.example.android_team.AppManager;
import tech.gusavila92.websocketclient.WebSocketClient;

public class HTTPNetworkService {

    /* Establish a connection to the server, and the void methods where we have Log.i logs what is happening in this class */

    private WebSocketClient webSocketClient;
    public static final String TAG = "Info";

    public static final String SERVER_ID = "134.209.198.123";
    public static final int SERVER_PORT = 8084;
    private Handler handler;
    private boolean isConnected;

    public WebSocketClient getWebSocketClient(){
        return webSocketClient;
    }

    public HTTPNetworkService(Handler handler){
        this.handler = handler;
        this.isConnected = false;
        URI uri;

        try {
            uri = new URI("ws://"+SERVER_ID+":"+SERVER_PORT+"/homesome");
        } catch (URISyntaxException e){
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                setConnected(true);
                Log.i(TAG,"C: Connected to server!");
            }

            @Override
            public void onTextReceived(String message) {
                Log.i(TAG,"Message received: " + message);
                updateUIThread(message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG,e.getMessage(),e);
            }

            @Override
            public void onCloseReceived() {
                setConnected(false);
                Log.i(TAG,"S: Connection closed!");
            }
        };
        Log.i(TAG, "C: Trying to connect...");
        webSocketClient.connect();
    }
    private void updateUIThread(final String request){
        handler.post(new Runnable() {
            @Override
           public void run() {
                AppManager.getInstance().handleServerResponse(request);
           }
       });
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
