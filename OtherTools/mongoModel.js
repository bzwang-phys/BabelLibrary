const mongoose = require('mongoose')

const sleepSchema = new mongoose.Schema({
    day: String,
    sleepTime: String,
    getupTime: String
})
const sleepModel = mongoose.model("sleep", sleepSchema);

const timeusedSchema = new mongoose.Schema({
    date: String,
    math: Number,
    Physics: Number,
    EE: Number,
    CS: Number,
    Language: Number,
    Reading: Number,
    Economics: Number,
    Sum: Number
})
const timeusedModel = mongoose.model("timeused", timeusedSchema);


module.exports.sleepModel = sleepModel;
module.exports.timeusedModel = timeusedModel;