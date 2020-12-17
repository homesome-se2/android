package comtest.example.android_team;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import comtest.example.android_team.background.WorkerSendLocation;
import comtest.example.android_team.models.MultiViewTypeAdapter;
import comtest.example.android_team.models.ReadWriteCache;
import comtest.example.android_team.models.CardModel;
import comtest.example.android_team.models.gadgets.Gadget;
import comtest.example.android_team.models.gadgets.GadgetType;
import comtest.example.android_team.voiceSystem.TTS;

public class SecondFragment extends Fragment implements UpdateResponse {
    private static final String TAG = "Info";
    private ArrayList<CardModel> gadgetCards;
    private RecyclerView recyclerView;
    private MultiViewTypeAdapter multiViewTypeAdapter;
    private Button btnLogOut, btnSpeech, BetaBtn_work, BetaBtn_killWork, btnMuteSound, btn_logOutAllDev;
    private NavController navController;
    private TTS tts;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private DrawerLayout drawer;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        drawer = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {

                    case R.id.start_worker:
                        Toast.makeText(getContext(), "Start Work", Toast.LENGTH_SHORT).show();
                        backgroundWorkTask();
                        break;
                    case R.id.kill_work:
                        Toast.makeText(getContext(), "Kill Work", Toast.LENGTH_SHORT).show();
                        WorkManager.getInstance(getContext()).cancelUniqueWork("sendGPSPos");
                        WorkManager.getInstance(getContext()).cancelAllWorkByTag("sendGPSPos");
                        boolean str = WorkManager.getInstance(getContext()).getWorkInfosForUniqueWork("sendGPSPos")
                                .isDone();
                        Log.i(TAG, "Kill Work: " + str);
                        break;
                    case R.id.speak:
                        Toast.makeText(getContext(), "Voice Control", Toast.LENGTH_SHORT).show();
                        speak();
                        break;
                    case R.id.soundOff:
                        Toast.makeText(getContext(), "Sound off", Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.logOut:
                        Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        logOut();
                        break;
                    case R.id.logOutAllDevices:
                        Toast.makeText(getContext(), "All devices logged out", Toast.LENGTH_SHORT).show();
                        logOutAllDevices();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        Log.d(TAG, "In the SecondFragment");
        AppManager.getInstance().currentFragment = this;
        gadgetCards = new ArrayList<>();
        recyclerView = view.findViewById(R.id.gadgetListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        return view;
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello! Say something :-)");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String speechInput = result.get(0).toLowerCase();
                Toast.makeText(getContext(), speechInput, Toast.LENGTH_LONG).show();

                for (Map.Entry<Integer, Gadget> entry : AppManager.getInstance().getGadgets().entrySet()) {

                    String gadgetResult = entry.getValue().getGadgetName().toLowerCase();
                    Log.i(TAG, gadgetResult);

                    if ((speechInput.contains(gadgetResult))) {
                        GadgetType type = entry.getValue().getType();

                        switch (type) {
                            case SWITCH:
                                if (speechInput.contains("on")) {
                                    String logString = "311::" + entry.getValue().getId() + "::1";
                                    Log.i(TAG, logString);
                                    AppManager.getInstance().requestToServer("311::" + entry.getValue().getId() + "::1");
                                } else if (speechInput.contains("off")) {
                                    AppManager.getInstance().requestToServer("311::" + entry.getValue().getId() + "::0");
                                } else {
                                    String wrongSentence = "You have to be specific, ON or OFF.";
                                    Toast.makeText(getContext(), wrongSentence, Toast.LENGTH_LONG).show();
                                    tts.textToSpeak(wrongSentence);
                                }

                                break;
                            case SET_VALUE:
                                float f = Float.parseFloat(speechInput.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
                                String s = String.valueOf(f);
                                AppManager.getInstance().requestToServer("311::" + entry.getValue().getId() + "::" + f);
                                Log.i(TAG, "311::" + entry.getValue().getId() + "::" + f);
                        }
                        break;

                    }
                }
            }
        }
    }


    private void logOut() {
        AppManager.getInstance().requestToServer("105");
        ReadWriteCache readWriteCache = new ReadWriteCache(getContext());
        readWriteCache.deleteCacheFile();
        navController.navigate(R.id.FirstFragment);
    }

    private void logOutAllDevices() {
        AppManager.getInstance().requestToServer("106");
        ReadWriteCache readWriteCache = new ReadWriteCache(getContext());
        readWriteCache.deleteCacheFile();
        navController.navigate(R.id.FirstFragment);
    }


    @Override
    public void update(int indexProtocol, String message, Integer gadgetID) {

        switch (indexProtocol) {
            case 304:
                for (Map.Entry<Integer, Gadget> entry : AppManager.getInstance().getGadgets().entrySet()) {
                    switch (entry.getValue().getType()) {

                        case SWITCH:
                            gadgetCards.add(new CardModel(CardModel.SWITCH_CARD));
                            break;
                        case BINARY_SENSOR:
                            gadgetCards.add(new CardModel(CardModel.BINARY_SENSOR_CARD));
                            break;
                        case SENSOR:
                            gadgetCards.add(new CardModel(CardModel.SENSOR_CARD));
                            break;
                        case SET_VALUE:
                            gadgetCards.add(new CardModel(CardModel.SET_VALUE_CARD));
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
                multiViewTypeAdapter.notifyItemChanged(gadgetID);
                break;
            case 352:
                switch (AppManager.getInstance().getGadgets().get(gadgetID).getType()) {
                    case SWITCH:
                        gadgetCards.add(new CardModel(CardModel.SWITCH_CARD));
                        break;
                    case BINARY_SENSOR:
                        gadgetCards.add(new CardModel(CardModel.BINARY_SENSOR_CARD));
                        break;
                    case SENSOR:
                        gadgetCards.add(new CardModel(CardModel.SENSOR_CARD));
                        break;
                    case SET_VALUE:
                        gadgetCards.add(new CardModel(CardModel.SET_VALUE_CARD));
                        break;
                }
                multiViewTypeAdapter.notifyItemInserted(gadgetID);
                break;
            case 354:
                AppManager.getInstance().getGadgets().remove(gadgetID);
                multiViewTypeAdapter.notifyItemRemoved(gadgetID);
                break;
            case 404:
                multiViewTypeAdapter.notifyItemChanged(gadgetID);
                break;
        }
    }


    //LATE REQUIREMENTS
    private void backgroundWorkTask() {
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
