const http = require('http');
const axios = require('axios');
const jsdom = require('jsdom');
const { JSDOM } = jsdom;

const host = '0.0.0.0';
const port = 3000;

const server = http.createServer((req, res) => {

    axios.get('https://intern.fh-wedel.de/~splan/index.html')
        .then(html => {
            const dom = new JSDOM(html.data);

            const title = dom.window.document.querySelectorAll('div[style] a');

            title.forEach(elem => { console.log(elem.textContent) })



            res.statusCode = 200;
            res.setHeader('Content-Type', 'text/plain');
            res.end(title.textContent);
        });

});

server.listen(port, host, () => {
    console.log('Web server running at http://%s:%s', host, port);
});