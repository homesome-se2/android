package comtest.example.android_team;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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


/* This works with the first page in the application. When we login we send request to the public server with Appmanager getInstance () */

public class FirstFragment extends Fragment implements UpdateResponse {

    private static final String TAG = "Info";
    private NavController navController;
    private Button login_btn;
    private EditText logAcc, logPass;
    private static int defaultStatusBarColor;

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
        defaultStatusBarColor = getActivity().getWindow().getStatusBarColor();
        getActivity().getWindow().setStatusBarColor(Color.WHITE);
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

        if (checkInputOkay(username, password)) {
            AppManager.getInstance().establishConnection();
            AppManager.getInstance().appInFocus = true;
            String logIn = "101::" + username + "::" + password + "";
            AppManager.getInstance().requestToServer(logIn);
        }
    }

    private boolean checkInputOkay(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Username or Password missing!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void update(int indexProtocol, String message, int gadgetID) {

        switch (indexProtocol) {

            case 102:
                ReadWriteCache readWriteCache = new ReadWriteCache(getContext());
                navController.navigate(R.id.SecondFragment);
                getActivity().getWindow().setStatusBarColor(defaultStatusBarColor);
                readWriteCache.writeToCache(message);
                break;
            case 107:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            case 901:
            case 903:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment: In the onStart() event");
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

}