// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PlatformChannel extends StatefulWidget {
  @override
  _PlatformChannelState createState() => _PlatformChannelState();
}

class _PlatformChannelState extends State<PlatformChannel> {

  static const MethodChannel methodChannel =
  MethodChannel('samples.flutter.io/bluetooth');

  String _batteryLevel = 'Battery level: unknown.';
  String _bluetoothAdapter = 'Bluetooth adapter: unknown';

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = await methodChannel.invokeMethod('getBatteryLevel');
      batteryLevel = 'Battery level: $result%.';
    } on PlatformException {
      batteryLevel = 'Failed to get battery level.';
    }
    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  Future<void> _getBluetoothAdapter() async {
    String bluetoothAdapter;
    try {
      final int result = await methodChannel.invokeMethod('getBluetoothAdapter');
      bluetoothAdapter = 'Battery level: $result%.';
    } on PlatformException {
      bluetoothAdapter = 'Failed to get battery level.';
    }
    setState(() {
      _bluetoothAdapter = bluetoothAdapter;
    });
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
              Text(_bluetoothAdapter, key: const Key('Battery level label')),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: RaisedButton(
                  child: const Text('Refresh'),
                  onPressed: _getBluetoothAdapter,
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
  runApp(MaterialApp(home: PlatformChannel()));
}
