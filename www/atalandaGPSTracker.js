var exec = require("cordova/exec");
module.exports = {
  configure: function(config) {
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
