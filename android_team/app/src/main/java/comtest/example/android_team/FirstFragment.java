package comtest.example.android_team;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import comtest.example.android_team.models.ReadWriteCache;


public class FirstFragment extends Fragment implements UpdateResponse {

    private static final String TAG = "Info";
    private NavController navController;
    private Button login_btn;
    private EditText logAcc, logPass;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        AppManager.getInstance().currentFragment = this;
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });


    }


    private void init(View view) {
        login_btn = view.findViewById(R.id.btnLogin);
        logAcc = view.findViewById(R.id.username);
        logPass = view.findViewById(R.id.passwordField);
    }

    private void logIn() {
        String username = logAcc.getText().toString().trim();
        String password = logPass.getText().toString().trim();

        AppManager.getInstance().establishConnection();
        AppManager.getInstance().requestToServer("101::Homer::1234");

    }


    @Override
    public void update(int indexProtocol, String message) {

        switch (indexProtocol) {

            case 102:
                ReadWriteCache readWriteCache = new ReadWriteCache(getContext());
                navController.navigate(R.id.SecondFragment);
                readWriteCache.writeToCache(message);
                break;
            case 901:
            case 903:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
    public void onResume() {
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