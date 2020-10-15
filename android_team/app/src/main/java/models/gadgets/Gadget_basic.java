package models.gadgets;

public class Gadget_basic extends Gadget {

    public Gadget_basic(int gadgetID, String gadgetName, GadgetType gadgetType, float state, String valueTemplate, long pollDelaySec) {

        super(gadgetID, gadgetName, gadgetType, state, valueTemplate, pollDelaySec, false);
    }

    public void poll() {
    }

    public void alterState(float requestedState) throws Exception {
    }

    protected String sendCommand(String command) throws Exception {

        return null;
    }


}





