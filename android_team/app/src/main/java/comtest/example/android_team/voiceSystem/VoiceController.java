package comtest.example.android_team.voiceSystem;

import comtest.example.android_team.R;
import comtest.example.android_team.models.gadgets.Gadget;

public class VoiceController {


    public static String generateVoiceAnswer(Gadget gadget) {
        return createString(gadget);
    }

    private static String createString(Gadget gadget) {
        String text = "";
        switch (gadget.getType()) {
            case SWITCH:
                if (gadget.getState() == 1) {
                    text = ResourceHelper.resources.getString(R.string.VOICE_ON, gadget.getGadgetName());
                } else {
                    text = ResourceHelper.resources.getString(R.string.VOICE_OFF, gadget.getGadgetName());
                }
                break;
            case BINARY_SENSOR:
                if (gadget.getState() == 1) {
                    text = ResourceHelper.resources.getString(R.string.VOICE_ON, gadget.getGadgetName());
                } else {
                    text = ResourceHelper.resources.getString(R.string.VOICE_OFF, gadget.getGadgetName());
                }
                break;
            case SENSOR:
                text = ResourceHelper.resources.getString(R.string.SENSOR_TEMP_VALUE, gadget.getGadgetName(), String.valueOf(gadget.getState()));
                break;
            case SET_VALUE:
                break;
        }
        return text;
    }

    
}
