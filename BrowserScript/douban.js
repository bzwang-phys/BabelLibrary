"use strict";
// ==UserScript==
// @name         BabelLibrary Douban Plug-in
// @namespace    http://tampermonkey.net/
// @version      0.1
// @match        https://douban.com/
// @grant        none
// ==/UserScript==
(function () {
    'use strict';
    function getBookInfo() {
        var _a;
        var bookinfos = {};
        var info = document.querySelector("#info");
        var authorNodes = info.querySelectorAll("span > span");
        authorNodes.forEach(function (node) {
            var nodeKey = String(node.textContent);
            if (nodeKey.indexOf("作者") != -1) {
                var temp = node.nextElementSibling;
                var value = "";
                while (temp != null) {
                    value += (temp.textContent + " ");
                    temp = temp.nextElementSibling;
                }
                bookinfos["author"] = value;
            }
            else if (nodeKey.indexOf("译者") != -1) {
                var temp = node.nextElementSibling;
                var value = "";
                while (temp != null) {
                    value += (temp.textContent + " ");
                    temp = temp.nextElementSibling;
                }
                bookinfos["translator"] = value;
            }
        });
        bookinfos["title"] = String((_a = document.querySelector("h1>span")) === null || _a === void 0 ? void 0 : _a.textContent);
        var items = info.querySelectorAll("span.pl");
        items.forEach(function (item) {
            var key = String(item.textContent);
            if (key.indexOf("出版社") != -1) {
                bookinfos["publisher"] = getContentBetweenElements(item, item);
            }
            else if (key.indexOf("出品方") != -1) {
                bookinfos["producer"] = getContentBetweenElements(item, item);
            }
            else if (key.indexOf("副标题") != -1) {
                bookinfos["subtitle"] = getContentBetweenElements(item, item);
            }
            else if (key.indexOf("原作名") != -1) {
                bookinfos["origintitle"] = getContentBetweenElements(item, item);
            }
            else if (key.indexOf("出版年") != -1) {
                bookinfos["year"] = getContentBetweenElements(item, item);
            }
            else if (key.indexOf("页数") != -1) {
                bookinfos["pages"] = getContentBetweenElements(item, item);
            }
            else if (key.indexOf("丛书") != -1) {
                bookinfos["series"] = getContentBetweenElements(item, item);
            }
            else if (key.indexOf("ISBN") != -1) {
                bookinfos["isbn"] = getContentBetweenElements(item, item);
            }
        });
        return bookinfos;
    }
    function getContentBetweenElements(element1, element2) {
        var value = "";
        var temp = element1 === null || element1 === void 0 ? void 0 : element1.nextSibling;
        while (String(temp === null || temp === void 0 ? void 0 : temp.nodeName) != element2.nodeName && temp != null && temp != undefined) {
            value = value + (temp === null || temp === void 0 ? void 0 : temp.textContent);
            temp = temp === null || temp === void 0 ? void 0 : temp.nextSibling;
        }
        value = value.replace(/[\~|\`|\!|\@|\#|\$|\%|\^|\&|\*|\=|\||\\|\{|\}|\;|\:|\”|\’|\,|\<|\>|\/|\?]/g, "");
        value = value.trim();
        return value;
    }
    var subInfo = "<div href='javascript:void(0)' target='_blank' id='download' style='cursor:pointer;z-index:98;display:block;width:30px;height:30px;line-height:30px;position:fixed;left:0;top:300px;text-align:center;overflow:visible'><img src='https://cdn.80note.com/vip.gif' height='55' ></div>";
})();
