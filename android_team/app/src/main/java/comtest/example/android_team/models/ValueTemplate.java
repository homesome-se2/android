package comtest.example.android_team.models;

import java.util.HashMap;

import comtest.example.android_team.R;
import comtest.example.android_team.models.valueModels.BinarySensorValueModel;
import comtest.example.android_team.models.valueModels.SensorValueModel;
import comtest.example.android_team.models.valueModels.SetValueModel;
import comtest.example.android_team.models.valueModels.SwitchValueModel;

public class ValueTemplate {

    private HashMap<String, SwitchValueModel> switchTemplate;
    private HashMap<String, SensorValueModel> sensorTemplate;
    private HashMap<String, BinarySensorValueModel> biSensorTemplate;
    private HashMap<String, SetValueModel> setValueHashMap;

    private static ValueTemplate instance = null;

    public static ValueTemplate getInstance(){
        if (instance == null){
            instance = new ValueTemplate();
        }
        return instance;
    }

    public ValueTemplate() {
    }

    public void fillValueTemplates(){
        this.switchTemplate = new HashMap<>();
        this.sensorTemplate = new HashMap<>();
        this.biSensorTemplate = new HashMap<>();
        this.setValueHashMap = new HashMap<>();

        // ************ Switch *****************
        this.switchTemplate.put("default", new SwitchValueModel());
        this.switchTemplate.put("light", new SwitchValueModel(R.drawable.light_on,R.drawable.light_off, "ON", "OFF"));
        this.switchTemplate.put("fan", new SwitchValueModel(R.drawable.fan_on,R.drawable.fan_off, "ON", "OFF"));

        // ************ Sensor *****************
        this.sensorTemplate.put("default", new SensorValueModel());
        this.sensorTemplate.put("temp", new SensorValueModel(R.drawable.thermometer,"°C",-40,60));
        this.sensorTemplate.put("percent", new SensorValueModel(R.drawable.percent,"%",0,100));

        // ************ BinarySensor *****************
        this.biSensorTemplate.put("default", new BinarySensorValueModel());
        this.biSensorTemplate.put("door", new BinarySensorValueModel(R.drawable.door_open,R.drawable.door_closed,"OPEN", "CLOSED"));
        this.biSensorTemplate.put("person", new BinarySensorValueModel(R.drawable.home,R.drawable.away,"HOME", "AWAY"));
        this.biSensorTemplate.put("detectorBurglar", new BinarySensorValueModel(R.drawable.motion_sens_on,R.drawable.motion_sens_off,"MOTION DETECTED!", "IDLE"));

        // ************ Setvalue *****************
        this.setValueHashMap.put("default", new SetValueModel());
    }

    public HashMap<String, SwitchValueModel> getSwitchTemplate() {
        return switchTemplate;
    }

    public HashMap<String, SensorValueModel> getSensorTemplate() {
        return sensorTemplate;
    }

    public HashMap<String, BinarySensorValueModel> getBiSensorTemplate() {
        return biSensorTemplate;
    }

    public HashMap<String, SetValueModel> getSetValueHashMap() {
        return setValueHashMap;
    }
}
