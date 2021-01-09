package comtest.example.android_team.models.valueModels;

import comtest.example.android_team.R;

public class BinarySensorValueModel {

    private int[] imageIconList;
    private String valueON;
    private String valueOFF;

    public BinarySensorValueModel() {
        this.imageIconList =new int[]{R.drawable.sensor_on, R.drawable.sensor_off};
        this.valueOFF = "IDLE";
        this.valueON = "TRIGGERED";
    }

    public BinarySensorValueModel(int switchImgOn, int switchImgOff, String valueOn, String valueOff) {
        this.imageIconList = new int[]{switchImgOn, switchImgOff};
        this.valueON = valueOn;
        this.valueOFF = valueOff;
    }

    public int getImageIconON() {
        return imageIconList[0];
    }

    public int getImageIconOFF() {
        return imageIconList[1];
    }

    public String getValueON() {
        return valueON;
    }

    public String getValueOFF() {
        return valueOFF;
    }
}
