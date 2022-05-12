'use strict';
const parser = require('../parser')

/**
 * export the splan
 * exports the splan
 *
 * url String url of custom splan
 * nameformat String Format of person names for calendar output (optional)
 * roomformat String Format of room names for calendar output (optional)
 * format String output format of calendar (optional)
 * returns List
 **/
exports.getSplan = function (url, nameformat, roomformat, format) {
  return parser.parseAsync(url);
}

