const axios = require("axios");
const cheerio = require("cheerio");
const chp = require("child_process");

function doubanOpen(bookname:string) {
    let url = "https://search.douban.com/book/subject_search?search_text=";
    url = "start " + url + bookname;
    chp.exec(url);
}



function getbookInfo(url:string){


}


exports.getbookInfo = getbookInfo
exports.doubanOpen = doubanOpen
