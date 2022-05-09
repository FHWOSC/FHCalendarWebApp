'use strict';

var utils = require('../utils/writer.js');
var Splan = require('../service/SplanService');

module.exports.getSplan = function getSplan(req, res, next) {
  var url = req.swagger.params['url'].value;
  var nameformat = req.swagger.params['nameformat'].value;
  var roomformat = req.swagger.params['roomformat'].value;
  var format = req.swagger.params['format'].value;

  Splan.getSplan(url, nameformat, roomformat, format)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};
