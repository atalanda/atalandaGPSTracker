var exec = require("cordova/exec");
module.exports = {
  configure: function(config, success, error) {
    exec(success, error,
      'atalandaGPSTracker',
      'configure',
      [config]);
  },
  startTracking: function(params, success, error) {
    exec(success, error,
      'atalandaGPSTracker',
      'startTracking',
      [params]);
  },
  stopTracking: function(success, failure) {
    exec(success, error,
      'atalandaGPSTracker',
      'stopTracking',
      []);
  },
};
