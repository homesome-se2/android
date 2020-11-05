package comtest.example.android_team.models.gadgets;

public class Gadget_basic extends Gadget {

    public Gadget_basic(int gadgetID, String gadgetName, GadgetType type, String valueTemplate, float state) {
        super(gadgetID, gadgetName, type, valueTemplate, state);
    }


    @Override
    public void alterState(float requestedState) throws Exception {

    }

    @Override
    protected String sendCommand(String command) throws Exception {
        return null;
    }


}





