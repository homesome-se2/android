package comtest.example.android_team;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
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

    public boolean appInFocus;

    public UpdateResponse currentFragment;
    // NetWork service

    // map for gadgets. GadgetID/object
    private HashMap<Integer, Gadget> gadgets;

    public HashMap<Integer, Gadget> getGadgets() {

        return gadgets;
    }

    private ArrayList<Gadget> listGadgetMapping;

    public ArrayList<Gadget> getListGadgetMapping() {
        return listGadgetMapping;
    }

    public static AppManager instance = null;

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    AppManager() {
        appInFocus = false;
        currentFragment = null;
    }

    public void initialization() {
        handler = new Handler();
        gadgets = new HashMap<>();
        listGadgetMapping = new ArrayList<>();
        ValueTemplate.getInstance().fillValueTemplates();
    }


    // =========================== HANDLE NETWORK CONNECTION ===============================
    public void establishConnection() {
        httpNetworkService = new HTTPNetworkService(handler);
    }

    public void endConnection() {
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
            case "904":
                exceptionHub(commands);
                break;
        }

    }

    // #102
    private void manuelLogin(String[] commands) {
        String cache = String.format("%s:%s", commands[1], commands[4]);
        currentFragment.update(102, cache, -1);
    }

    // #104
    private void automaticLogin(String[] commands) {
        currentFragment.update(104, "", -1);
    }

    // #107
    private void confirmAllLogout(String[] commands) {
        String message = commands[1];
        currentFragment.update(107, message, -1);

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
        listGadgetMapping = new ArrayList<>(gadgets.values());
        currentFragment.update(304, "", -1);

    }


    // #316
    private void gadgetStateUpdate(String[] commands) {
        int gadgetID = Integer.parseInt(commands[1]);
        float newState = Float.parseFloat(commands[2]);
        // Set new state
        if (!gadgets.isEmpty()) {
            Objects.requireNonNull(gadgets.get(gadgetID)).setState(newState);
            String response = VoiceController.generateVoiceAnswer(gadgets.get(gadgetID));

            int index = getMapIndex(gadgetID);
            listGadgetMapping.set(index, gadgets.get(gadgetID));
            currentFragment.update(316, response, index);
        }
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

        listGadgetMapping.add(gadget);
        int index = getMapIndex(gadgetID);
        currentFragment.update(352, "", index);
    }

    //#354
    private void removeGadget(String[] commands) {
        if (!gadgets.isEmpty()) {
            int gadgetID = Integer.parseInt(commands[1]);
            int index = getMapIndex(gadgetID);
            gadgets.remove(gadgetID);
            Log.i(TAG, "removeGadget: " + index);
            getListGadgetMapping().remove(index);
            currentFragment.update(354, "", index);
        }
    }

    //#404
    private void aliasChange(String[] commands) {
        if (!gadgets.isEmpty()) {
            int gadgetID = Integer.parseInt(commands[1]);
            String newAlias = commands[2];
            Objects.requireNonNull(gadgets.get(gadgetID)).setGadgetName(newAlias);

            int index = getMapIndex(gadgetID);
            listGadgetMapping.set(index, gadgets.get(gadgetID));
            currentFragment.update(404, "", index);
        }
    }

    // #901
    private void exceptionMSG(String[] commands) {
        String error = "Exception msg: " + commands[1];
        Log.e(TAG, error);
        currentFragment.update(901, error, -1);
    }

    // #903
    private void exceptionFailedLogIn(String[] commands) {
        String error = "Exception msg: " + commands[1];
        Log.e(TAG, error);
        currentFragment.update(903, error, -1);
    }

    // #904
    private void exceptionHub(String[] commands) {
        String error = "Exception msg: " + commands[1];
        Log.e(TAG, error);
        currentFragment.update(904, error, -1);
    }


    private int getMapIndex(int gadgetId) {
        int countIndex = 0;
        for (Gadget entry : getListGadgetMapping()) {
            if (entry.getId() == gadgetId) {
                return countIndex;
            }
            countIndex++;
        }
        return -1;
    }
}
