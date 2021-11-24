var path = require('path')
const douban = require('./Library/douban')
const sleepserver = require('./OtherTools/sleepserver')
const timeUsedServer = require('./OtherTools/timeUsedServer')
var express = require('express')
var router = express.Router()

const app = express()
app.use(express.urlencoded({extended:true}));
app.use(express.json());




router.get('/', function (req, res) {
    res.sendFile(path.join(__dirname, '/views/index.html'))
})

router.get('/booksClassify', function (req, res) {
    res.sendFile(path.join(__dirname, '/views/booksClassify.html'));
})
router.post('/booksClassify', function (req, res) {
    if (req.body.action == "getBookInfo"){ res.send(douban.getbookInfo(req.body)) }
    else if (req.body.action == "openDouban"){ res.send(douban.doubanOpen(req.body)) }
    
})


router.get('/sleep', function (req, res) {
    res.sendFile(path.join(__dirname, '/views/sleep.html'));
})
router.post('/sleep', function (req, res) {
    console.log(req.body)
    if (req.body["action"] == "savedata"){ sleepserver.saveAndRead(req.body).then(val=>res.send(val)).catch(e=>{});
    } else if (req.body["action"] == "getdata") {sleepserver.readTime().then(dat=>res.send(dat));
    } else if (req.body["action"] == "deldata"){

    }
})


router.get('/timeused', function (req, res) { res.sendFile(path.join(__dirname, '/views/timeused.html')); })
router.post('/timeused', function (req, res) {
    console.log(req.body)
    if (req.body["action"] == "savedata"){ timeUsedServer.saveAndRead(req.body).then(val=>res.send(val));
    } else if (req.body["action"] == "getdata") {timeUsedServer.readTime().then(dat=>res.send(dat));
    } else if (req.body["action"] == "deldata"){

    }
})


module.exports = router
