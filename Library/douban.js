"use strict";
var axios = require("axios");
var cheerio = require("cheerio");
var chp = require("child_process");
function doubanOpen(bookname) {
    var url = "https://search.douban.com/book/subject_search?search_text=";
    url = "start " + url + bookname;
    chp.exec(url);
}
function getbookInfo(url) {
}
exports.getbookInfo = getbookInfo;
exports.doubanOpen = doubanOpen;
