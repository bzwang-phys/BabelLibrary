const dns = require('dns');
const { url } = require('inspector');


function checkInternet() {
    let promise = new Promise(function (resolve, reject) {
        dns.resolve('www.baidu.com', function (err, stringList) {
            if (err) {reject("No connection");} 
            else {resolve("Connected");}
        })
    });

    promise.then(function (res) {
        console.log(res);
        return true;
    })
    .catch(function (err) {
        console.log(err);
        return false;
    }) 
}


res = checkInternet();

fetch()