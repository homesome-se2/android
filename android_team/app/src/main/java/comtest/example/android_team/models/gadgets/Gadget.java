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
