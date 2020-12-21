package comtest.example.android_team.models;

public class CardModel {

    /* Used to be call upon to retrieve all the cards */

    public static final int SWITCH_CARD = 0;
    public static final int BINARY_SENSOR_CARD = 1;
    public static final int SENSOR_CARD = 2;
    public static final int SET_VALUE_CARD = 3;

    private int type;

public CardModel(int type){
        this.type = type;

}

    @Override
    public String toString() {
        return "CardModel{" +
                "type=" + type +
                '}';
    }

    public int getType(){
    return type;
}

}
