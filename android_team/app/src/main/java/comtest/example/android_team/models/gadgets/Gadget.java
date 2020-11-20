package comtest.example.android_team.models.gadgets;

public class Gadget {

    /* Here we define our parameters for a gadget. Then we add a constructor to the parameters. Method setState is there to make the system to dont bother
     what gadget that is toggled. It is independent in that way and only checks for the update */

    private int id;
    private String gadgetName;
    private GadgetType type;
    private float state;
    private String valueTemplate;

    public Gadget(GadgetBuilder gadgetBuilder) {
        this.id = gadgetBuilder.id;
        this.gadgetName = gadgetBuilder.gadgetName;
        this.type = gadgetBuilder.type;
        this.valueTemplate = gadgetBuilder.valueTemplate;
        this.state = gadgetBuilder.state;
    }

    public int getId() {
        return id;
    }

    public String getGadgetName() {
        return gadgetName;
    }

    public GadgetType getType() {
        return type;
    }

    public String getValueTemplate() {
        return valueTemplate;
    }

    public float getState() {
        return state;
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

    @Override
    public String toString() {
        return "Gadget{" +
                "id=" + id +
                ", alias='" + gadgetName + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", valueTemplate='" + valueTemplate + '\'' +
                '}';
    }

    public static class GadgetBuilder {
        private int id;
        private String gadgetName;
        private GadgetType type;
        private float state;
        private String valueTemplate;

        public GadgetBuilder id(final int id){
            this.id = id;
            return this;
        }

        public GadgetBuilder gadgetName(final String gadgetName){
            this.gadgetName =  gadgetName;
            return this;
        }

        public GadgetBuilder type(final GadgetType type){
            this.type = type;
            return this;
        }

        public GadgetBuilder state(final float state){
            this.state = state;
            return this;
        }

        public GadgetBuilder valueTemplate(final String valueTemplate){
            this.valueTemplate = valueTemplate;
            return this;
        }

        public Gadget build(){
            return new Gadget(this);
        }


    }

}
