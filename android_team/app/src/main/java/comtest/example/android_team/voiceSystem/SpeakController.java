package comtest.example.android_team.voiceSystem;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

import comtest.example.android_team.AppManager;
import comtest.example.android_team.models.gadgets.Gadget;
import comtest.example.android_team.models.gadgets.GadgetType;

public class SpeakController {
    private static final String TAG = "Info";
    private Context activityContext;

    public SpeakController(Context context) {
    this.activityContext = context;
    }

    public void voiceCommand(String speechInput, TTS tts){
        for (Map.Entry<Integer, Gadget> entry : AppManager.getInstance().getGadgets().entrySet()) {

            String gadgetResult = entry.getValue().getGadgetName().toLowerCase();
            Log.i(TAG, gadgetResult);
            if ((speechInput.contains(gadgetResult))) {
                GadgetType type = entry.getValue().getType();

                switch (type) {
                    case SWITCH:
                        if (speechInput.contains("on")) {
                            String logString = "311::" + entry.getValue().getId() + "::1";
                            Log.i(TAG, logString);
                            AppManager.getInstance().requestToServer("311::" + entry.getValue().getId() + "::1");
                        } else if (speechInput.contains("off")) {
                            AppManager.getInstance().requestToServer("311::" + entry.getValue().getId() + "::0");
                        } else {
                            String wrongSentence = "You have to be specific, ON or OFF.";
                            Toast.makeText(this.activityContext, wrongSentence, Toast.LENGTH_LONG).show();
                            tts.textToSpeak(wrongSentence);
                        }

                        break;
                    case SET_VALUE:
                        float f = Float.parseFloat(speechInput.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
                        AppManager.getInstance().requestToServer("311::" + entry.getValue().getId() + "::" + f);
                        Log.i(TAG, "311::" + entry.getValue().getId() + "::" + f);
                }
                break;

            }
        }
    }

}
