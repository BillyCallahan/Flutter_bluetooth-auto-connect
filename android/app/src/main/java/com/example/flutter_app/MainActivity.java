package com.example.flutter_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugins.GeneratedPluginRegistrant;

@TargetApi(5)
public class MainActivity extends FlutterActivity {

    private static final String BATTERY_CHANNEL = "samples.flutter.io/bluetooth";
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice octavioDevice;
    private MethodCall pendingCall;
    private Result pendingResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        new MethodChannel(getFlutterView(), BATTERY_CHANNEL).setMethodCallHandler(
                new MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, Result result) {

                        switch (call.method){
                            case "isBluetoothAvailable":
                                result.success(btAdapter != null);
                                break;

                            case "isBluetoothEnabled":
                                result.success(btAdapter.isEnabled());
                                break;

                            case "enableBluetooth":
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                pendingResult = result;
                                break;

                            case "startScan":
                                startScan();
                                pendingResult = result;
                                break;

                            case "stopScan":
                                stopScan();
                                break;

                             default:
                                 result.notImplemented();
                        }

                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_ENABLE_BT)
            pendingResult.success(resultCode != 0);

    }

    @TargetApi(5)
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if ( isDeviceMatching(device.getName()) ) {
                    octavioDevice = device;
                    btAdapter.cancelDiscovery();
                    unregisterReceiver(receiver);
                    pendingResult.success(true);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("Finiiii");
            }
        }
    };

    private Boolean isDeviceMatching(String deviceName){
        //prevent from getting a null deviceName, happens sometimes
        if (deviceName == null) return false;

        String pattern = "^Octavio_[a-zA-Z0-9]{4}";
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(deviceName);

        return matcher.find();
    }

    private void startScan() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        btAdapter.startDiscovery();
    }

    private void stopScan() {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
    }

}
