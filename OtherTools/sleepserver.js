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

async function saveTime(timeDict) {
    let date = new Date();
    let dayStr = date.getFullYear().toString()+"-"+(date.getMonth()+1).toString()+"-"+date.getDate().toString();
    
    await connctDB();
    var dataTime = new mongoModel.sleepModel({day: dayStr,
        sleepTime: timeDict["sleepTime"],
        getupTime: timeDict["getupTime"]
    });
    res = dataTime.save(function (err){
        if(err){ Promise.reject(false); };
        Promise.resolve(true);
    })
}

function readTime() {
    connctDB();
    return new Promise((resolve, reject)=>{
        mongoModel.sleepModel.find((err, comments)=>{
            if (err) {
                console.log("Error: " + err.toString());
            } else {resolve(comments);}
        })
    })
}

function saveAndRead(paraDict) {
    return new Promise(function (resolve, reject) {
        saveTime(paraDict).then(val=>{
            // readTime().then(val=>{ resolve(val); })
            resolve(true);
        }).catch(val => {
            resolve("Save data Wrong")
        })
})
}

exports.saveAndRead = saveAndRead
exports.readTime = readTime