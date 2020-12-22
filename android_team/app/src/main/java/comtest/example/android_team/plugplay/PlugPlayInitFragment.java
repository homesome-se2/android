package comtest.example.android_team.plugplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import comtest.example.android_team.AppManager;
import comtest.example.android_team.R;
import comtest.example.android_team.UpdateResponse;
import comtest.example.android_team.voiceSystem.TTS;

public class PlugPlayInitFragment extends Fragment implements UpdateResponse {

    private static final String TAG = "Info";
    private NavController navController;
    private Handler handler;
    private TTS tts;

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
    // ==== 2. Local hub =======
    private String assumedPrefixIP;
    private int suffixIP;
    private boolean scanRunning = false;
    private ImageView processLocalHub;
    private ProgressBar progressBarHub;
    private TextView labelLocalHub;
    private ImageButton btnInspectLocalHub;
    private Button btnManualLocalHub;
    // ==== 3. Discover gadget ======
    private ImageView processGadget;
    private TextView labelGadget;
    private ImageButton btnSearchGadget;
    private Button btnGadgetConnect;
    private Button btnGadgetTest;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plugplay_setup, container, false);
        Log.i(TAG, "In the SetupFragment");
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        AppManager.getInstance().currentFragment = this;
        handler = new Handler();
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

    @Override
    public void update(int indexProtocol, String message, int gadgetIndex) {

    }

    private void initialization(View view) {
        sync_btn = view.findViewById(R.id.sync_btn);
        loading = view.findViewById(R.id.frameLoading);
        loading.setVisibility(View.INVISIBLE);
        // ==== 1. Home network =====
        cardHomeNetwork = view.findViewById(R.id.cardHome);
        processHomeNet = view.findViewById(R.id.verify_process);
        labelHomeNet = view.findViewById(R.id.network_SSID);
        btnSearchHomeNet = view.findViewById(R.id.btn_search_SSID);
        // ==== 2. Local hub =======
        progressBarHub = view.findViewById(R.id.progressBarhub);
        processLocalHub = view.findViewById(R.id.verify_process2);
        labelLocalHub = view.findViewById(R.id.hub_alias);
        btnInspectLocalHub = view.findViewById(R.id.btn_inspect_hub);
        btnManualLocalHub = view.findViewById(R.id.btn_manual_input_hub);
        btnInspectLocalHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInspectHub();
            }
        });
        btnManualLocalHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogManualHub(null);
            }
        });

        // ==== 3. Discover gadget ======
        processGadget = view.findViewById(R.id.verify_process3);
        labelGadget = view.findViewById(R.id.gadget_SSID);
        btnSearchGadget = view.findViewById(R.id.btn_search_AP);
        btnGadgetConnect = view.findViewById(R.id.btn_connect_to_AP);
        btnGadgetTest = view.findViewById(R.id.btn_test_AP);
        btnGadgetConnect.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                toggleConnectionAP();
            }
        });
        btnSearchGadget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListGadgetAPs();
            }
        });
        btnGadgetTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlinkRequest();
            }
        });

    }

    private void launch() {
        if (!wifiManager.isWifiEnabled()) {
            tts.textToSpeak("Enable WiFi and try again.");
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
        Toast.makeText(getContext(), "Scanning WiFi...", Toast.LENGTH_SHORT).show();
        tts.textToSpeak("Scanning WiFi");
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
        homeNetworkSSID = info.getSSID();
        labelHomeNet.setText(homeNetworkSSID.replace("\"", ""));
        confirmedLAN = true;
        processHomeNet.setImageResource(R.drawable.icon_verify_ok);
        btnSearchHomeNet.setEnabled(true);
        // Start Hub scanning
        toggleScanLAN();

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

    // =============================== 2. Local hub =============================================

    private void verifyHub() {
        pingLANDevice(localHubIP, localHubPort, false);
    }

    private void toggleScanLAN() {
        if (scanRunning) {
            stopScan();
        } else {
    //        btnScanLocalHub.setText("Stop");
            scanRunning = true;
            initiateScanLAN();
        }
    }

    private void stopScan() {
 //       btnScanLocalHub.setText("Scan");
        scanRunning = false;
    }

    private void initiateScanLAN() {
        tts.textToSpeak("Scanning for HomeSome hub.");
        progressBarHub.setMin(0);
        progressBarHub.setMax(100);
        progressBarHub.setVisibility(View.VISIBLE);
        int intIP = wifiManager.getConnectionInfo().getIpAddress();
        @SuppressLint("DefaultLocale")
        String[] ipOctets = {
                String.format("%d", (intIP & 0xff)),        // byte 1
                String.format("%d", (intIP >> 8 & 0xff)),   // byte 2
                String.format("%d", (intIP >> 16 & 0xff)),  // byte 3
                String.format("%d", (intIP >> 24 & 0xff))}; // byte 4

        String fullIP = String.format("%s.%s.%s.%s", ipOctets[0], ipOctets[1], ipOctets[2], ipOctets[3]);
        assumedPrefixIP = String.format("%s.%s.%s", ipOctets[0], ipOctets[1], ipOctets[2]);
        suffixIP = 1;
        if (isPrivateNetwork(fullIP)) {
            proceedScan();
        } else {
            stopScan();
        }
    }

    private void proceedScan() {
        if (++suffixIP < 101 && scanRunning) { // Limit on scanning host suffix IP 0-100 (not 0-255)
            // Update percentage scan
            //int percentage = (100 * suffixIP) / 255;
            int percentage = suffixIP;
            progressBarHub.setProgress(percentage);
            Log.i(TAG, (String.format("%s %s", percentage, "%")));
            // Scan device
            String pingIP = String.format("%s.%s", assumedPrefixIP, String.valueOf(suffixIP));
            pingLANDevice(pingIP, 8084, true);
        } else {
            onPingComplete(false, null);
        }
    }

    private synchronized void pingLANDevice(final String ip, final int port, final boolean isScan) {
        Log.i(TAG, "Ping IP: " + ip);
        // Enable spinner

        new Thread(new Runnable() {
            BufferedWriter output;
            BufferedReader input;
            String[] response = {""};

            @Override
            public void run() {
                try (Socket socket = new Socket()) {
                    // Limits the time allowed to establish a connection
                    socket.connect(new InetSocketAddress(ip, port), isScan ? 1500 : 3000);
                    // Force session timeout after specified interval after connection succeeds.
                    socket.setSoTimeout(isScan ? 1500 : 3000);
                    // Obtain output & input streams
                    output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // Write request
                    output.write(String.format("%s%n", "601"));
                    output.flush();
                    // Read and process response
                    response = input.readLine().split("::");
                    Log.i(TAG, "Response: " + response[0]);
                } catch (IOException e) {
                    // Ignore
                } finally {
                    close();
                    final boolean success;
                    final String responseContent;
                    if (response.length == 2 && response[0].equals("602")) {
                        success = true;
                        responseContent = response[1];
                    } else {
                        success = false;
                        responseContent = null;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (success) {
                                localHubIP = ip;
                                localHubPort = port;
                                onPingComplete(true, responseContent);
                            } else {
                                if (isScan) {
                                    proceedScan();
                                } else {
                                    onPingComplete(false, null);
                                }
                            }
                        }
                    });
                }
            }

            private void close() {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    // Ignore
                }
            }
        }).start();
    }

    private void onPingComplete(boolean success, String responseContent) {
        stopScan();
        if (success) {
            confirmedHub = true;
            labelLocalHub.setText(responseContent);
            progressBarHub.setProgress(100);
            processLocalHub.setImageResource(R.drawable.icon_verify_ok);
            tts.textToSpeak("HomeSome hub found.");
            discoverGadget();
        } else {
            confirmedHub = false;
            labelLocalHub.setText("Hub not found");
            tts.textToSpeak("HomeSome hub not found.");
            processLocalHub.setImageResource(R.drawable.icon_verify_no);
        }
    }

    private boolean isPrivateNetwork(String IP) {
        ArrayList<String> knownPrivatePrefixes = new ArrayList<>();
        // 10.0.0.0    - 10.255.255.255
        // 172.16.0.0  - 172.31.255.255
        // 192.168.0.0 - 192.168.255.255
        knownPrivatePrefixes.add("10");
        knownPrivatePrefixes.add("192.168");
        for (int i = 16; i < 32; i++) {
            knownPrivatePrefixes.add("172".concat(String.valueOf(i)));
        }
        for (String startIP : knownPrivatePrefixes) {
            if (IP.startsWith(startIP)) {
                return true;
            }
        }
        return false;
    }

    private void dialogInspectHub() {
        /*
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selected hub is at:");

        // Set custom dialog layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_hub_address, null);
        builder.setView(customLayout);

        // Initiate custom layout text fields
        final TextView ip = (TextView) customLayout.findViewById(R.id.ip);
        final TextView port = (TextView) customLayout.findViewById(R.id.port);

        ip.setText(localHubIP.length() < 7 ? "Not defined" : localHubIP);
        port.setText(String.valueOf(localHubPort));

        // Add dialog buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Create and show alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

         */
    }

    private void dialogManualHub(String status) {
        /*
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Input hub address:");

        // Set custom dialog layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_hub_address_input, null);
        builder.setView(customLayout);

        // Initiate custom layout text fields
        final EditText editIP = customLayout.findViewById(R.id.ip);
        final EditText editPort = customLayout.findViewById(R.id.port);
        final TextView statusLabel = customLayout.findViewById(R.id.status);
        if(localHubIP.length() > 6) {
            editIP.setText(localHubIP);
            editPort.setText(String.valueOf(localHubPort));
        }
        if(status != null) {
            statusLabel.setText(status);
        }

        // Add dialog buttons
        builder.setNeutralButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String IP = editIP.getText().toString().trim();
                String port = editPort.getText().toString().trim();
                if (verifyIPv4(IP) && verifyPort(port)) {
                    localHubIP = IP;
                    localHubPort = Integer.parseInt(port);
                    // Verification:
                    pingLANDevice(localHubIP, localHubPort, false);
                } else {
                    dialogManualHub("Invalid format");
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String IP = editIP.getText().toString().trim();
                String port = editPort.getText().toString().trim();
                if (verifyIPv4(IP) && verifyPort(port)) {
                    if(!confirmedHub) {
                        localHubIP = IP;
                        localHubPort = Integer.parseInt(port);
                        // Bypass verification process:
                        onPingComplete(true, String.format("%s:%s", localHubIP, localHubPort));
                    }
                } else {
                    dialogManualHub("Invalid format");
                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // Create and show alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

         */
    }

    private boolean gadgetSelected;
    private boolean connectedToAP;
    private String gadgetSelectedSSID;

    private void discoverGadget() {
        tts.textToSpeak("Scanning for HomeSome gadget.");
        gadgetSelected = false;
        // Filter available WiFi AP's based on system specific criteria:
        String reference = "HomeSome";
        for(String SSID : reachableSSIDs) {
            if(SSID.startsWith(reference)) {
                gadgetSelected = true;
                gadgetSelectedSSID = SSID;
                labelGadget.setText(gadgetSelectedSSID);
                tts.textToSpeak("HomeSome gadget found.");
        //        spinnerGadget.setVisibility(View.INVISIBLE);
                return;
            }
            labelGadget.setText("No gadget found");
      //      spinnerGadget.setVisibility(View.INVISIBLE);
        }
        tts.textToSpeak("Scanning complete.");
        loading.setVisibility(View.INVISIBLE);
    }

    private void dialogListGadgetAPs() {
        if (connectedToAP) {
            dialogNote("Oops", "Please disconnect from current Access Point.");
        } else {
            // Copy ArrayList to String array.
            final String[] items = new String[reachableSSIDs.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] = reachableSSIDs.get(i);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select a gadget");
            // Define list items and actions.
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    gadgetSelectedSSID = reachableSSIDs.get(pos);
                    gadgetSelected = true;
                    labelGadget.setText(gadgetSelectedSSID);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            // Create and show alert dialog
            AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            dialog.show();
        }
    }

    // Trigger connect/disconnect to AP
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void toggleConnectionAP() {
        if (connectedToAP) {
            disConnectFromAP();
        } else {
            if (gadgetSelected) {
                connectToAP();
            } else {
                dialogNote("Oops", "You need to select a gadget.");
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectToAP() {

        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
        builder.setSsid(gadgetSelectedSSID);
        builder.setWpa2Passphrase(""); // Open network

        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
        NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
        networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED);
        networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
        NetworkRequest networkRequest = networkRequestBuilder.build();
        final ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull final Network network) {
                    super.onAvailable(network);
                    //Use the network object to set up connection.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cm.bindProcessToNetwork(network);
                            onConnectedToAP();
                        }
                    });
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDisconnectFromAP();
                        }
                    });
                }
            };
            cm.requestNetwork(networkRequest, networkCallback);
            keepNetworkData(cm, networkCallback);
        }
    }

    // Data needed to programmatically disconnect from AP
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback networkCallback;

    private void keepNetworkData(ConnectivityManager cm, ConnectivityManager.NetworkCallback networkCallback) {
        this.cm = cm;
        this.networkCallback = networkCallback;
    }

    private void onConnectedToAP() {
        Log.i(TAG, "Connected to AP");
        connectedToAP = true;
        btnGadgetConnect.setText("Disconnect");
        writeToAP("{\"command\":611}"); // Request HoSo confirmation (#612).
    }

    private void onDisconnectFromAP() {
        processGadget.setImageResource(R.drawable.icon_verify_no);
        btnGadgetConnect.setText("Connect");
        confirmedGadget = false;
        connectedToAP = false;
    }

    private String accessPointIP = "192.168.4.1"; // Default AP IP.
    private int accessPointPort = 8084; // Service specific.

    private void writeToAP(final String msg) {
        new Thread(new Runnable() {
            BufferedWriter output;
            BufferedReader input;
            String response = "";

            @Override
            public void run() {
                try (Socket socket = new Socket()) {
                    // Limits the time allowed to establish a connection
                    socket.connect(new InetSocketAddress(accessPointIP, accessPointPort), 4000);
                    // Force session timeout after specified interval after connection succeeds.
                    socket.setSoTimeout(2000);
                    // Obtain output & input streams
                    output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // Write request
                    output.write(String.format("%s%n", msg));
                    output.flush();
                    // Read and process response
                    response = input.readLine();
                } catch (IOException e) {
                    // Ignore
                } finally {
                    close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                   //         spinnerSubmit.setVisibility(View.INVISIBLE);
                            responseFromAP(response);
                        }
                    });
                }
            }

            private void close() {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    // Ignore
                }
            }
        }).start();
    }

    // Btn Test
    private void sendBlinkRequest() {
        if (confirmedGadget) {
            writeToAP("{\"command\":613}");
        } else {
            dialogNote("Oops", "HoSo connection has not been confirmed.");
        }
    }

    private void responseFromAP(String responseMsg) {
        if (responseMsg != null && responseMsg.length() > 2) {
            String[] response = responseMsg.split("::");
            switch (response[0]) {
                case "612": // Connection confirmation
                    processGadget.setImageResource(R.drawable.icon_verify_ok);
                    confirmedGadget = true;
                    break;
                case "615": // LAN credentials received by AP
                    disConnectFromAP(); //TODO: Test
                    dialogNote("Mission complete!", "New gadget added.");
                    // Mission complete!
                    break;
                default:
                    // Ignore
                    break;
            }
        }
    }

    private void disConnectFromAP() {
        if (connectedToAP) {
            cm.unregisterNetworkCallback(networkCallback);
            onDisconnectFromAP();
        }
    }

    // =============================== 4. Submit-field =============================================


    // ================================= UTILITIES =================================================

    private boolean verifyIPv4(String ipv4) {
        if(ipv4.isEmpty() || ipv4.endsWith(".")) {
            return false;
        }
        String[] ipSplit = ipv4.split("\\.");
        if (ipSplit.length == 4) {
            for (int i = 0; i < 4; i++) {
                try {
                    int ipByte = Integer.parseInt(ipSplit[i]);
                    if(ipByte < 0 || ipByte > 255) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean verifyPort(String portString) {
        try {
            int port = Integer.parseInt(portString);
            return port >= 0 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void dialogNote(String title, String msg) {
        /*
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        // Set custom dialog layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_exception, null);
        builder.setView(customLayout);

        // Initiate custom layout text fields
        final TextView label = (TextView) customLayout.findViewById(R.id.msg);
        label.setText(String.valueOf(msg));

        // Add dialog buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Create and show alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

         */
    }


    private void reset() {
        confirmedLAN = false;
        confirmedHub = false;
        confirmedGadget = false;
        processHomeNet.setImageResource(R.drawable.icon_verify_no);
        processLocalHub.setImageResource(R.drawable.icon_verify_no);
        processGadget.setImageResource(R.drawable.icon_verify_no);
        labelHomeNet.setText("");
        labelLocalHub.setText("");
        labelGadget.setText("");
        disConnectFromAP();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "SecondFragment: In the onStartView() event");
        tts = new TTS(getContext());
        tts.initTTS();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "SecondFragment: In the onStopView() event");
        tts.stopTTS();
    }

}
