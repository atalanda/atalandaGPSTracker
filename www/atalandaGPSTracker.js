var exec = require("cordova/exec");
module.exports = {
  config: function(config) {
    exec(function() {}, function() {},
      'atalandaGPSTracker',
      'configure',
      [config]);
  },
  startTracking: function(success, failure, config) {
    exec(success || function() {},
      failure || function() {},
      'atalandaGPSTracker',
      'startTracking',
      []);
  },
  stopTracking: function(success, failure, config) {
    exec(success || function() {},
      failure || function() {},
      'atalandaGPSTracker',
      'stopTracking',
      []);
  },
};
