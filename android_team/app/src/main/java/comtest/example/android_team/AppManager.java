package comtest.example.android_team;

import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import comtest.example.android_team.models.gadgets.GadgetType;
import comtest.example.android_team.network.HTTPNetworkService;
import comtest.example.android_team.models.gadgets.Gadget_basic;

public class AppManager {
    private static final String TAG = "Info";
    private static HTTPNetworkService httpNetworkService;
    // Handler of UI-thread. For communication: Service threads -> UI thread
    private Handler handler;

    public UpdateResponse currentFragment;
    // NetWork service


    // map for gadgets. GadgetID/object
    private HashMap<Integer, Gadget_basic> gadgets;

    public HashMap<Integer, Gadget_basic> getGadgets() {

        return gadgets;
    }

    public static AppManager instance = null;

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    private AppManager() {
        handler = new Handler();
        currentFragment = null;
        gadgets = new HashMap<>();

    }


    // =========================== HANDLE NETWORK CONNECTION ===============================
    public void establishConnection() {
        httpNetworkService = new HTTPNetworkService(handler);
    }

    public static void endConnection() {
        httpNetworkService.getWebSocketClient().close();
        Log.i(TAG, "C: Socket is closed!");
    }

    public boolean networkNotNull(){
        return httpNetworkService != null;
    }

    public void requestToServer(String request) {
        httpNetworkService.getWebSocketClient().send(request);
    }
    // =====================================================================================

    public void handleServerResponse(String response) {
        String[] commands = response.split("::");
        switch (commands[0]) {
            case "102":
                manuelLogin(commands);
                break;
            case "104":
                automaticLogin(commands);
                break;
            case "304":
                receiveAllGadgets(commands);
                break;
            case "316":
                gadgetStateUpdate(commands);
                break;
            case "901":
                exceptionMSG(commands);
                break;
            case "903":
                exceptionFailedLogIn(commands);
                break;
        }

    }


    // #102
    private void manuelLogin(String[] commands){
        String cache = String.format("%s:%s", commands[1], commands[4]);
        currentFragment.update(102, cache);
    }

    // #104
    private void automaticLogin(String[] commands) {
        currentFragment.update(104, "");
    }


    // #304
    private void receiveAllGadgets(String[] commands) {
        int nbrOfGadgets = Integer.parseInt(commands[1]);
        int count = 2; // Start index to read in gadgets
        for (int i = 0; i < nbrOfGadgets; i++) {
            int gadgetID = Integer.parseInt(commands[count++]);
            String alias = commands[count++];
            GadgetType type = GadgetType.valueOf(commands[count++]);
            String valueTemplate = commands[count++];
            float state = Float.parseFloat(commands[count++]);
            long pollDelaySeconds = Long.parseLong(commands[count++]);
            Gadget_basic gadgetBasic = new Gadget_basic(gadgetID, alias, type, valueTemplate, state);
            gadgets.put(gadgetID, gadgetBasic);
        }
        currentFragment.update(304, "");

        //TODO tempt print! delete later
        for(Map.Entry<Integer, Gadget_basic> entry : gadgets.entrySet()) {
            Log.i(TAG, entry.toString());
        }
    }

    // #316
    private void gadgetStateUpdate(String[] commands) {
        int gadgetID = Integer.parseInt(commands[1]);
        float newState = Float.parseFloat(commands[2]);
        // Set new state
        gadgets.get(gadgetID).setState(newState);
        currentFragment.update(316, String.valueOf(newState));
    }

    // #901
    private void exceptionMSG(String[] commands){
        String error = "Exception msg: " + commands[1];
        Log.e(TAG, error);
        currentFragment.update(901, error);
    }

    // #903
    private void exceptionFailedLogIn(String[] commands){
        String error = "Exception msg: " + commands[1];
        Log.e(TAG, error);
        currentFragment.update(903, error);
    }

}
