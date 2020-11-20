package comtest.example.android_team.models.valueModels;

import comtest.example.android_team.R;

public class SetValueModel {

    private int imageIcon;
    private String unit;
    private float rangeMin;
    private float rangeMax;


    public SetValueModel() {
        this.imageIcon = R.drawable.def_on;
        this.unit = "%";
        this.rangeMin = 0;
        this.rangeMax = 100;
    }

    public SetValueModel(int imageIcon, String unit, float rangeMin, float rangeMax) {
        this.imageIcon = imageIcon;
        this.unit = unit;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
    }

    public int getImageIcon() {
        return imageIcon;
    }

    public String getUnit() {
        return unit;
    }

    public float getRangeMin() {
        return rangeMin;
    }

    public float getRangeMax() {
        return rangeMax;
    }
}

