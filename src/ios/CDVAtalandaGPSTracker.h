//
//  atalandaGPSTracker.h
//
//  Created by Hubert Hölzl <hubert.hoelzl@atalanda.com> on 2014-07-25
//

#import <CoreLocation/CoreLocation.h>
#import <Cordova/CDVPlugin.h>

@interface CDVAtalandaGPSTracker : CDVPlugin <CLLocationManagerDelegate>
- (void) configure:(CDVInvokedUrlCommand*)command;
- (void) startTracking:(CDVInvokedUrlCommand*)command;
- (void) stopTracking:(CDVInvokedUrlCommand*)command;
@end