package comtest.example.android_team.voiceSystem;

import comtest.example.android_team.R;
import comtest.example.android_team.models.ValueTemplate;
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
                text = binarySensorString(gadget);
                break;
            case SENSOR:
                text = sensorString(gadget);
                break;
            case SET_VALUE:
                text = ResourceHelper.resources.getString(R.string.SET_VALUE_PERCENT, gadget.getGadgetName(), String.valueOf(gadget.getState()));
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

    private static String binarySensorString(Gadget gadget) {
        switch (gadget.getValueTemplate()) {
            case "default":
            case "detectorBurglar":
                if (gadget.getState() == 1) {
                    return ResourceHelper.resources.getString(R.string.BINARY_SENSOR_TRIGGERED, gadget.getGadgetName());
                } else {
                    return ResourceHelper.resources.getString(R.string.BINARY_SENSOR_IDLE, gadget.getGadgetName());
                }
            case "door":
                if (gadget.getState() == 1) {
                    return ResourceHelper.resources.getString(R.string.BINARY_SENSOR_TRIGGER_VALUE, gadget.getGadgetName(), ValueTemplate.getInstance().getBiSensorTemplate().get("door").getValueON());
                } else {
                    return ResourceHelper.resources.getString(R.string.BINARY_SENSOR_TRIGGER_VALUE, gadget.getGadgetName(), ValueTemplate.getInstance().getBiSensorTemplate().get("door").getValueOFF());
                }
            case "person":
                if (gadget.getState() == 1) {
                    return ResourceHelper.resources.getString(R.string.BINARY_SENSOR_TRIGGER_VALUE, gadget.getGadgetName(), ValueTemplate.getInstance().getBiSensorTemplate().get("person").getValueON());
                } else {
                    return ResourceHelper.resources.getString(R.string.BINARY_SENSOR_TRIGGER_VALUE, gadget.getGadgetName(), ValueTemplate.getInstance().getBiSensorTemplate().get("person").getValueOFF());
                }
        }
        return "";
    }
}
