'use strict';

const parser = require("../parser.js");

/**
 * Export the splan to the ICalender format
 *
 * url String URL of custom splan
 * nameformat String Format of person names in output (optional)
 * roomformat String Format of room names in output (optional)
 * no response value expected for this operation
 **/
exports.getSplanAsIcs = function(url,nameformat,roomformat) {
  return new Promise(function(resolve, reject) {
    resolve();
  });
}


/**
 * Export the splan to JSON
 *
 * url String URL of custom splan
 * returns Plan
 **/
exports.getSplanAsJson = function(url) {
  return parser.parseAsync(url);
}


/**
 * Export the splan to PDF
 *
 * url String URL of custom splan
 * nameformat String Format of person names in output (optional)
 * roomformat String Format of room names in output (optional)
 * orientation String Orientation of the the table (horizontal or vertical) (optional)
 * no response value expected for this operation
 **/
exports.getSplanAsPdf = function(url,nameformat,roomformat,orientation) {
  return new Promise(function(resolve, reject) {
    resolve();
  });
}

