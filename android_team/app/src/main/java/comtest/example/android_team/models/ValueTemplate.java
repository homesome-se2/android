package comtest.example.android_team.models;

import java.util.ArrayList;
import java.util.HashMap;

import comtest.example.android_team.R;
import comtest.example.android_team.models.gadgets.GadgetType;
import comtest.example.android_team.models.valueModels.SensorValueModel;
import comtest.example.android_team.models.valueModels.SwitchValueModel;

public class ValueTemplate {

    private HashMap<String, SwitchValueModel> switchTemplate;
    private HashMap<String, SensorValueModel> sensorTemplate;

    public ValueTemplate() {
        this.switchTemplate = new HashMap<>();
        this.sensorTemplate = new HashMap<>();
        fillTemplates();
    }

    private void fillTemplates(){

        // ************ Switch *****************
        this.switchTemplate.put("default", new SwitchValueModel());
        this.switchTemplate.put("light", new SwitchValueModel(R.drawable.light_on,R.drawable.light_off));

        // ************ Sensor *****************

        this.sensorTemplate.put("default", new SensorValueModel());
        this.sensorTemplate.put("temp", new SensorValueModel(R.drawable.thermometer,"°C",0,1023));
    }

    public HashMap<String, SwitchValueModel> getSwitchTemplate() {
        return switchTemplate;
    }

    public HashMap<String, SensorValueModel> getSensorTemplate() {
        return sensorTemplate;
    }
}
