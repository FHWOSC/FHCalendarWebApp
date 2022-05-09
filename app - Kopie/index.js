const http = require('http');
const urlParser = require('url');
const parser = require('./parser');

const host = '0.0.0.0';
const port = 3000;

const server = http.createServer((req, res) => {

    const query = urlParser.parse(req.url, true).query;

    let result;
    try {
        result = parser.parse(query.url);
    } catch (error) {
        res.statusCode = 400;
    } finally {
        res.statusCode = 200;
        res.setHeader('Content-Type', 'application/json');
        res.end(result);
    }

    //export according to  query.format, query.nameformat, query.roomformat

});

server.listen(port, host, () => {
    console.log('Web server running at http://%s:%s', host, port);
});