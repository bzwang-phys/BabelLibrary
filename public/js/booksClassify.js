$(document).ready(function(){


$("#openDouban").click( ()=>{douban("openDouban")} );
$("#getDouban").click( ()=>{douban("getBookInfo")} );


function douban(actionStr){
	var dataJson = {};
	let data = $("#douban").serializeArray();
	$.each(data, function(i, item){
		dataJson[item.name] = item.value;
	})
	
	axios.post("/booksClassify", {
		action: actionStr, 
		bookPath: dataJson["bookPath"],
		doubanUrl: dataJson["doubanUrl"]
	})
	.then((response) => {
		console.log(response);
	})
}

	
	
	
	
	
})