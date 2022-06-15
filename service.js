let path = require("path");
let Service = require("node-windows").Service;

let service = new Service({
    name: 'node Babel Library',
    description: 'A Node Service For Babel Library.',
    script: path.resolve("./service.js")

});

service.on('install', ()=>{ service.start(); });

service.install();