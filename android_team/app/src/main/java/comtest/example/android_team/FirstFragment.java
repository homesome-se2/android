package comtest.example.android_team;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private int LOCATION_REQUEST_CODE = 10001;

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

        if (checkInputOkay(username,password)) {
            AppManager.getInstance().establishConnection();
            String logIn = "101::" + username + "::" + password + "";
            AppManager.getInstance().requestToServer(logIn);
        }
    }

    private boolean checkInputOkay(String username, String password){
      if (username.isEmpty() || password.isEmpty()){
          Toast.makeText(getContext(), "Username or Password missing!", Toast.LENGTH_LONG).show();
          return false;
      }else {
          return true;
      }
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


    private void askLocationPermission() {
        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, LOCATION_REQUEST_CODE);
        }
    }

    private boolean checkLocationPermission() {
        int result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        return result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment: In the onStart() event");
        if (checkLocationPermission()) {
            // do something directly
        } else {
            askLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean coarseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean backgroundLocation = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                if (coarseLocation && fineLocation && backgroundLocation) {
                    // Permission granted
                } else {
                    // Permission not granted
                }
            }
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