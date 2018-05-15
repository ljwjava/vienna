"use strict";

import React from 'react';

var BenefitCharts = React.createClass({
	getInitialState() {
        return {detail: this.props.valDetail, selfChart: null};
    },
	/** 组件渲染之前 **/
    componentWillMount(){

    },
	/** 组件渲染之后 **/
    componentDidMount() {
    	// console.log(this.refs);
		this.refs.self.style.height = this.refs.self.offsetWidth * 0.7 + "px";
        let myChart = echarts.init(this.refs.self, null, {renderer: 'svg'}); //初始化echarts

        let options = this.resetChartOption(this.state.detail);
        //设置options
        myChart.setOption(options);

        myChart.dispatchAction({
            type:'showTip',
            seriesIndex: 0,
            dataIndex: 0
        });
        this.setState({selfChart: myChart});
    },
    componentDidUpdate() {
        let options = this.resetChartOption(this.props.valDetail);
        this.state.selfChart.clear();
        this.state.selfChart.setOption(options);
        this.state.selfChart.dispatchAction({
            type:'showTip',
            seriesIndex: 0,
            dataIndex: 0
        });
    },
    resetChartOption(data){
    	if(data == null) {
    		return {};
		}
        return {
            color: ['#5bceff','#ffd200','#ff5b5b','#93ff5b'],
            tooltip: {
                show: true,
                trigger: 'axis',
                alwaysShowContent: true,
                confine: true,
                position: function (pos, params, dom, rect, size) {
                    // 鼠标在左侧时 tooltip 显示到右侧，鼠标在右侧时 tooltip 显示到左侧。
                    var obj = {top: 30};
                    if(pos[0] < size.viewSize[0] / 2) {
                    	obj.right = '5%';
					}else{
                    	obj.left = '10%';
					}
                    return obj;
                },
                formatter: function(a,b,c,d,e){
                    let hh = '';
                    if(a != null && a.length > 0){
                        hh = '当被保人' + a[0].axisValue + '时<br>';
                        for(let i = 0; i < a.length; i++){
                            let vv = a[i];
                            hh += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:'+vv.color+';"></span>'+vv.seriesName+': '+vv.value.toFixed(2)+'<br>';
                        }
                    }
                    return hh;
                },
                axisPointer: {
                    type: 'cross',
                    snap: true,
                    axis: 'x',
                    crossStyle: {
                        color: '#999'
                    }
                }
            },
            // 顶部选项卡
            legend: {
                show: false,
                data: data.sets == null ? [] : data.sets
            },
            // 坐标轴位置
            grid: {
            	top: '30px',
                right: '5%'
            },
            // x轴样式
            xAxis: [
                {
                    type: 'category',
                    // 刻线
                    axisLine: {
                        lineStyle:{
                            color: "#a5a5a5"
                        }
                    },
                    // 刻度
                    axisTick:{
                        alignWithLabel: true,
                        lineStyle: {
                            color: '#a5a5a5'
                        }
                    },
                    // 数据
                    data: (data.axis == null ? [] : data.axis),
                    // 选中区域（线条/区域）
                    axisPointer: {
                        type: 'shadow',
                        snap: true,
                        value: 0,   // 初始值
                        status: 'show',
                        label: {
                            show: true,
                            formatter: '当被保人{value}时'
                        },
                        handle: {
                            show: true,
                            size: 30,
                            margin: 40,
                            color: '#5ccfff'
                        }
                    },
                    // 刻度名称
                    axisLabel: {
                        interval: 'auto',
                        showMinLabel: true,
                        showMaxLabel: true,
                        color: "#a5a5a5"
                    }
                }
            ],
            // y轴样式
            yAxis: [
                {
                    name: '万元',
                    type: 'value',
                    axisLine: {
                        lineStyle:{
                            color: "#a5a5a5"
                        }
                    },
                    axisTick:{
                        alignWithLabel: true,
                        lineStyle: {
                            color: '#a5a5a5'
                        }
                    },
                    // 选中区域（线条/区域）
                    axisPointer: {
                        show: false,
                        type: 'line',
                        snap: true,
                        value: 0,   // 初始值
                        status: 'show',
                        label: {
                            show: true,
                            precision: 2,
                            formatter: '{value}'
                        }
                    },
                    axisLabel: {
                        formatter: function(p){return (p/10000);}
                    }
                }
            ],
            series: data.data == null ? [] : data.data
        };
	},
	val() {
		return null;
	},
	text() {
		return null;
	},
	verify() {
		return null;
	},
	change(code) {
    	code = this.refs.chartsGrade.val();
    	console.log(code);
		this.state.value = code; //setState是异步方法
		if (this.props.onChange)
			this.props.onChange(this, code);
		this.setState({value: code});
	},
	render() {
        return (<div style={{backgroundColor: '#ffffff'}}>
					{/*<div className="row">*/}
						<div className="col line" style={{display: '-webkit-box'}}>
							<div className="tab">
								<div className="row">
									<div className="col left" style={{width: "60%"}}>账户价值演示(单位：万元)</div>
									<div className="col right" style={{width: "40%"}}><Selecter ref="chartsGrade" valCode="chartsGrade" onChange={this.change} options={[["0.035","低:保底3.5%"],["0.045","中:4.5%"],["0.053","高:5.3%"]]} value={0.053}/></div>
								</div>
							</div>
						</div>
					{/*</div>*/}
					<div ref="self" style={{width: "100%", height: "200px"}}></div>
					<div className="benefitChartsDesc">
						温馨提示：首年追加保费能极大提升本产品账户价值，若您选择4000元计划，并首次追加20万，按现有利率5.3%，从0岁开始投保，您的万能账户价值将比不追加提高70万。追加越多价值越高
					</div>
				</div>);
	}
});

module.exports = BenefitCharts;

