atalandaGPSTracker
==================

Cordova Plugin for tracking GPS in fore- and background mode.

## Getting started ##
Install the plugin with cordova's CLI:

```
cordova plugin add https://github.com/atalanda/atalandaGPSTracker
```

## Usage ##

Before tracking can be started, you must provide a configuration:

```
window.plugins.atalandaGPSTracker.configure({
  url: "http://yourdomain.com/yourcallback"
});
```

Now tracking can be started and stopped:

```
window.plugins.atalandaGPSTracker.startTracking();
window.plugins.atalandaGPSTracker.stopTracking();
```

For every location update, a JSON encoded `POST` request with the following format is made:
```
{
  "latitude": 47.80668,
  "longitude": 13.04949,
  "horizontalAccuracy": 65, // lat/lng identifies the center of a circle with a radius of 65 meters
  "altitude": 436.3852, // altitude measured in meters
  "verticalAccuracy": 10, // accuracy of altitude in meters
  "velocity": -1, // speed in m/s, negative value indicates an invalid speed
  "timestamp": "2014-07-25 12:22:04:0004", // time when the location was tracked
  "batteryLevel": 0.95 // between 0..1
}
```

## Passing additional parameters ##

If you need to pass additional parameters along with the POST request, do it like this:

```
window.plugins.atalandaGPSTracker.startTracking({
  myValues: [1,2,3,4,5]
});
```

The resulting JSON that gets posted looks like this:

```
{
  "latitude": 47.80668,
  "longitude": 13.04949,
  "horizontalAccuracy": 65, // lat/lng identifies the center of a circle with a radius of 65 meters
  "altitude": 436.3852, // altitude measured in meters
  "verticalAccuracy": 10, // accuracy of altitude in meters
  "velocity": -1, // speed in m/s, negative value indicates an invalid speed
  "timestamp": "2014-07-25 12:22:04:0004", // time when the location was tracked
  "batteryLevel": 0.95, // between 0..1

  "myValues": [1,2,3,4,5] // additional parameters
}
```

## Platform specific hints ##

### iOS ###

Only tested with iOS 7.1. Foreground will work, but background tracking changed since iOS 7.1.

**The plugin specifies a background mode property for background location updates! See your `*-Info.plist`:**

```
<key>UIBackgroundModes</key>
<array>
  <string>location</string>
</array>
```

### Android ###

Not implemented yet.

## License ##
The MIT License (MIT)

Copyright (c) 2014 atalanda

see LICENSE file