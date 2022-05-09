const axios = require('axios');
const { JSDOM } = require('jsdom');

function parse(url) {
    const dom = new JSDOM(html.data);

    axios.get(url)
    .then(html => {

    });

}

module.exports = {
    "parse": parse
}

