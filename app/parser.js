const axios = require('axios');
const { JSDOM } = require('jsdom');

function parse(url) {
    console.log(url)
    
    axios.get(url)
    .then(html => {
        console.log(html);
        const dom = new JSDOM(html.data);
        const document = dom.window.document;

        const tablePlan = document.querySelector("tbody");
        const rows = tablePlan.childNodes;
        
        const timeSlots = [];
        const rowHeader = tablePlan.firstChild;
        rowHeader.childNodes.forEach(header => {
            const bounds = header.innerHtml.split(" - ");
            if (bounds.length != 2) return;

            timeSlots.push({
                start:  bounds[0],
                end:    bounds[1]
            });
        });

        const events = [];

        const trDays = [...rows].slice(1);
        for (let day of trDays) {
            if (day.rowSpan > 1) {

            }
        }

    });
}

module.exports = {
    "parse": parse
}

