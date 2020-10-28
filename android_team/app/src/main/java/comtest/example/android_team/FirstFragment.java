package comtest.example.android_team;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import models.TemplateModel;

public class FirstFragment extends Fragment {

    private static final String TAG = "Info";
    private ArrayList<TemplateModel> gadgets;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                AppManager.getInstance().establishConnection();
                AppManager.getInstance().requestToServer("101::Homer::1234");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HomeFragment: In the onDestroyView() event");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "HomeFragment: In the onAttach() event");
    }

    @Override
    public void onResume(){
        super.onResume();
 //       AppManager.getInstance().establishConnection();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "HomeFragment: In the onActivityCreated() event");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment: In the onStart() event");
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "HomeFragment: In the onPause() event");
//        gadgets.clear();
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "HomeFragment: In the onStop() event");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeFragment: In the onDestroy() event");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "HomeFragment: In the onDetach() event");
    }

}