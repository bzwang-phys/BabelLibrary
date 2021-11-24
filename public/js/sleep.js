$(document).ready(function () {

    document.querySelector("#submitInfo").addEventListener('click', ()=>{subInfo()} )
    document.querySelector("#getdata").addEventListener('click', ()=>{getData()} )

    function subInfo() {
        var dataJson = {action : "savedata"};
        let flagWrong = false;
	let data = $("#sleep").serializeArray();
	$.each(data, function(i, item){
	    dataJson[item.name] = timeStrClean(item.value);
            if (!timeStrCheck(dataJson[item.name])) {flagWrong=true}
	})
        if (flagWrong) {return}

        axios.post("/sleep", dataJson).then(res=>{
            if (res.data==true) { alert("保存数据成功"); }
            getData();
        })

    }

    function getData() {
        var dataJson = {action : "getdata"};
        axios.post("/sleep", dataJson).then(res=>{
            data2Lines(res.data);
        })
    }

    function timeStrClean(timestr) {
        let newstr = timestr.replace("：",":").trim();
        let strList = newstr.split(":");
        for (let i=0; i < strList.length; i++){
            if (strList[i].length!=2){
                strList[i] = strList[i].padStart(2,"0");
            }
        }
        return strList.join(":")
    }
    function timeStrCheck(timestr) {
        reg = /^(20|21|22|23|[0-1]\d):[0-5]\d$/;
        regExp = new RegExp(reg);
        if(!regExp.test(timestr)){
            alert("时间格式不正确，正确格式为：08:45");
            return false;
        }
        return true;
    }

    function timeToFloat(timeStr){
        timeArray = timeStr.split(":");
        timeFloat = (parseFloat(timeArray[0])==12?0:parseFloat(timeArray[0])) + parseFloat(timeArray[1])/60;
        return timeFloat;
    }

    function data2Lines(dataArray) {
        sleepData = []
        getupData = []
        dataArray.forEach(dict => {
            sleepTime = timeToFloat(dict["sleepTime"]);
            getupTime = timeToFloat(dict["getupTime"]);
            day = new Date(dict["day"]).getTime();
            
            sleepData.push([day, sleepTime]);
            getupData.push([day, getupTime]);
        });
        dataJson = { "sleepData":sleepData,"getupData":getupData };
        plot(dataJson)
    }

    function plot(data) {
        Highcharts.chart('fig', {
                title: {
                        text: '睡眠时间记录'
                },
                // subtitle: {
                //         text: '数据来源：thesolarfoundation.com'
                // },
                yAxis: {
                        title: { text: '时间'}
                },
                xAxis: {
                    type: "datetime",
                },
                legend: {
                        layout: 'vertical',
                        align: 'right',
                        verticalAlign: 'middle'
                },
                plotOptions: {
                        series: {
                                label: { connectorAllowed: false },
                                pointStart: 2010
                        }
                },
                series: [{
                        name: '入睡时间',
                        data: data["sleepData"]
                }, {
                        name: '起床时间',
                        data: data["getupData"]
                }],
                responsive: {
                        rules: [{
                                condition: {
                                        maxWidth: 500
                                },
                                chartOptions: {
                                        legend: {
                                                layout: 'horizontal',
                                                align: 'center',
                                                verticalAlign: 'bottom'
                                        }
                                }
                        }]
                }
            });
    }
        

    getData();


})