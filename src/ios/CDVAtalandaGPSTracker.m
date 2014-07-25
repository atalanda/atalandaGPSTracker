//
//  atalandaGPSTracker.m
//
//  Created by Hubert Hölzl <hubert.hoelzl@atalanda.com> on 2014-07-25
//

#import <CoreLocation/CoreLocation.h>
#import "CDVAtalandaGPSTracker.h"
#import <Cordova/CDVJSON.h>

#define maxAgeOfPosition 5.0
#define minHighAccuracyResults 5
#define lowAccuracyDistanceFilter 100.0f

@implementation CDVAtalandaGPSTracker {
    CLLocationManager *locationManager;
    BOOL configSet;
    NSString *url;
    // counter to first get high accuracy results
    NSInteger receivedLocationUpdates;
}

/**
 * configure plugin
 * @param {String} token
 * @param {String} url
 * @param {Number} stationaryRadius
 * @param {Number} distanceFilter
 * @param {Number} locationTimeout
 */
- (void) configure:(CDVInvokedUrlCommand*)command
{
    NSDictionary *config = [command.arguments objectAtIndex:0];
    url = config[@"url"];
    NSLog(@"Configuration set");

    configSet = TRUE;

    CDVPluginResult* result = nil;
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)startTracking:(CDVInvokedUrlCommand*)command {
    if(!configSet) {
        NSException* noConfigurationException = [NSException exceptionWithName:@"ConfigurationNotSet"
                                                           reason:@"No configuration is set, please call configure first!"
                                                         userInfo:nil];
        @throw noConfigurationException;
    }

    if(locationManager) {
      NSLog(@"Already tracking! Can't start again");
      return;
    }

    // start monitoring battery
    [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];

    // reset the updates count
    receivedLocationUpdates = 0;

    // configure location manager
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    // first set the highest accuracy, to determine the current location
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    locationManager.distanceFilter = kCLDistanceFilterNone;
    // allow iOS to pause the updates to save battery
    locationManager.pausesLocationUpdatesAutomatically = YES;
    locationManager.activityType = CLActivityTypeOther;
    NSLog(@"startUpdatingLocation");
    [locationManager startUpdatingLocation];

    CDVPluginResult* result = nil;
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)stopTracking:(CDVInvokedUrlCommand*)command {
    [[UIDevice currentDevice] setBatteryMonitoringEnabled:NO];
    if(locationManager) {
        NSLog(@"stopUpdatingLocation");
        [locationManager stopUpdatingLocation];
        locationManager = nil;
    }

    CDVPluginResult* result = nil;
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

// notifies when authorization changes (e.g. when the user disables / enables location services
- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status
{
    if(status == kCLAuthorizationStatusAuthorized) {
        NSLog(@"User authorized use of location!");
    } else if(status == kCLAuthorizationStatusNotDetermined) {
        NSLog(@"Location authorization pending!");
    } else {
        NSLog(@"User denied location access!");
    }
}

- (void)setLowAccuracyOnRuntime
{
    // if we are already at a low accuracy, don't change it!
    if (locationManager.desiredAccuracy == kCLLocationAccuracyNearestTenMeters) {
        return;
    }
    NSLog(@"switch to low accuracy");
    [locationManager stopUpdatingLocation];
    locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters;
    locationManager.distanceFilter = lowAccuracyDistanceFilter;
    [locationManager startUpdatingLocation];
}


- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations {
    CLLocation* location = [locations lastObject];
    NSDate* eventDate = location.timestamp;
    NSTimeInterval howRecent = [eventDate timeIntervalSinceNow];

    // only use locations that are not cached and recent enough
    BOOL notCached = abs(howRecent) < maxAgeOfPosition;
    if (notCached) {
        // if we have enough high accurate results, switch to lower accuracy to save battery
        if(receivedLocationUpdates < minHighAccuracyResults) {
            receivedLocationUpdates++;
        } else {
            [self setLowAccuracyOnRuntime];
        }

        // we received a new position
        NSLog(@"latitude %f, longitude %f\n", location.coordinate.latitude, location.coordinate.longitude);
        [self updateOnServer:location];
    }
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied){
        NSLog(@"User has denied location services");
    } else {
        NSLog(@"Location manager did fail with error: %@", error.localizedFailureReason);
    }
}

- (void)updateOnServer:(CLLocation *)location {
    float batteryLevel = [[UIDevice currentDevice] batteryLevel];

    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss:ssss"];
    [dateFormatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
    NSString *timestamp = [dateFormatter stringFromDate:location.timestamp];

    NSDictionary *geodata = @{
                              @"latitude" : [NSNumber numberWithFloat:location.coordinate.latitude],
                              @"longitude" : [NSNumber numberWithFloat:location.coordinate.longitude],
                              @"horizontalAccuracy": [NSNumber numberWithFloat:location.horizontalAccuracy],
                              @"altitude": [NSNumber numberWithFloat:location.altitude],
                              @"verticalAccuracy": [NSNumber numberWithFloat:location.verticalAccuracy],
                              @"velocity": [NSNumber numberWithFloat:location.speed],
                              @"timestamp": timestamp,
                              @"batteryLevel": [NSNumber numberWithFloat:batteryLevel]
                              };

    NSError * err;
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:geodata options:0 error:&err];
    NSString * myString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    NSMutableURLRequest *postRequest = [NSMutableURLRequest requestWithURL: [NSURL URLWithString:url]];
    postRequest.HTTPMethod = @"POST";
    [postRequest setValue:@"application/json; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    postRequest.HTTPBody = [myString dataUsingEncoding: NSUTF8StringEncoding];
    [NSURLConnection sendAsynchronousRequest:postRequest queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
                               NSLog(@"REQUEST SENT TO SERVER!");
                           }];
}


@end
