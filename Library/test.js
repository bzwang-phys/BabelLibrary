import fetch from 'node-fetch';

let urls = [
    'https://www.baidu.com',
    'https://weibo.com',
    'https://google.com'
];


const res = await fetch(urls[0]);

console.log(res)

