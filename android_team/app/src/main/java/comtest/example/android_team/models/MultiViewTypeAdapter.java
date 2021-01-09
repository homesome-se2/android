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
import android.widget.LinearLayout;
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
import comtest.example.android_team.models.valueModels.BinarySensorValueModel;
import comtest.example.android_team.models.valueModels.SensorValueModel;
import comtest.example.android_team.models.valueModels.SetValueModel;
import comtest.example.android_team.models.valueModels.SwitchValueModel;


public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CardModel> dataSet;
    private Context mContext;
    private static final String TAG = "Info";

    public MultiViewTypeAdapter(Context context, ArrayList<CardModel> data) {
        this.dataSet = data;
        this.mContext = context;
    }


    public static class SwitchTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, unit;
        SwitchCompat switchCompat;
        ImageView gadgetImage;

        public SwitchTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.unit = itemView.findViewById(R.id.textValue);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.switchCompat = itemView.findViewById(R.id.switchLamp);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    public static class BinarySensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state;
        ImageView gadgetImage;
        LinearLayout background;

        public BinarySensorTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
            this.background = itemView.findViewById(R.id.card_background);
        }
    }

    public static class SensorTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView alias, state;
        ImageView gadgetImage;

        public SensorTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    public static class SetValueTemplateViewHolder extends RecyclerView.ViewHolder {
        TextView alias, state,state2;
        SeekBar seekBar;
        ImageView gadgetImage;

        public SetValueTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alias = itemView.findViewById(R.id.text_alias);
            this.state = itemView.findViewById(R.id.text_state);
            this.state2 = itemView.findViewById(R.id.text_state2);
            this.seekBar = itemView.findViewById(R.id.seekBar_state);
            this.gadgetImage = itemView.findViewById(R.id.image_item);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case CardModel.SWITCH_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_card_item, parent, false);
                return new SwitchTemplateViewHolder(view);
            case CardModel.BINARY_SENSOR_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.binary_sensor_card_item, parent, false);
                return new BinarySensorTemplateViewHolder(view);
            case CardModel.SENSOR_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_card_item, parent, false);
                return new SensorTemplateViewHolder(view);
            case CardModel.SET_VALUE_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setvalue_card_item, parent, false);
                return new SetValueTemplateViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //position is item card index
        CardModel object = dataSet.get(position);
        Log.i(TAG, "onBindViewHolder: " + position);
        if (object != null) {
            Gadget gadget = AppManager.getInstance().getListGadgetMapping().get(position);
            switch (object.getType()) {
                case CardModel.SWITCH_CARD:
                    setSwitchDetails(((SwitchTemplateViewHolder) holder), gadget);
                    break;
                case CardModel.BINARY_SENSOR_CARD:
                    setBinarySensorDetails(((BinarySensorTemplateViewHolder) holder), gadget);
                    break;
                case CardModel.SENSOR_CARD:
                    setSensorDetails(((SensorTemplateViewHolder) holder), gadget);
                    break;
                case CardModel.SET_VALUE_CARD:
                    setValueCardDetails(((SetValueTemplateViewHolder) holder), gadget);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).getType()) {
            case 0:
                return CardModel.SWITCH_CARD;
            case 1:
                return CardModel.BINARY_SENSOR_CARD;
            case 2:
                return CardModel.SENSOR_CARD;
            case 3:
                return CardModel.SET_VALUE_CARD;
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
        SwitchValueModel valueTemplate;
        boolean okTemplate = ValueTemplate.getInstance().getSwitchTemplate().containsKey(gadget.getValueTemplate());
        if (okTemplate) {
            valueTemplate = ValueTemplate.getInstance().getSwitchTemplate().get(gadget.getValueTemplate());
        } else {
            valueTemplate = ValueTemplate.getInstance().getSwitchTemplate().get("default");
        }

        holder.alias.setText(gadget.getGadgetName());
        holder.switchCompat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (holder.switchCompat.isChecked()) {
                        String set = "311::" + gadget.getId() + "::0";
                        AppManager.getInstance().requestToServer(set);
                    } else {
                        String set = "311::" + gadget.getId() + "::1";
                        AppManager.getInstance().requestToServer(set);
                    }
                }
                return true;
            }
        });

        if (gadget.getState() == 1) {
            holder.switchCompat.setChecked(true);
            holder.unit.setText(valueTemplate.getValueON());
            holder.gadgetImage.setImageResource(valueTemplate.getImageIconON());
        } else {
            holder.switchCompat.setChecked(false);
            holder.unit.setText(valueTemplate.getValueOFF());
            holder.gadgetImage.setImageResource(valueTemplate.getImageIconOFF());
        }
    }

    private void setBinarySensorDetails(BinarySensorTemplateViewHolder holder, Gadget gadget) {
        BinarySensorValueModel valueTemplate;
        boolean okTemplate = ValueTemplate.getInstance().getBiSensorTemplate().containsKey(gadget.getValueTemplate());
        if (okTemplate) {
            valueTemplate = ValueTemplate.getInstance().getBiSensorTemplate().get(gadget.getValueTemplate());
        } else {
            valueTemplate = ValueTemplate.getInstance().getBiSensorTemplate().get("default");
        }
        holder.alias.setText(gadget.getGadgetName());
        if (gadget.getState() == 1) {
            holder.state.setText(valueTemplate.getValueON());
            if (!gadget.getValueTemplate().equals("person")) {
                holder.background.setBackgroundColor(mContext.getResources().getColor(R.color.alert));
            }
            holder.gadgetImage.setImageResource(valueTemplate.getImageIconON());
        } else {
            holder.state.setText(valueTemplate.getValueOFF());
            holder.gadgetImage.setImageResource(valueTemplate.getImageIconOFF());
            holder.background.setBackgroundColor(Color.WHITE);
        }
    }

    private void setSensorDetails(SensorTemplateViewHolder holder, Gadget gadget) {
        SensorValueModel valueTemplate;
        boolean okTemplate = ValueTemplate.getInstance().getSensorTemplate().containsKey(gadget.getValueTemplate());
        if (okTemplate) {
            valueTemplate = ValueTemplate.getInstance().getSensorTemplate().get(gadget.getValueTemplate());
        } else {
            valueTemplate = ValueTemplate.getInstance().getSensorTemplate().get("default");
        }
        holder.alias.setText(gadget.getGadgetName());
        holder.state.setText(String.format("%s %s", gadget.getState(), valueTemplate.getUnit()));
        holder.gadgetImage.setImageResource(valueTemplate.getImageIcon());
    }

    private void setValueCardDetails(final SetValueTemplateViewHolder holder, final Gadget gadget) {
        SetValueModel valueTemplate;
        boolean okTemplate = ValueTemplate.getInstance().getSetValueHashMap().containsKey(gadget.getValueTemplate());
        if (okTemplate) {
            valueTemplate = ValueTemplate.getInstance().getSetValueHashMap().get(gadget.getValueTemplate());
        } else {
            valueTemplate = ValueTemplate.getInstance().getSetValueHashMap().get("default");
        }
        holder.alias.setText(gadget.getGadgetName());
        holder.state.setText(String.valueOf((int) valueTemplate.getRangeMin()));
        holder.state2.setText(String.valueOf((int) valueTemplate.getRangeMax()));
        holder.gadgetImage.setImageResource(valueTemplate.getImageValue(gadget.getState()));
        holder.seekBar.setMin((int) valueTemplate.getRangeMin());
        holder.seekBar.setMax((int) valueTemplate.getRangeMax());
        holder.seekBar.setProgress((int) gadget.getState());
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String set = "311::" + gadget.getId() + "::" + holder.seekBar.getProgress();
                AppManager.getInstance().requestToServer(set);
            }
        });
    }

}
