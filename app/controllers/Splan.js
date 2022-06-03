'use strict';

var utils = require('../utils/writer.js');
var Splan = require('../service/SplanService');

module.exports.getSplanAsIcs = function getSplanAsIcs (req, res, next) {
  var url = req.swagger.params['url'].value;
  var nameformat = req.swagger.params['nameformat'].value;
  var roomformat = req.swagger.params['roomformat'].value;
  Splan.getSplanAsIcs(url,nameformat,roomformat)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getSplanAsJson = function getSplanAsJson (req, res, next) {
  var url = req.swagger.params['url'].value;
  Splan.getSplanAsJson(url)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getSplanAsPdf = function getSplanAsPdf (req, res, next) {
  var url = req.swagger.params['url'].value;
  var nameformat = req.swagger.params['nameformat'].value;
  var roomformat = req.swagger.params['roomformat'].value;
  var orientation = req.swagger.params['orientation'].value;
  Splan.getSplanAsPdf(url,nameformat,roomformat,orientation)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};
