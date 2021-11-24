const axios = require("axios");
const cheerio = require("cheerio");
const mongoose = require('mongoose');
const mongoModel = require("./mongoModel");

function connctDB() {
    mongoose.connect('mongodb://localhost:27017/Others', 
        {useNewUrlParser: true});
    const db = mongoose.connection;
    db.on('error', console.error.bind(console, "Connection Error: "));
    db.once('open', ()=>{console.log('Successfully connected to MongoDB.')});    
}

function dataTransform(data, key){
    if (key in data) { return Number(data[key]).toFixed(2); }
    else{ return 0;}
}

function saveTime(dataDict) {
    connctDB()

    sum = 0;
    for (var key in dataDict) {
        if (key!="action") {sum += Number(dataDict[key])}
    }

    var timeused = new mongoModel.timeusedModel({
        date: new Date().toISOString(),
        math: dataTransform(dataDict,"math"),
        Physics: dataTransform(dataDict,"Physics"),
        EE: dataTransform(dataDict,"EE"),
        CS: dataTransform(dataDict,"CS"),
        Language: dataTransform(dataDict,"Language"),
        Reading: dataTransform(dataDict,"Reading"),
        Economics: dataTransform(dataDict,"Economics"),
        Sum: sum.toFixed(2)
    });

    return new Promise((resolve,reject)=>{
        res = timeused.save(function (err){
            if(err){ reject(false); };
            resolve(true);
        })
    });
    
}

function readTime() {
    connctDB();
    return new Promise((resolve, reject)=>{
        mongoModel.timeusedModel.find((err, comments)=>{
            if (err) {
                console.log("Error: " + err.toString());
            } else {resolve(comments);}
        })
    })
}

function saveAndRead(paraDict) {
    return new Promise(function (resolve, reject) {
        saveTime(paraDict).then(val=>{
            resolve(true);
            // readTime().then(val=>{ resolve(val); })
        }).catch(val => {
            resolve("Save data Wrong");
        })
})
}

exports.saveAndRead = saveAndRead
exports.readTime = readTime