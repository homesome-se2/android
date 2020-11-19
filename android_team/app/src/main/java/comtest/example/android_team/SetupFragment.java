package comtest.example.android_team;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import comtest.example.android_team.models.ReadWriteCache;


public class SetupFragment extends Fragment implements UpdateResponse {

    /* A middle hand that is there when we are setting up new fragments */

    private static final String TAG = "Info";
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        Log.i(TAG, "In the SetupFragment");
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        AppManager.getInstance().currentFragment = this;


        return view;
    }


    @Override
    public void update(int indexProtocol, String message) {
        switch (indexProtocol) {
            case 104:
                navController.navigate(R.id.SecondFragment);
                Log.i(TAG, "Open Second fragment");
                break;
            case 901:
            case 903:
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                navController.navigate(R.id.FirstFragment);
                break;
        }
    }

    private void checkCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    ReadWriteCache readWriteCache = new ReadWriteCache(getContext());
                    if (readWriteCache.cacheFileExist()) {
                        String cacheData = readWriteCache.readFromCache();
                        String[] commands = cacheData.split(":");
                        String username = commands[0].trim();
                        String sessionKey = commands[1].trim();
                        String request = "103::" + username + "::" + sessionKey;
                        AppManager.getInstance().establishConnection();
                        AppManager.getInstance().requestToServer(request);
                    } else {
                        navController.navigate(R.id.FirstFragment);
                    }
            }
        }).start();

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