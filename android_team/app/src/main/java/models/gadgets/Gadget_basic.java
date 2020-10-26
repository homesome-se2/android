package models.gadgets;

public class Gadget_basic extends Gadget {

    public Gadget_basic(int id, String gadgetName, GadgetType gadgetType, float state, String valueTemplate, long pollDelaySec, boolean isPresent) {
        super(id, gadgetName, gadgetType, state, valueTemplate, pollDelaySec, isPresent);
    }

    public void poll() {
    }

    public void alterState(float requestedState) throws Exception {
    }

    protected String sendCommand(String command) throws Exception {

        return null;
    }


}





