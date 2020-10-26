package models;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import comtest.example.android_team.AppManager;
import comtest.example.android_team.R;
import models.gadgets.Gadget_basic;

public class Adapters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<TemplateModel> gadgetList;
    private Context mContext;
    private static final String TAG = "Info >> ";

    public Adapters(ArrayList<TemplateModel> gadgetList, Context mContext) {
        this.gadgetList = gadgetList;
        this.mContext = mContext;
    }

    public static class SwitchTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView aliasGadget, template;
        RelativeLayout background;
        SwitchCompat switchLamp;

        public SwitchTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.aliasGadget = aliasGadget;
            this.template = template;
            this.background = background;
            this.switchLamp = switchLamp;

            // Lägg till ID senare på våra gadgets INTE KLAR DENNA!
        }
    }

    public static class BinarySensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state, template;
        RelativeLayout background;

        public BinarySensorTemplateViewHolder(@NonNull View itemView, TextView alias, TextView state, TextView template, RelativeLayout background) {
            super(itemView);
            this.alias = alias;
            this.state = state;
            this.template = template;
            this.background = background;

            // Lägg till ID senare på våra gadgets INTE KLAR DENNA!
        }
    }

    public static class SensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state, template;
        RelativeLayout background;

        public SensorTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = alias;
            this.state = state;
            this.template = template;
            this.background = background;
        }

        // Lägg till ID senare på våra gadgets INTE KLAR DENNA!
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {

            case TemplateModel.SWITCH_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_card, parent, false);
                Log.i(TAG, "SwitchTemplateViewHolder view created.");
                return new SwitchTemplateViewHolder(view);
            case TemplateModel.BINARY_SENSOR_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.binary_sensor_card, parent, false);
                Log.i(TAG, "BinarySensorTemplateViewHolder view created.");
                return new SensorTemplateViewHolder(view);
            case TemplateModel.SENSOR_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_card, parent, false);
                Log.i(TAG, "BinarySensorTemplateViewHolder view created.");
                return new SensorTemplateViewHolder(view);

            case TemplateModel.SET_VALUE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_value_card, parent, false);
                Log.i(TAG, "SensorTemplateViewHolder view created.");
                return new SensorTemplateViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        TemplateModel object = gadgetList.get(position);
        ArrayList<Gadget_basic> valueList = new ArrayList<>(AppManager.getInstance().getGadgets().values());
        Gadget_basic gadget = valueList.get(position);

        if (object != null) {

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


            }
        }

    }


    @Override
    public int getItemViewType(int position) {

        switch (gadgetList.get(position).getType()) {
            case 0:
                return TemplateModel.SWITCH_CARD;
            case 1:
                return TemplateModel.BINARY_SENSOR_CARD;
            case 2:
                return TemplateModel.SENSOR_CARD;

            case 3:
                return TemplateModel.SET_VALUE;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private void setSwitchDetails(SwitchTemplateViewHolder gadget, Gadget_basic gadget_basic) {
        gadget.template.setText(gadget_basic.valueTemplate);
        gadget.aliasGadget.setText(gadget_basic.gadgetName);
        if (gadget_basic.getState() == 1) {
            gadget.switchLamp.setChecked(true);
        } else {
            gadget.switchLamp.setChecked(false);
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
    }

}
