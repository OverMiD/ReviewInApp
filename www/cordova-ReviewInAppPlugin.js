var exec = require('cordova/exec');

exports.requestReview = function (arg0, success, error) {
    exec(success, error, 'cordova-ReviewInAppPlugin', 'requestReview', [arg0]);
};

exports.requestReviewInApp = function (success, error) {
    exec(success, error, 'cordova-ReviewInAppPlugin', 'requestReviewInApp', []);
};