const express = require('express')
const router = require('./router')
const app = express()

app.set("view engine", "ejs");
app.use('/public/', express.static('./public/'));
app.use(express.urlencoded({extended:false}));
app.use(express.json());
app.use(router);



const port = 18000;
app.listen(port, ()=>{serverInit(port)} );




function serverInit(port) {
    console.log("start at port: " + String(port));
}
