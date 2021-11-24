

const getData = async () => {
    // const res = await fetch("https://reqres.in/api/users?page=2");
    const res = await fetch("https://baidu.com");
    console.log(res);
}

const sendData = async () => {
    const res = await fetch("https://reqres.in/api/users?page=2");
    console.log(res);
}


const getBtn = document.getElementById("get-btn");
const postBtn = document.getElementById("post-btn");
getBtn.addEventListener('click', getData);
postBtn.addEventListener('click', sendData);