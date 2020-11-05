package comtest.example.android_team.models;

public class TemplateModel {

    public static final int SWITCH_CARD = 0;
    public static final int BINARY_SENSOR_CARD = 1;
    public static final int SENSOR_CARD = 2;
    public static final int SET_VALUE = 3;

    private int type;

public TemplateModel(int type){
        this.type = type;

}

public int getType(){
    return type;
}

}
