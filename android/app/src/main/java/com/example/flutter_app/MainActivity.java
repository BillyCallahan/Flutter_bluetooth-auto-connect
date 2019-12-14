package com.example.flutter_app;

import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  private static final String BATTERY_CHANNEL = "samples.flutter.io/bluetooth";
  private final static int REQUEST_ENABLE_BT = 1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), BATTERY_CHANNEL).setMethodCallHandler(
        new MethodCallHandler() {
          @Override
          public void onMethodCall(MethodCall call, Result result) {
           if (call.method.equals("getBluetoothAdapter")) {

              BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
              if (bluetoothAdapter != null) {

                if (!bluetoothAdapter.isEnabled()) {
                  if (VERSION.SDK_INT >= VERSION_CODES.ECLAIR) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    result.success(1);
                  }
                }
              } else {
                result.error("UNAVAILABLE", "Bluetooth adapter not available", null);
              }

            } else {
              result.notImplemented();
            }
          }
        }
      );
  }

  private BluetoothAdapter getBluetoothAdapter() {
    if (VERSION.SDK_INT >= VERSION_CODES.ECLAIR) {
      return BluetoothAdapter.getDefaultAdapter();
    }
    return null;
  }
}
