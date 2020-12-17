package comtest.example.android_team.models.valueModels;

import comtest.example.android_team.R;

public class SetValueModel {

    private int imageIcon;
    private int[] imageValueIcon;
    private String unit;
    private float rangeMin;
    private float rangeMax;


    public SetValueModel() {
        this.imageIcon = R.drawable.def_on;
        this.unit = "%";
        this.rangeMin = 0;
        this.rangeMax = 100;
        this.imageValueIcon = new int[]{R.drawable.value10, R.drawable.value20, R.drawable.value30, R.drawable.value40, R.drawable.value50,
                R.drawable.value60, R.drawable.value70, R.drawable.value80, R.drawable.value90, R.drawable.value100};
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

    public int getImageValue(float value) {
        if (value <= 10) {
            return this.imageValueIcon[0];
        } else if (value > 10 && value <= 20) {
            return this.imageValueIcon[0];
        } else if (value > 20 && value <= 30) {
            return this.imageValueIcon[1];
        } else if (value > 30 && value <= 40) {
            return this.imageValueIcon[2];
        } else if (value > 40 && value <= 50) {
            return this.imageValueIcon[3];
        } else if (value > 50 && value <= 60) {
            return this.imageValueIcon[4];
        } else if (value > 60 && value <= 70) {
            return this.imageValueIcon[5];
        } else if (value > 70 && value <= 80) {
            return this.imageValueIcon[6];
        } else if (value > 80 && value <= 90) {
            return this.imageValueIcon[7];
        } else if (value > 90 && value < 100) {
            return this.imageValueIcon[8];
        } else if (value == 100) {
            return this.imageValueIcon[9];
        }
        return this.imageIcon;
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

