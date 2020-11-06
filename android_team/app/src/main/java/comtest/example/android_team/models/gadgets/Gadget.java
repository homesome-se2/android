package comtest.example.android_team.models.gadgets;

public abstract class Gadget {

    /* Here we define our parameters for a gadget. Then we add a constructor to the parameters. Method setState is there to make the system to dont bother
     what gadget that is toggled. It is indedendent in that way and only checks for the update */

    public final int id;
    public final String gadgetName;
    public final GadgetType type;
    private float state;
    public String valueTemplate;
    public boolean isPresent;


    public Gadget(int gadgetID, String gadgetName, GadgetType type, String valueTemplate, float state) {
        this.id = gadgetID;
        this.gadgetName = gadgetName;
        this.type = type;
        this.valueTemplate = valueTemplate;
        this.state = state;
        isPresent = false;
    }


    // Request gadget to alter state
    public abstract void alterState(float requestedState) throws Exception;

    // Communications specifics for sending request to a gadget
    protected abstract String sendCommand(String command) throws Exception;

    // Set instance variable 'state' to match actual state (called when a gadget has reported a state change)
    public void setState(float newState) {
        boolean isBinaryGadget = (type == GadgetType.SWITCH || type == GadgetType.BINARY_SENSOR);
        if (isBinaryGadget) {
            state = (newState == 1 ? 1 : 0);
        } else {
            state = newState;
        }
    }

    public float getState() {
        return state;
    }

    // Translate gadget according to HoSo protocol. Gadget object -> HoSo protocol
    public String toHoSoProtocol() {
        return String.format("%s::%s::%s::%s::%s::%s", id, gadgetName, type, valueTemplate, state,0);
    }

    @Override
    public String toString() {
        return "Gadget{" +
                "id=" + id +
                ", alias='" + gadgetName + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", valueTemplate='" + valueTemplate + '\'' +
                ", isPresent=" + isPresent +
                '}';
    }
}
