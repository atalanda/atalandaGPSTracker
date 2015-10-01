var exec = require("cordova/exec");
module.exports = {
  configure: function(config, success, error) {
    exec(success, error,
      'atalandaGPSTracker',
      'configure',
      [config]);
  },
  setAdditionalHeader: function(params, success, error) {
    exec(success, error,
      'atalandaGPSTracker',
      'setAdditionalHeader',
      [params]);
  },
  startTracking: function(params, success, error) {
    params = params || {}; // default params to empty object {}
    exec(success, error,
      'atalandaGPSTracker',
      'startTracking',
      [params]);
  },
  stopTracking: function(success, error) {
    exec(success, error,
      'atalandaGPSTracker',
      'stopTracking',
      []);
  },
};
