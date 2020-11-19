package comtest.example.android_team.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import comtest.example.android_team.AppManager;
import comtest.example.android_team.R;
import comtest.example.android_team.models.gadgets.Gadget_basic;


public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<TemplateModel> dataSet;
    private Context mContext;
    private static final String TAG = "Info";

    public MultiViewTypeAdapter(Context context, ArrayList<TemplateModel> data) {
        this.dataSet = data;
        this.mContext = context;
    }


    public static class SwitchTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, template;
        RelativeLayout background;
        SwitchCompat switchLamp;
        ImageView gadgetImage;

        public SwitchTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.switchLamp = itemView.findViewById(R.id.switchLamp);
            this.template = itemView.findViewById(R.id.text_template);
            this.background = itemView.findViewById(R.id.card_background);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    public static class BinarySensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state, template;
        RelativeLayout background;

        public BinarySensorTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.template = itemView.findViewById(R.id.text_template);
            this.background = itemView.findViewById(R.id.card_background);
        }
    }

    public static class SensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state, template;
        RelativeLayout background;
        ImageView gadgetImage;

        public SensorTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.template = itemView.findViewById(R.id.text_template);
            this.background = itemView.findViewById(R.id.card_background);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    public static class SetValueTemplateViewHolder extends RecyclerView.ViewHolder{
        TextView alias, state, template;
        RelativeLayout background;

        public SetValueTemplateViewHolder(@NonNull View itemView){
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.template = itemView.findViewById(R.id.text_template);
            this.background = itemView.findViewById(R.id.card_background);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TemplateModel.SWITCH_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_card_item, parent, false);
                return new SwitchTemplateViewHolder(view);
            case TemplateModel.BINARY_SENSOR_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.binary_sensor_card_item, parent, false);
                return new BinarySensorTemplateViewHolder(view);
            case TemplateModel.SENSOR_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_card_item, parent, false);
                return new SensorTemplateViewHolder(view);
            case TemplateModel.SET_VALUE_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setvalue_card_item, parent, false);
                return new SetValueTemplateViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //position is item card index
        TemplateModel object = dataSet.get(position);
        ArrayList<Gadget_basic> valuesList = new ArrayList<>(AppManager.getInstance().getGadgets().values());
        Gadget_basic gadget = valuesList.get(position);
        if (object != null) {
            Log.d(TAG, gadget.toString());
            switch (object.getType()) {
                case TemplateModel.SWITCH_CARD:
                    setSwitchDetails(((SwitchTemplateViewHolder) holder), gadget);
                    break;
                case TemplateModel.BINARY_SENSOR_CARD:
                    setBinarySensorDetails(((BinarySensorTemplateViewHolder) holder), gadget);
                    break;
                case TemplateModel.SENSOR_CARD:
                    setSensorDetails(((SensorTemplateViewHolder) holder), gadget);
                    break;
                case TemplateModel.SET_VALUE_CARD:
                    setValueCardDetails(((SetValueTemplateViewHolder) holder), gadget);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).getType()) {
            case 0:
                return TemplateModel.SWITCH_CARD;
            case 1:
                return TemplateModel.BINARY_SENSOR_CARD;
            case 2:
                return TemplateModel.SENSOR_CARD;
            case 3:
                return TemplateModel.SET_VALUE_CARD;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setSwitchDetails(final SwitchTemplateViewHolder gadget, final Gadget_basic gadget_basic) {
        gadget.template.setText(gadget_basic.valueTemplate);
        gadget.alias.setText(gadget_basic.gadgetName);

        gadget.switchLamp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (gadget.switchLamp.isChecked()) {
                        String set = "311::"+gadget_basic.id+"::0";
                        AppManager.getInstance().requestToServer(set);
                    } else {
                        String set = "311::"+gadget_basic.id+"::1";
                        AppManager.getInstance().requestToServer(set);
                    }
                }
                return true;
            }
        });

        if (gadget_basic.getState() == 1) {
            gadget.switchLamp.setChecked(true);
            gadget.gadgetImage.setImageResource(AppManager.getInstance().getValueTemplate().getSwitchTemplate().get(gadget_basic.valueTemplate).getImageIconON());
        } else {
            gadget.switchLamp.setChecked(false);
            gadget.gadgetImage.setImageResource(AppManager.getInstance().getValueTemplate().getSwitchTemplate().get(gadget_basic.valueTemplate).getImageIconOFF());
        }

    }


    private void setBinarySensorDetails(BinarySensorTemplateViewHolder gadget, Gadget_basic gadget_basic) {
        gadget.template.setText(gadget_basic.valueTemplate);
        gadget.alias.setText(gadget_basic.gadgetName);
        gadget.state.setText(String.valueOf(gadget_basic.getState()));
        gadget.background.setBackgroundColor(Color.RED);
    }

    private void setSensorDetails(SensorTemplateViewHolder gadget, Gadget_basic gadget_basic) {
        gadget.template.setText(gadget_basic.valueTemplate);
        gadget.alias.setText(gadget_basic.gadgetName);
        gadget.state.setText(String.valueOf(gadget_basic.getState()));
        gadget.background.setBackgroundColor(Color.GRAY);
        gadget.gadgetImage.setImageResource(AppManager.getInstance().getValueTemplate().getSensorTemplate().get(gadget_basic.valueTemplate).getImageIcon());
    }
    private void setValueCardDetails(SetValueTemplateViewHolder gadget, Gadget_basic gadget_basic){
        gadget.template.setText(gadget_basic.valueTemplate);
        gadget.alias.setText(gadget_basic.gadgetName);
        gadget.state.setText(String.valueOf(gadget_basic.getState()));
        gadget.background.setBackgroundColor(Color.GREEN);
    }

}
