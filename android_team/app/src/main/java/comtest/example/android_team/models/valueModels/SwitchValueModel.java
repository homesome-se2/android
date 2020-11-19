package comtest.example.android_team.models.valueModels;

import comtest.example.android_team.R;
import comtest.example.android_team.models.ValueTemplate;

public class SwitchValueModel{

    private int[] imageIconList;

    public SwitchValueModel() {
        this.imageIconList =new int[]{R.drawable.switch_on_def, R.drawable.switch_off_def};
    }

    public SwitchValueModel(int switchImgOn, int switchImgOff) {
        this.imageIconList = new int[]{switchImgOn, switchImgOff};;
    }

    public int getImageIconON() {
        return imageIconList[0];
    }

    public int getImageIconOFF() {
        return imageIconList[1];
    }
}
