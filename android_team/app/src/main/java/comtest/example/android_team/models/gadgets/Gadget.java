package comtest.example.android_team.models.gadgets;

public abstract class Gadget {

    private int id;
    private String gadgetName;
    private GadgetType gadgetType;
    private float state;
    private String valueTemplate;
    private long pollDelaySec;
    private boolean isPresent;

    public Gadget(int id, String gadgetName, GadgetType gadgetType, float state, String valueTemplate, long pollDelaySec, boolean isPresent) {
        this.id = id;
        this.gadgetName = gadgetName;
        this.gadgetType = gadgetType;
        this.state = state;
        this.valueTemplate = valueTemplate;
        this.pollDelaySec = pollDelaySec;
        isPresent = false;
    }

    public void setState(float state) {
        this.state = state;
    }

    public float getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Gadget{" +
                "id=" + id +
                ", gadgetName='" + gadgetName + '\'' +
                ", gadgetType=" + gadgetType +
                ", state=" + state +
                ", valueTemplate='" + valueTemplate + '\'' +
                ", pollDelaySec=" + pollDelaySec +
                ", isPresent=" + isPresent +
                '}';
    }
}
