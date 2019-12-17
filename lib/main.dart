// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class PlatformChannel extends StatefulWidget {
  @override
  _PlatformChannelState createState() => _PlatformChannelState();
}

class _PlatformChannelState extends State<PlatformChannel> {

  static const MethodChannel bluetoothChannel = MethodChannel('samples.flutter.io/bluetooth');

  void _handleClick() {
    _connectToNearbyDevice();
  }

  void _showSnackBar(String text) {
    Scaffold.of(context).showSnackBar(new SnackBar(
        content: new Text(text),
        action: SnackBarAction(
          label: 'Got it',
          onPressed: () {},
        ),
    ));
  }

  void _connectToNearbyDevice() async {

    try {
      if (!await _isBluetoothAvailable()) {
        _showSnackBar("No bluetooth adapter found...");
        return;
      }

      if (!await _isBluetoothEnabled()) {
        if (!await _enableBluetooth()) {
          _showSnackBar("Why won't you let me ?");
          return;
        }
      }

      if (!await _checkPermissions()) {
        _showSnackBar("Permission denied");
        return;
      }

      if (!await _startScan()) {
        _showSnackBar("No Octavio device found");
        return;
      }
    }
    catch (e) { _showSnackBar(e.message); }

  }

  Future<bool> _isBluetoothAvailable() async {
    try {
      return await bluetoothChannel.invokeMethod('isBluetoothAvailable');
    }
    on PlatformException catch (e) { throw e; }
  }

  Future<bool> _isBluetoothEnabled() async {
    try {
      return await bluetoothChannel.invokeMethod('isBluetoothEnabled');
    }
    on PlatformException catch (e) { throw e; }
  }

  Future<bool> _enableBluetooth() async {
    try {
      return await bluetoothChannel.invokeMethod('enableBluetooth');
    }
    on PlatformException catch (e) { throw e; }
  }

  Future<bool> _startScan() async {
    try {
      return await bluetoothChannel.invokeMethod('startScan')
                                .timeout(const Duration(seconds: 10), onTimeout: _onTimeOut);
    }
    on PlatformException catch (e) { throw e; }
  }

  bool _onTimeOut() {
    bluetoothChannel.invokeMethod('stopScan');
    return false;
  }

  Future<bool> _checkPermissions() async {
    PermissionStatus permission = await PermissionHandler().checkPermissionStatus(PermissionGroup.location);

    if (permission == PermissionStatus.denied) {
      Map<PermissionGroup, PermissionStatus> permissions = await PermissionHandler().requestPermissions([PermissionGroup.location]);
      return permissions[PermissionGroup.location] == PermissionStatus.disabled ||
             permissions[PermissionGroup.location] == PermissionStatus.granted;
    }
    return true;
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: <Widget>[
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: RaisedButton(
                  child: const Text('Connect to a nearby device'),
                  onPressed: _handleClick,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

void main() {
  runApp(
      MaterialApp(
          title: 'Bluetooth Auto Connect',
          home: Scaffold(
            appBar: AppBar(
              title: Text('Bluetooth Auto Connect'),
            ),
            body: PlatformChannel(),
          ),
      )
  );
}
