# Flutter - Bluetooth Auto Connect

This project aims to leverage Flutter's method channel to automatically connect your device to a Bluetooth Device.
For now, the code is only used on Android as Apple does not give access to classic bluetooth programmatically on iOS.

The program will ensure in the following order that:

1) The device has bluetooth.
2) The bluetooth is enabled (if not, prompt a modal to turn it on).
3) The app has access to the user's location (if not, prompt a modal to allow). This is mandatory as scanning nearby devices requires location permission.
4) The device starts a scan for one bluetooth device that matches a regex.
5) If found, the app automatically connects to it.
6) Pairing for the first time, requires the user's permission by prompting a modal (again).
7) Done.