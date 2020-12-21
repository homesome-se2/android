package comtest.example.android_team.plugplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import comtest.example.android_team.R;

public class PlugPlayInitFragment extends Fragment {

    private static final String TAG = "Info";
    private NavController navController;

    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private ArrayList<String> reachableSSIDs;
    private boolean confirmedLAN;
    private boolean confirmedHub;
    private boolean confirmedGadget;
    // ==== Collect data ========
    private String homeNetworkSSID;
    private String homeNetworkPwd;
    private String localHubIP;
    private int localHubPort;
    // ==== 1. Home network =====
    private CardView cardHomeNetwork;
    private ImageView processHomeNet;
    private TextView labelHomeNet;
    private ImageButton btnSearchHomeNet;
    private Button sync_btn;
    private FrameLayout loading;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plugplay_setup, container, false);
        Log.i(TAG, "In the SetupFragment");
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        initialization(view);


        btnSearchHomeNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListNetworkAPs();
            }
        });

        // ==== Utilities ========
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        reachableSSIDs = new ArrayList<>();

        // ==== Start ============


        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                launch();
            }
        });

        return view;
    }

    private void initialization(View view) {
        sync_btn = view.findViewById(R.id.sync_btn);
        loading = view.findViewById(R.id.frameLoading);
        loading.setVisibility(View.INVISIBLE);
        // ==== 1. Home network =====
        cardHomeNetwork = view.findViewById(R.id.cardHome);
        cardHomeNetwork.setAlpha(0.0f);
        processHomeNet = view.findViewById(R.id.verify_process);
        labelHomeNet = view.findViewById(R.id.network_SSID);
        btnSearchHomeNet = view.findViewById(R.id.btn_search_SSID);

    }

    private void launch() {
        if (!wifiManager.isWifiEnabled()) {
            Log.i(TAG, "Enable WiFi and try again.");
            Toast.makeText(getContext(), "Enable WiFi and try again.", Toast.LENGTH_SHORT).show();
        } else {
            //    reset();
            scanWifi();
        }
        btnSearchHomeNet.setEnabled(false);
    }

    private void scanWifi() {
        reachableSSIDs.clear();
        getActivity().registerReceiver(onWiFiScanComplete, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan(); // Limited usage since Android P. Foreground apps: Max 4 scans every 2 minutes.
        Log.i(TAG, "Scanning WiFi...");
        Toast.makeText(getContext(), "Scanning WiFi...", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver onWiFiScanComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanResults = wifiManager.getScanResults();
            getActivity().unregisterReceiver(this);

            for (ScanResult result : scanResults) {
                if (!result.SSID.trim().isEmpty()) {
                    reachableSSIDs.add(result.SSID);
                }
            }
            // Once WiFi AP's has been identified, proceed to read history (previous settings)
            //  readHistoryFromCache();
            verifyHomeNetwork();
        }
    };


/*    private void onCacheReadComplete(boolean successfulCacheRead) {
        if (successfulCacheRead) {
            homeNetworkSSID = history.homeNetworkSSID;
            localHubIP = history.localHubIP;
            localHubPort = history.localHubPort;
        } else {
            homeNetworkSSID = "not defined";
            localHubIP = "";
            localHubPort = 0;
        }
        verifyHomeNetwork();
    //    verifyHub();
    //    discoverGadget();
    }*/


    // =============================== 1. Home network =============================================

    private void verifyHomeNetwork() {
        homeNetworkSSID = "not defined";
        localHubIP = "";
        localHubPort = 0;
        WifiInfo info = wifiManager.getConnectionInfo();
        homeNetworkSSID = info.getSSID().replace("\"", "");
        labelHomeNet.setText(homeNetworkSSID);
        confirmedLAN = true;
        processHomeNet.setImageResource(R.drawable.icon_verify_ok);
        btnSearchHomeNet.setEnabled(true);
        cardHomeNetwork.animate()
                .alpha(1.0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        loading.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private void dialogListNetworkAPs() {
        final String[] items = reachableSSIDs.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select your home network");
        // Define list items and actions.
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                homeNetworkSSID = reachableSSIDs.get(pos);
                verifyHomeNetwork();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // Create and show alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
