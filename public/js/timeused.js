$(document).ready(function () {
    document.querySelector("#submitInfo").addEventListener('click', ()=>{subInfo()} )
    document.querySelector("#getdata").addEventListener('click', ()=>{getData()} )

    
    function subInfo() {
        var dataJson = {action : "savedata"};
        let data = $("#timeused").serializeArray();
        let flagWrong = false;
        $.each(data, function(i, item){
            if(item.value != ""){
                if (isNaN(Number(item.value))) {alert("不能填入非数字类的值"); flagWrong=true;}
                dataJson[item.name] = item.value;
            }
        })
        if (flagWrong) { return; }
        axios.post("/timeused", dataJson).then(res=>{
            if (res.data==true) { alert("保存数据成功"); }
            getData();
        })

    }



    function getData() {
        var dataJson = {action : "getdata"};
        axios.post("/timeused", dataJson).then(res=>{
            data2Lines(res.data);
        })

    }

    function data2Lines(dataArray) {
        math=[]; phys=[]; ee=[]; cs=[];
        lang=[]; read=[]; economics=[]; sum=[];
        dataArray.forEach(dict => {
            day = new Date(dict["date"]).getTime();
            if ("math" in dict) { math.push([day, dict["math"]]);}
            if ("EE" in dict) { ee.push([day, dict["EE"]]);}
            if ("CS" in dict) { cs.push([day, dict["CS"]]);}
            if ("Physics" in dict) { phys.push([day, dict["Physics"]]);}
            if ("Language" in dict) { lang.push([day, dict["Language"]]);}
            if ("Reading" in dict) { read.push([day, dict["Reading"]]);}
            if ("Economics" in dict) { economics.push([day, dict["Economics"]]);}
            sum.push([day, dict["Sum"]]);
        });
        dataJson = {"math":math,"phys":phys,"ee":ee,"cs":cs,
                    "lang":lang,"read":read,"economic":economics,"sum":sum};
        plot(dataJson)
    }

    function plot(data) {
        Highcharts.chart('fig1', {
            title: {text: '时间使用记录'},
        // subtitle: {
        //         text: '数据来源：thesolarfoundation.com'
        // },
            yAxis: {title: { text: '时间'} },
            xAxis: {type: "datetime" },
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
            series: [{name: 'Physics', data: dataJson['phys']}, 
                    {name: 'Math', data: dataJson['math']}, 
                    {name: 'EE', data: dataJson['ee']},
                    {name: 'CS', data: dataJson['cs']},
        ],
        responsive: {rules: [{condition: {maxWidth: 500},
                chartOptions: {
                        legend: {
                            layout: 'horizontal',
                            align: 'center',
                            verticalAlign: 'bottom'}
                        }
                }]
        }
            });
            
        
        Highcharts.chart('fig2', {
        title: {text: '时间使用记录'},
        // subtitle: {
        //         text: '数据来源：thesolarfoundation.com'
        // },
        yAxis: {title: { text: '时间'} },
        xAxis: {type: "datetime" },
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
        series: [{name: 'Language', data: dataJson['lang']}, 
                {name: 'Reading', data: dataJson['read']}, 
                {name: 'Economics', data: dataJson['economic']}
        ],
        responsive: {rules: [{condition: {maxWidth: 500},
                        chartOptions: {
                                legend: {
                                        layout: 'horizontal',
                                        align: 'center',
                                        verticalAlign: 'bottom'}
                                }
                }]}
        }); 


        Highcharts.chart('fig3', {
                title: {text: '时间使用记录'},
                // subtitle: {
                //         text: '数据来源：thesolarfoundation.com'
                // },
                yAxis: {title: { text: '时间'} },
                xAxis: {type: "datetime" },
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
                series: [{name: 'Sum', data: dataJson['sum']}
                ],
                responsive: {rules: [{condition: {maxWidth: 500},
                                chartOptions: {
                                        legend: {
                                                layout: 'horizontal',
                                                align: 'center',
                                                verticalAlign: 'bottom'}
                                        }
                        }]}
                }); 
    }

    getData();

})