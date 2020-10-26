package models.gadgets;

public abstract class Gadget {

    public int id;
    public String gadgetName;
    public GadgetType gadgetType;
    public float state;
    public String valueTemplate;
    public long pollDelaySec;
    public boolean isPresent;

    public Gadget(int id, String gadgetName, GadgetType gadgetType, float state, String valueTemplate, long pollDelaySec, boolean isPresent) {
        this.id = id;
        this.gadgetName = gadgetName;
        this.gadgetType = gadgetType;
        this.state = state;
        this.valueTemplate = valueTemplate;
        this.pollDelaySec = pollDelaySec;
        this.isPresent = isPresent;
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
