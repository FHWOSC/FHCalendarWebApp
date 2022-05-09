const axios = require('axios');
const { JSDOM } = require('jsdom');

function parse(url) {
    
    axios.get(url)
    .then(html => {
        const dom = new JSDOM(html.data);
        const document = dom.window.document;

        const tablePlan = document.querySelector("tbody");
        const rows = tablePlan.childNodes;
        
        const timeSlots = [];
        const trHeader = tablePlan.firstChild;
        trHeader.childNodes.forEach(header => {
            const bounds = header.innerHtml.split(" - ");
            if (bounds.length != 2) return;

            timeSlots.push({
                start:  bounds[0],
                end:    bounds[1]
            });
        });
    });

}

module.exports = {
    "parse": parse
}

