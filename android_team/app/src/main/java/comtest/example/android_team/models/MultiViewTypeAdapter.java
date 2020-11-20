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
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import comtest.example.android_team.AppManager;
import comtest.example.android_team.R;
import comtest.example.android_team.models.gadgets.Gadget;


public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<TemplateModel> dataSet;
    private Context mContext;
    private static final String TAG = "Info";

    public MultiViewTypeAdapter(Context context, ArrayList<TemplateModel> data) {
        this.dataSet = data;
        this.mContext = context;
    }


    public static class SwitchTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias;
        RelativeLayout background;
        SwitchCompat switchCompat;
        ImageView gadgetImage;

        public SwitchTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.switchCompat = itemView.findViewById(R.id.switchLamp);
            this.background = itemView.findViewById(R.id.card_background);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    public static class BinarySensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state;
        RelativeLayout background;
        ImageView gadgetImage;

        public BinarySensorTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.background = itemView.findViewById(R.id.card_background);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    public static class SensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state;
        RelativeLayout background;
        ImageView gadgetImage;

        public SensorTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.background = itemView.findViewById(R.id.card_background);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    public static class SetValueTemplateViewHolder extends RecyclerView.ViewHolder{
        TextView alias, state;
        RelativeLayout background;
        SeekBar seekBar;
        ImageView gadgetImage;

        public SetValueTemplateViewHolder(@NonNull View itemView){
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.background = itemView.findViewById(R.id.card_background);
            this.seekBar = itemView.findViewById(R.id.seekBar_state);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
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
        ArrayList<Gadget> valuesList = new ArrayList<>(AppManager.getInstance().getGadgets().values());
        Gadget gadget = valuesList.get(position);
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
    private void setSwitchDetails(final SwitchTemplateViewHolder holder, final Gadget gadget) {
        holder.alias.setText(gadget.gadgetName);

        holder.switchCompat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (holder.switchCompat.isChecked()) {
                        String set = "311::"+gadget.id+"::0";
                        AppManager.getInstance().requestToServer(set);
                    } else {
                        String set = "311::"+gadget.id+"::1";
                        AppManager.getInstance().requestToServer(set);
                    }
                }
                return true;
            }
        });

        if (gadget.getState() == 1) {
            holder.switchCompat.setChecked(true);
            holder.gadgetImage.setImageResource(ValueTemplate.getInstance().getSwitchTemplate().get(gadget.valueTemplate).getImageIconON());
        } else {
            holder.switchCompat.setChecked(false);
            holder.gadgetImage.setImageResource(ValueTemplate.getInstance().getSwitchTemplate().get(gadget.valueTemplate).getImageIconOFF());
        }

    }


    private void setBinarySensorDetails(BinarySensorTemplateViewHolder holder, Gadget gadget) {
        holder.alias.setText(gadget.gadgetName);
        holder.background.setBackgroundColor(Color.RED);
        if (gadget.getState() == 1) {
            holder.state.setText(ValueTemplate.getInstance().getBiSensorTemplate().get(gadget.valueTemplate).getValueON());
            holder.gadgetImage.setImageResource(ValueTemplate.getInstance().getBiSensorTemplate().get(gadget.valueTemplate).getImageIconON());
        } else {
            holder.state.setText(ValueTemplate.getInstance().getBiSensorTemplate().get(gadget.valueTemplate).getValueOFF());
            holder.gadgetImage.setImageResource(ValueTemplate.getInstance().getBiSensorTemplate().get(gadget.valueTemplate).getImageIconOFF());
        }
    }

    private void setSensorDetails(SensorTemplateViewHolder holder, Gadget gadget) {
        holder.alias.setText(gadget.gadgetName);
        holder.state.setText(String.valueOf(gadget.getState()));
        holder.background.setBackgroundColor(Color.DKGRAY);
        holder.gadgetImage.setImageResource(ValueTemplate.getInstance().getSensorTemplate().get(gadget.valueTemplate).getImageIcon());
    }
    //TODO add seakbar to show floating value
    private void setValueCardDetails(SetValueTemplateViewHolder holder, Gadget gadget){
        holder.alias.setText(gadget.gadgetName);
        holder.state.setText(String.valueOf(gadget.getState()));
        holder.background.setBackgroundColor(Color.MAGENTA);
        holder.gadgetImage.setImageResource(ValueTemplate.getInstance().getSetValueHashMap().get(gadget.valueTemplate).getImageIcon());
    }

}
