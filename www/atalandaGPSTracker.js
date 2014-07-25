var exec = require("cordova/exec");
module.exports = {
  config: function(config) {
    exec(function() {}, function() {},
      'atalandaGPSTracker',
      'configure',
      [config]);
  },
  startTracking: function(params) {
    exec(function() {}, function() {},
      'atalandaGPSTracker',
      'startTracking',
      [params]);
  },
  stopTracking: function(success, failure) {
    exec(function() {}, function() {},
      'atalandaGPSTracker',
      'stopTracking',
      []);
  },
};
