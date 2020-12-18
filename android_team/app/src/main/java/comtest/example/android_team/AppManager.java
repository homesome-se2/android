package comtest.example.android_team;

import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.Objects;

import comtest.example.android_team.models.ValueTemplate;
import comtest.example.android_team.models.gadgets.Gadget;
import comtest.example.android_team.models.gadgets.GadgetType;
import comtest.example.android_team.network.HTTPNetworkService;
import comtest.example.android_team.voiceSystem.VoiceController;

/* This class works to manipulate and work with the data. To connect to the network, and handle server respones. Background work is done here! */

public class AppManager {
    private static final String TAG = "Info";
    private static HTTPNetworkService httpNetworkService;
    // Handler of UI-thread. For communication: Service threads -> UI thread
    private Handler handler;

    public UpdateResponse currentFragment;
    // NetWork service

    private ValueTemplate valueTemplate;

    public ValueTemplate getValueTemplate() {
        return valueTemplate;
    }

    // map for gadgets. GadgetID/object
    private HashMap<Integer, Gadget> gadgets;

    public HashMap<Integer, Gadget> getGadgets() {

        return gadgets;
    }

    public static AppManager instance = null;

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    AppManager() {
    }

    public void initialization(){
        currentFragment = null;
        handler = new Handler();
        gadgets = new HashMap<>();
        ValueTemplate.getInstance().fillValueTemplates();
    }


    // =========================== HANDLE NETWORK CONNECTION ===============================
    public void establishConnection() {
        httpNetworkService = new HTTPNetworkService(handler);
    }

    public  void endConnection() {
        httpNetworkService.getWebSocketClient().close();
        httpNetworkService.setConnected(false);
        Log.i(TAG, "C: Socket is closed!");
    }

    public boolean networkNotNull() {
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
            case "107":
                confirmAllLogout(commands);
                break;
            case "304":
                receiveAllGadgets(commands);
                break;
            case "316":
                gadgetStateUpdate(commands);
                break;
            case "352":
                forwardNewGadget(commands);
                break;
            case "354":
                removeGadget(commands);
                break;
            case "404":
                aliasChange(commands);
            case "901":
                exceptionMSG(commands);
                break;
            case "903":
                exceptionFailedLogIn(commands);
                break;
        }

    }

    // #102
    private void manuelLogin(String[] commands) {
        String cache = String.format("%s:%s", commands[1], commands[4]);
        currentFragment.update(102, cache,null);
    }

    // #104
    private void automaticLogin(String[] commands) {
        currentFragment.update(104, "",null);
    }

    // #107
    private void confirmAllLogout(String[] commands){
        String message = commands[1];
        currentFragment.update(107, message, null);

    }


    // #304
    private void receiveAllGadgets(String[] commands) {
        gadgets.clear();
        int nbrOfGadgets = Integer.parseInt(commands[1]);
        int count = 2; // Start index to read in gadgets
        for (int i = 0; i < nbrOfGadgets; i++) {
            int gadgetID = Integer.parseInt(commands[count++]);
            String alias = commands[count++];
            GadgetType type = GadgetType.valueOf(commands[count++]);
            String valueTemplate = commands[count++];
            float state = Float.parseFloat(commands[count++]);
            count++; // Skipping pollDelaySec

            // Build gadget
            Gadget gadget = new Gadget.GadgetBuilder()
                    .id(gadgetID)
                    .gadgetName(alias)
                    .type(type)
                    .valueTemplate(valueTemplate)
                    .state(state)
                    .build();
            gadgets.put(gadgetID, gadget);
        }
        currentFragment.update(304, "",null);

    }


    // #316
    private void gadgetStateUpdate(String[] commands) {
        int gadgetID = Integer.parseInt(commands[1]);
        float newState = Float.parseFloat(commands[2]);
        // Set new state
        gadgets.get(gadgetID).setState(newState);
        String response = VoiceController.generateVoiceAnswer(gadgets.get(gadgetID));
        currentFragment.update(316, response,gadgetID);
    }

    // #352
    private void forwardNewGadget(String[] commands) {
        int count = 1;
        int gadgetID = Integer.parseInt(commands[count++]);
        String alias = commands[count++];
        GadgetType type = GadgetType.valueOf(commands[count++]);
        String valueTemplate = commands[count++];
        float state = Float.parseFloat(commands[count++]);

        Gadget gadget = new Gadget.GadgetBuilder()
                .id(gadgetID)
                .gadgetName(alias)
                .type(type)
                .valueTemplate(valueTemplate)
                .state(state)
                .build();
        gadgets.put(gadgetID, gadget);

        currentFragment.update(352, "", gadgetID);
    }

    //#354
    private void removeGadget(String[] commands){
        int index = 0;
        for (int i = 0; i < gadgets.size(); i++){
            if (Objects.requireNonNull(gadgets.get(i)).getId() == Integer.parseInt(commands[1])){
                index = gadgets.get(i).getId();
                gadgets.remove(i);
            }

        }
        currentFragment.update(354,"", index);
    }

    //#404
    private void aliasChange(String[] commands){
        int index = 0;
        for (int i = 0; i < gadgets.size(); i++){
            if (Objects.requireNonNull(gadgets.get(i)).getId() == Integer.parseInt(commands[1])){
                Objects.requireNonNull(gadgets.get(i)).setGadgetName(commands[2]);
                index = gadgets.get(i).getId();
            }
        }
        currentFragment.update(404,"",index);
    }

    // #901
    private void exceptionMSG(String[] commands) {
        String error = "Exception msg: " + commands[1];
        Log.e(TAG, error);
        currentFragment.update(901, error,null);
    }

    // #903
    private void exceptionFailedLogIn(String[] commands) {
        String error = "Exception msg: " + commands[1];
        Log.e(TAG, error);
        currentFragment.update(903, error,null);
    }
}
