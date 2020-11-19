package comtest.example.android_team;
import android.content.Intent;

import android.os.Bundle;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import comtest.example.android_team.background.WorkerSendLocation;
import comtest.example.android_team.models.MultiViewTypeAdapter;
import comtest.example.android_team.models.ReadWriteCache;
import comtest.example.android_team.models.TemplateModel;
import comtest.example.android_team.models.gadgets.Gadget_basic;
import comtest.example.android_team.voiceSystem.TTS;


public class SecondFragment extends Fragment implements UpdateResponse {
    private static final String TAG = "Info";
    private ArrayList<TemplateModel> gadgetCards;
    private RecyclerView recyclerView;
    private MultiViewTypeAdapter multiViewTypeAdapter;
    private Button btnLogOut, btnSpeech, BetaBtn_work, BetaBtn_killWork;
    private NavController navController;
    private TTS tts;
    private AppManager appManager = new AppManager();
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        Log.d(TAG, "In the SecondFragment");
        AppManager.getInstance().currentFragment = this;
        gadgetCards = new ArrayList<>();
        recyclerView = view.findViewById(R.id.gadgetListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        initbtnLogOut(view);
        btnSpeech(view);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });


// *************************************************************************************
        BetaBtn_work = view.findViewById(R.id.btn_worker);
        BetaBtn_killWork = view.findViewById(R.id.btn_killWorker);

        BetaBtn_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          //      backgroundWorkTask();
                AppManager.getInstance().requestToServer("311::6::45.8");
            }
        });

        BetaBtn_killWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                WorkManager.getInstance(getContext()).cancelUniqueWork("sendGPSPos");
//                WorkManager.getInstance(getContext()).cancelAllWorkByTag("sendGPSPos");
//              boolean str =  WorkManager.getInstance(getContext()).getWorkInfosForUniqueWork("sendGPSPos")
//                        .isDone();
//                Log.i(TAG, "Kill Work: " + str);

            }
        });
// *****************************************************************************************
        // Voice to text
        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();

            }
        });

        return view;
    }

    private void initbtnLogOut(View view) {
        btnLogOut = view.findViewById(R.id.btn_logOut);
    }

    private void btnSpeech(View view) {
        btnSpeech = view.findViewById(R.id.btnSpeech);
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello! Say something :-)");

        try {
            // Compare with a string like turn on the lamp
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            System.out.println(e.getMessage());;
        }
    }

        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode){
                case REQUEST_CODE_SPEECH_INPUT:{
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    for (int i = 0; i < appManager.getGadgets().size(); i++) {


                        if (Objects.requireNonNull(appManager.getGadgets().get(i)).gadgetName.contains(result.get(0))) {

                            AppManager.getInstance().requestToServer("311::" + Objects.requireNonNull(appManager.getGadgets().get(i)).id + "::1");
                        }
                    }
                    break;
                }
            }
        }


    private void logOut() {
        AppManager.getInstance().requestToServer("105");
        ReadWriteCache readWriteCache = new ReadWriteCache(getContext());
        readWriteCache.deleteCacheFile();
        navController.navigate(R.id.FirstFragment);
    }


    @Override
    public void update(int indexProtocol, String message) {

        switch (indexProtocol) {
            case 304:
                for (Map.Entry<Integer, Gadget_basic> entry : AppManager.getInstance().getGadgets().entrySet()) {
                    switch (entry.getValue().type) {

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
                            gadgetCards.add(new TemplateModel(TemplateModel.SET_VALUE_CARD));
                            break;
                    }
                }
                multiViewTypeAdapter = new MultiViewTypeAdapter(getContext(), gadgetCards);
                recyclerView.setAdapter(multiViewTypeAdapter);
                multiViewTypeAdapter.notifyDataSetChanged();
                break;
            case 316:
                Log.i(TAG, message);
        //        tts.textToSpeak(message);
                multiViewTypeAdapter.notifyDataSetChanged();
                break;
        }
    }


    //LATE REQUIREMENTS
    private void backgroundWorkTask(){
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

//        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(WorkerTest.class,2, TimeUnit.MINUTES)
//                .setConstraints(constraints)
//                .addTag("sendGPSPos")
//                .build();
//        WorkManager.getInstance(getContext()).enqueue(workRequest);


        OneTimeWorkRequest refreshWork = new OneTimeWorkRequest.Builder(WorkerSendLocation.class)
                .setConstraints(constraints)
                .addTag("sendGPSPos")
                .build();
        WorkManager.getInstance(getContext()).enqueueUniqueWork("sendGPSPos", ExistingWorkPolicy.KEEP, refreshWork);
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
        tts = new TTS(getContext());
        tts.initTTS();

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
        tts.stopTTS();
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
