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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import comtest.example.android_team.models.ReadWriteCache;


public class SetupFragment extends Fragment implements UpdateResponse {

    /* A middle hand that is there when we are setting up new fragments */

    private static final String TAG = "Info";
    private NavController navController;
    private int LOCATION_REQUEST_CODE = 10001;
    private static int defaultStatusBarColor;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        Log.i(TAG, "In the SetupFragment");
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        AppManager.getInstance().currentFragment = this;
        defaultStatusBarColor = getActivity().getWindow().getStatusBarColor();
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        return view;
    }

    @Override
    public void update(int indexProtocol, String message, int gadgetID) {
        switch (indexProtocol) {
            case 104:
                navController.navigate(R.id.SecondFragment);
                getActivity().getWindow().setStatusBarColor(defaultStatusBarColor);
                Log.i(TAG, "Open Second fragment");
                break;
            case 901:
            case 903:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                getActivity().getWindow().setStatusBarColor(defaultStatusBarColor);
                navController.navigate(R.id.FirstFragment);
                break;
        }
    }

    private void checkCache() {
        ReadWriteCache readWriteCache = new ReadWriteCache(getContext());
        if (readWriteCache.cacheFileExist()) {
            String cacheData = readWriteCache.readFromCache();
            String[] commands = cacheData.split(":");
            String username = commands[0].trim();
            String sessionKey = commands[1].trim();
            String request = "103::" + username + "::" + sessionKey;
            AppManager.getInstance().establishConnection();
            AppManager.getInstance().requestToServer(request);
            AppManager.getInstance().appInFocus = true;
        } else {
            navController.navigate(R.id.FirstFragment);
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
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "SetupFragment: In the onDestroyView() event");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "SetupFragment: In the onAttach() event");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "SetupFragment: In the onResume event");
        checkCache();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "SetupFragment: In the onActivityCreated() event");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "SetupFragment: In the onStart() event");
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
                    Log.i(TAG, "SetupFragment: Permission granted");
                } else {
                    // Permission not granted
                    Log.i(TAG, "SetupFragment: Permission not granted");

                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "SetupFragment: In the onPause() event");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "SetupFragment: In the onStop() event");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "SetupFragment: In the onDestroy() event");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "SetupFragment: In the onDetach() event");
    }


}