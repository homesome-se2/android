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
                text = sensorString(gadget);
                break;
            case SET_VALUE:
                break;
        }
        return text;
    }

    private static String sensorString(Gadget gadget) {
        switch (gadget.getValueTemplate()) {
            case "default":
                return ResourceHelper.resources.getString(R.string.SENSOR_VALUE, gadget.getGadgetName(), String.valueOf(gadget.getState()));
            case "temp":
                return ResourceHelper.resources.getString(R.string.SENSOR_TEMP_VALUE, gadget.getGadgetName(), String.valueOf(gadget.getState()));
            case "percent":
                return ResourceHelper.resources.getString(R.string.SENSOR_PERCENT_VALUE, gadget.getGadgetName(), String.valueOf(gadget.getState()));
        }
        return "";
    }

}
