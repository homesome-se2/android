package comtest.example.android_team;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import comtest.example.android_team.models.Adapters;
import comtest.example.android_team.models.MultiViewTypeAdapter;
import comtest.example.android_team.models.TemplateModel;
import comtest.example.android_team.models.gadgets.Gadget_basic;

public class SecondFragment extends Fragment implements UpdateResponse {

    private static final String TAG = "Info";
    private ArrayList<TemplateModel> gadgetCards;
    private RecyclerView recyclerView;
    private Adapters adapters;
    private MultiViewTypeAdapter multiViewTypeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        Log.d(TAG, "In the SecondFragment");
        AppManager.getInstance().currentFragment = this;
        gadgetCards = new ArrayList<>();
        recyclerView = view.findViewById(R.id.gadgetListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return view;
    }

    @Override
    public void update(int indexProtocol, String message) {

        switch (indexProtocol) {
            case 304:

                for(Map.Entry<Integer, Gadget_basic> entry : AppManager.getInstance().getGadgets().entrySet()) {
                    switch (entry.getValue().type){
                        case SWITCH:
                            gadgetCards.add(new TemplateModel(TemplateModel.SWITCH_CARD));
                            break;
                        case BINARY_SENSOR:
                            gadgetCards.add(new TemplateModel(TemplateModel.BINARY_SENSOR_CARD));
                            break;
                        case SENSOR:
                            gadgetCards.add(new TemplateModel(TemplateModel.SENSOR_CARD));
                            break;
                        case SET_VALUE:

                            break;
                    }
                }
                multiViewTypeAdapter = new MultiViewTypeAdapter(getContext(),gadgetCards);
                recyclerView.setAdapter(multiViewTypeAdapter);
                multiViewTypeAdapter.notifyDataSetChanged();
                break;

//                for (Map.Entry<Integer, Gadget_basic> entry : AppManager.getInstance().getGadgets().entrySet()) {
//                    Log.i(TAG, entry.toString());
//                    switch (entry.getValue().type) {
//                        case SWITCH:
//                            gadgetCards.add(new TemplateModel(TemplateModel.SWITCH_CARD));
//                            break;
//                        case BINARY_SENSOR:
//                            gadgetCards.add(new TemplateModel(TemplateModel.BINARY_SENSOR_CARD));
//                            break;
//                        case SENSOR:
//                            gadgetCards.add(new TemplateModel(TemplateModel.SENSOR_CARD));
//                            break;
//                        case SET_VALUE:
//                            gadgetCards.add(new TemplateModel(TemplateModel.SET_VALUE_CARD));
//                            break;
//                    }
//                }
//                adapters = new Adapters(gadgetCards, getContext());
//                recyclerView.setAdapter(adapters);
//                adapters.notifyDataSetChanged();
//                break;

            case 316:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
               //    adapters.notifyDataSetChanged();
                multiViewTypeAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HomeFragment: In the OnCreate event()");
        // This callback will only be called when Fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "SecondFragment: In the onStartView() event");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "SecondFragment: In the onResumeView() event");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SecondFragment: In the onPauseView() event");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "SecondFragment: In the onStopView() event");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SecondFragment: In the onDestroyView() event");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "SecondFragment: In the onDetachView() event");
    }

}
