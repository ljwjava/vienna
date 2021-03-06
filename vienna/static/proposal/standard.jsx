"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import TabCard from '../common/component.tabcard.jsx';
import Form from '../common/widget.form2.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import DateEditor from '../common/widget.date.jsx';
import Inputer from '../common/widget.inputer.jsx';

var env = {
	applicant: {gender:"M", age:30},
	insurant: {gender:"M", age:18},
};

env.setCurrency = function(c) {
	if (c == "cny") env.currency = "元";
    else if (c == "hkd") env.currency = "港元";
    else if (c == "twd") env.currency = "新台币";
    else if (c == "jpy") env.currency = "日元";
    else if (c == "usd") env.currency = "美元";
    else env.currency = null;
}

env.style = {
	menu: {
		product: {
			width:"350px"
		}
	}
}

var SimDraw = React.createClass({
	getInitialState() {
		return {sim:[1,2,3,4,5,6,7,8]};
	},
	val() {
		return {SIM:this.state.sim.map(v => {
			let age = parseInt(this.refs["age"+v].value);
			let draw = parseFloat(this.refs["draw"+v].value);
			let amt = parseFloat(this.refs["amt"+v].value);
			let prm = parseFloat(this.refs["apd"+v].value);
			if (isNaN(age) || (isNaN(draw) && isNaN(amt) && isNaN(prm)))
				return null;
			return {AGE:age, DRAW:draw, AMT:amt, PRM:prm};
		})};
	},
	render() {
		// let list = this.state.sim.map(v => {
		// 	return <div className="form">
		// 		满 <input style={{width:"50px"}} ref={"age"+v}/> 岁时，领取 <input style={{width:"100px"}} ref={"draw"+v}/><input style={{width:"100px", display:"none"}} ref={"amt"+v}/> 元，追加保费 <input style={{width:"100px"}} ref={"apd"+v}/> 元
		// 	</div>
		// });
		// return (<div>{list}</div>)
		let list = this.state.sim.map(v => {
			return <tr>
				<td><input className="form-control" ref={"age"+v}/></td>
				<td><input className="form-control" ref={"apd"+v}/></td>
				<td><input className="form-control" ref={"draw"+v}/></td>
				<td><input className="form-control" ref={"amt"+v}/></td>
			</tr>
		});
		return (<table className="bordered text-center"><thead><th>年龄(周岁)</th><th>追加保费</th><th>部分领取</th><th>调整保额</th></thead><tbody>{list}</tbody></table>);
	}
});

var Console = React.createClass({
	getInitialState() {
		return {clause: [], select: null};
	},
	componentDidMount() {
		common.req("proposal/list_clause.json", {}, r => {
			this.setState({clause: r});
		});
	},
    setVendor(vendorId) {
		this.setState({select: vendorId});
    },
	addProduct(productId) {
		common.req("proposal/plan/clause.json", {productId:productId, planId:this.props.plan.planId}, r => {
			this.props.refresh(r);
		});
	},
	render() {
        var vendors = {};
        var clauses = this.state.clause.map(v => {
        	if (this.state.select == null)
        		this.state.select = v.company;
            vendors[v.company] = v.icon;
            return v.company != this.state.select ? null : <div key={v.id} style={{padding: "10px 10px 10px 20px", whiteSpace: "nowrap"}} onClick={this.addProduct.bind(this, v.id)}>
				<div style={{fontSize: "12pt", color: "#000", height:"25px"}}>{v.name}</div>
				<div style={{fontSize: "10pt", color: "#AAA", height:"20px"}}>{v.remark}</div>
			</div>;
        });
        var vr = [];
        for (var v in vendors) {
            vr.push(<div onMouseEnter={this.setVendor.bind(this, v)}><img src={vendors[v]} style={{width:"50px", height:"50px", margin:"5px 10px 5px 10px"}}/><span style={{height:"50px", borderRight: this.state.select == v ? "6px solid #0AF" : "none"}}></span></div>);
        }
		return (
			<nav className="navbar navbar-expand-lg navbar-light bg-light">
				<div className="collapse navbar-collapse">
					<div className="dropdown mr-4">
						<button className="btn btn-primary dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">添加产品</button>
						<table className="dropdown-menu" style={{width:"440px"}}>
							<tbody>
								<tr>
									<td style={{width:"80px", verticalAlign:"top", borderRight:"1px solid #EEE"}}>{vr}</td>
									<td style={{verticalAlign:"top"}}>{clauses}</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div className="collapse navbar-collapse mr-auto">
                        { this.props.plan.applicant == null ? null : <Customer tag="applicant" show="投保人" plan={this.props.plan} val={this.props.plan.applicant} refresh={this.props.refresh}/> }
                        { this.props.plan.insurant == null ? null : <Customer tag="insurant" show="被保险人" plan={this.props.plan} val={this.props.plan.insurant} refresh={this.props.refresh}/> }
					</div>
					<button type="button" className="btn btn-success mr-1" onClick={this.props.parent.save}>保存</button>
					<button type="button" className="btn btn-success" onClick={this.props.parent.apply}>投保</button>
				</div>
			</nav>
		);
	}
});

var Customer = React.createClass({
	ages: [0,1,2,3,4,5,6,7,8,9,10,15,18,20,25,30,35,40,45,50,55,60,65,70],
	setAge(age) {
		this.props.val.age = age;
		this.refresh();
	},
    addAge(v) {
        this.props.val.age += v;
        this.refresh();
    },
	setGender(code) {
		this.props.val.gender = code;
		this.refresh();
	},
	refresh() {
		let req = {proposalId:env.proposalId, planId:this.props.plan.planId};
		this.props.val.birthday = null;
		req[this.props.tag] = this.props.val;
		common.req("proposal/" + this.props.tag + ".json", req, r => {
			this.props.refresh();
		});
	},
	render() {
		let ages = this.ages.map(v => (<li className="dropdown-item" key={v} onClick={this.setAge.bind(this, v)}>{v}</li>));
		return (
			<div>
				<div className="collapse navbar-collapse mr-4">
					<div><a href="#" style={{color:"#AAA"}}>{this.props.show}</a></div>
					<div className="ml-2 btn-group mr-2">
						<button className={this.props.val.gender=="M"?"btn btn-success":"btn border btn-light"} onClick={this.setGender.bind(this, "M")}>男</button>
						<button className={this.props.val.gender=="F"?"btn btn-success":"btn border btn-light"} onClick={this.setGender.bind(this, "F")}>女</button>
					</div>
					<div className="dropdown mr-2">
						<button className="btn btn-primary dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">{this.props.val.age}周岁</button>
						<ul className="dropdown-menu">{ages}</ul>
					</div>
					<div>
						<div onClick={this.addAge.bind(this, 1)} style={{lineHeight:"26px"}}>▲</div>
						<div onClick={this.addAge.bind(this, -1)} style={{lineHeight:"26px"}}>▼</div>
					</div>
				</div>
			</div>
		);
	}
});

class ClauseForm extends Form {
	form() {
		if (this.props.fields == null)
			return [];
		return this.buildForm(this.props.fields.map(function(v) {
			return {name:v.label, code:v.name, type:v.widget, refresh:"yes", options:v.detail, value:v.value};
		}));
	}
}

var ClauseWindow = React.createClass({
	getInitialState() {
		return {clause:{}, mode:1, index:-1};
	},
	save() {
		let param = {index:this.state.index, planId:this.props.planId};
		param.detail = this.refs.form.val();
		common.req("proposal/plan/clause.json", param, r => {
			this.props.parent.props.refresh(r);
		});
	},
	render() {
		return (
			<div className="modal fade" id="clauseEdit" tabIndex="-1" role="dialog" aria-hidden="true">
				<div className="modal-dialog modal-lg">
					<div className="modal-content">
						<div className="modal-header">
							<h5 className="modal-title">{this.state.clause.name}</h5>
							<button type="button" className="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div className="modal-body">
							<div className="form">
								{this.state.mode == 1 ? <ClauseForm ref="form" fields={this.state.clause.factors}/> : <SimDraw ref="form"/>}
							</div>
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.save}>确定</button>
							<button type="button" className="btn btn-default" data-dismiss="modal">取消</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
});

var Plan = React.createClass({
	getInitialState() {
		return {child:false, riders:[]};
	},
	edit(i) {
		common.req("proposal/plan/view_clause.json", {index:i, planId:this.props.plan.planId}, r => {
			this.refs.win.setState({clause:r, index:i, mode:1});
		});
	},
	adjust(i) {
		common.req("proposal/plan/view_clause.json", {index:i, planId:this.props.plan.planId}, r => {
			this.refs.win.setState({clause:r, index:i, mode:2});
		});
	},
	remove(i) {
		common.req("proposal/plan/remove_clause.json", {index:i, planId:this.props.plan.planId}, r => {
			this.props.refresh(r);
		});
	},
	openChild(i) {
		this.setState({child:!this.state.child});
	},
	addRider(i, riderId) {
		if (riderId != null && riderId != "") {
			common.req("proposal/plan/clause.json", {productId: riderId, parentIndex: i, planId: this.props.plan.planId}, r => {this.props.refresh(r);});
		}
	},
	showRiders(i) {
		common.req("proposal/plan/list_clause.json", {parentIndex:i, planId:this.props.plan.planId}, r => {
			if (r == null || r.length == 0) r = [{text:"无可用产品"}];
			let v = {};
			v[i] = r.map(j => {
				return (<li key={j.id} className="dropdown-item" style={{padding:"5px 16px 5px 16px"}} onClick={this.addRider.bind(this, i, j.id)}>{j.name}</li>);
			});
			this.setState({riders:v});
		});
	},
	render() {
		let list = [];
		if (this.props.plan.product != null) {
			let i = -1;
			list = this.props.plan.product.map(v => {
				i++;
				let append = null;
				if (v.children != null) {
					//append = [<a key="0" onClick={this.openChild.bind(this, i)}>{this.state.child ? "▲" : "▼"}</a>];
					// if (this.state.child) v.children.map(v => {
					// 	append.push(<span key={v.id} style={{fontSize:"12px", lineHeight:"24px", marginLeft:"5px", color:"#888"}}>● {v.name}</span>);
					// });
                    append = v.children.map(v => {
                        return (<span key={v.id} style={{fontSize:"12px", lineHeight:"24px", marginLeft:"5px", color:"#888"}}>● {v.name}</span>);
                    });
                }
				return (
					<tr key={i}>
						<td>{v.parent != null ? null :
							<div className="dropdown">
								<a href="#" onClick={this.showRiders.bind(this, i)} data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
									<img src="../images/add.png" style={{width:"24px", height:"24px"}}/>
								</a>
								<ul className="dropdown-menu" style={{width:"320px"}}>{this.state.riders[i]}</ul>
							</div>}
						</td>
						<td style={{textAlign:"left"}}>
							{v.children != null ? <font color="#F0F">[组合]</font> : v.parent == null ? <font color="#F00">[主险]</font> : <font color="#0A0">[附加]</font>}
							&nbsp;
							<a onClick={this.edit.bind(this, i)} data-toggle="modal" data-target="#clauseEdit">{v.name}</a>
							&nbsp;
							{v.productType=="universal"?(<a onClick={this.adjust.bind(this, i)} data-toggle="modal" data-target="#clauseEdit">[调整领取]</a>):null}
							&nbsp;
							{append}
						</td>
						<td>{v.amount}</td>
						<td>{v.insure}</td>
						<td>{v.pay}</td>
						<td>{v.premium}</td>
						<td>{v.auto ? null: (<a onClick={this.remove.bind(this, i)}><img src="../images/delete.png" style={{width:"24px", height:"24px"}}/></a>)}</td>
					</tr>
				);
			});
		}

		return (
			<div className="navbar">
				<ClauseWindow ref="win" parent={this} planId={this.props.plan.planId}/>
				<table className="table table-bordered">
					<thead className="thead-light">
						<tr>
							<th style={{width:"40px"}}></th>
							<th>产品名称</th>
							<th>保额/份数/档次</th>
							<th>保障期间</th>
							<th>缴费期间</th>
							<th>保费{env.currency}</th>
							<th style={{width:"40px"}}></th>
						</tr>
					</thead>
					<tbody>
						{list}
					</tbody>
				</table>
			</div>
		);
	}
});

var Main = React.createClass({
	getInitialState() {
		return {plan:{}, mode:null, show:null};
	},
    save() {
        common.req("proposal/save.json", {proposalId:env.proposalId}, r => {
            alert("已保存");
        });
    },
    apply() {
        common.req("proposal/apply.json", {proposalId:env.proposalId}, r => {
            common.req("order/save.json", {orderId:r.id}, r => {
                alert(r);
            });
        });
    },
	componentWillMount() {
		this.refresh();
	},
	refresh(r) {
        if (r != null) {
            if (r.product != null && r.product.length > 0)
                env.setCurrency(r.product[0].currency);
            this.setState({plan: r});
        } else common.req("proposal/plan/edit.json", {planId: this.props.planId}, r => {
            if (r.product != null && r.product.length > 0)
                env.setCurrency(r.product[0].currency);
			this.setState({plan: r});
		});
	},
	doEvent(x) {
		if (x == "7") {
			common.req("proposal/fee.json", {proposalId: env.proposalId}, r => {
				this.setState({mode:x, show:r, type:"json"});
			});
		} else if (x == "1") {
			common.req("proposal/plan/show_list.json", {planId: this.props.planId, type: "liability"}, r => {
				this.setState({mode:x, show:r, type:"liability"});
			});
		} else if (x == "2") {
			common.req("proposal/plan/show_list.json", {planId: this.props.planId, type: "table"}, r => {
				this.setState({mode:x, show:r, type:"table"});
			});
		} else if (x == "3") {
			common.req("proposal/plan/show_list.json", {planId: this.props.planId, type: "chart"}, r => {
				this.setState({mode:x, show:r, type:"chart"});
			});
		} else if (x == "4") {
			common.req("proposal/plan/overview.json", {planId: this.props.planId}, r => {
				this.setState({mode:x, show:r, type:"overview"});
			});
		} else if (x == "5") {
			common.req("proposal/print.json", {proposalId: env.proposalId}, r => {
				this.setState({mode:x, show:null, type:"preview"}, () => {
					printer.preview("iyb_proposal", "preview", r);
				});
			});
        } else if (x == "6") {
            common.req("proposal/plan/quest/merge.json", {planId: this.props.planId}, r => {
                this.setState({mode:x, show:r, type:"mergeQuest"});
            });
        } else if (x == "8") {
            common.req("proposal/plan/format.json", {planId: this.props.planId, style: "benefit"}, r => {
                this.setState({mode:x, show:r.benefit, type:"benefit2"});
            });
		} else {
			this.setState({mode:x, show:null, type:"text"});
		}
	},
	render() {
		var bar = [["1","保险责任"],["2","利益表"],["3","利益图"],["5","建议书预览"],["4","利益总览"],["6","健康告知"],["7","费用"],["8","合并图"]];
		var barList = bar.map(r => {
			return (<button key={r[0]} className={this.state.mode == r[0] ? "btn btn-success" : "btn border btn-light"} onClick={this.doEvent.bind(this, r[0])}>{r[1]}</button>);
		});
		return (
			<div className="mt-3">
				<Console plan={this.state.plan} refresh={this.refresh} parent={this}/>
				<Plan plan={this.state.plan} refresh={this.refresh}/>
				<div className="navbar navbar-expand-lg navbar-light bg-light">
					<div className="btn-group mr-auto">{barList}</div>
					合计保费：{this.state.plan.premium} {env.currency}
				</div>
				<Show plan={this.state.plan} content={this.state.show} type={this.state.type}/>
			</div>
		);
	}
});

var Show = React.createClass({
	getInitialState() {
		return {};
	},
	showTable(i) {
		common.req("proposal/plan/show.json", {planId: this.props.plan.planId, type: "table", index:i}, r => {
			this.setState({select:i, content:r, type:"table"});
		});
	},
	showChart(i) {
        common.req("proposal/plan/show.json", {planId: this.props.plan.planId, type: "chart", index:i}, r => {
            this.setState({select:i, content:r, type:"chart"}, () => {
                for (var x in r) {
                    var cc = r[x];
                    var graph = document.getElementById("chart");
                    graph.style.height = graph.offsetWidth / 2;
                    var chart = echarts.init(graph, 'macarons');
                    var option = {
                        tooltip : {
                            trigger: 'axis'
                        },
                        calculable : true,
                        legend: {
                            data: cc.sets
                        },
                        xAxis : [{
                            type : 'category',
                            data : cc.axis
                        }],
                        yAxis : [{
                            type : 'value',
                            name : '金额',
                            axisLabel : { formatter: '{value}' }
                        }],
                        series : cc.data
                    };
                    chart.setOption(option);
                }
            });
        });
    },
    showBenefit(i) {
        this.setState({select: i, content: r, type: "chart"}, this.drawChart.bind(this, r));
    },
    drawChart(cc) {
		var graph = document.getElementById("chart");
		graph.style.height = graph.offsetWidth / 2;
		var chart = echarts.init(graph, 'macarons');
		var data = [];
		for (var i=0;i<cc.title.length;i++)
            data.push({data: cc.value[i], type: "line", name: cc.title[i]});
		var option = {
			tooltip : {
				trigger: 'axis'
			},
			calculable : true,
			legend: {
				data: cc.title
			},
			xAxis : [{
				type : 'category',
				data : cc.axis
			}],
			yAxis : [{
				type : 'value',
				name : '金额',
				axisLabel : { formatter: '{value}' }
			}],
			series : data
		};
		chart.setOption(option);
    },
    drawTable(cc, i) {
		this.setState({select: i, content: cc, type: "benefit2"});
    },
	showLiability(i) {
		common.req("proposal/plan/show.json", {planId: this.props.plan.planId, type: "liability", index:i}, r => {
			this.setState({select:i, content:r, type:"liability"});
		});
	},
	buildConsole(func) {
		let prds = null;
		if (this.props.content.length > 0) {
			prds = this.props.content.map(v => <button className={this.state.select == v ? "btn btn-success" : "btn border btn-light"} onClick={func.bind(this, v)}>{this.props.plan.product[v].name}</button>);
			if (this.state.content == null || this.state.type != this.props.type)
				func(this.props.content[0]);
		}
		return <div key="tabs" className="navbar navbar-expand-lg navbar-light bg-light btn-group">{prds}</div>;
	},
	render() {
		let r = null;
		if (this.props.type == "table") {
			r = [this.buildConsole(this.showTable)];
			if (this.state.content != null && this.state.type == this.props.type) {
				let v = this.state.content;
				for (let t1 in v) {
					let tables = v[t1].map(t2 => {
						if (t2.type != "table") return null;
						let head = t2.head.map(t3 => {
							let row = t3.map(t4 => {
								if (t4 == null) return null;
								return (<th colSpan={t4.col} rowSpan={t4.row}>{t4.text}</th>);
							});
							return (<tr>{row}</tr>);
						});
						let body = t2.body.map(t3 => {
							let row = t3.map(t4 => {
								return (<td>{t4}</td>);
							});
							return (<tr style={{textAlign:"right"}}>{row}</tr>);
						});
						return (<table className="table table-bordered"><thead className="thead-light">{head}</thead><tbody>{body}</tbody></table>);
					});
					r.push(<div>{tables}</div>);
				}
			}
		} else if (this.props.type == "chart") {
			r = [this.buildConsole(this.showChart)];
			if (this.state.content != null && this.state.type == this.props.type) {
				r.push(<div><div id="chart"></div></div>);
			}
        } else if (this.props.type == "benefit") {
			r = [<div className="navbar navbar-expand-lg navbar-light bg-light btn-group">
				{this.props.content.map((v, i) => <button className={this.state.select == i ? "btn btn-warning" : "btn border btn-light"} onClick={this.drawChart.bind(this, v, i)}>{v.name}</button>)}
			</div>];
			r.push(<div><div id="chart"></div></div>);
        } else if (this.props.type == "benefit2") {
            r = [<div className="navbar navbar-expand-lg navbar-light bg-light btn-group">
				{this.props.content.map((v, i) => <button className={this.state.select == i ? "btn btn-warning" : "btn border btn-light"} onClick={this.drawTable.bind(this, v, i)}>{v.name}</button>)}
			</div>];
            if (this.state.content != null && this.state.type == this.props.type) {
                let v = this.state.content;
				let head = v.title.map(t4 => <th>{t4}</th>);
				let s = v.value[0].length;
				let x = [];
				for (let i=0;i<s;i++) {
                    let y = [<td>{i}</td>, <td>{v.axis[i]}</td>];
                    for (let j=0;j<v.value.length;j++) {
                    	y.push(<td style={{textAlign:"right"}}>{Number(v.value[j][i]).toFixed(2)}</td>);
                    }
                    x.push(<tr>{y}</tr>);
				}
				r.push(<table className="table table-bordered"><thead className="thead-light"><tr><th>保单年度</th><th>年龄</th>{head}</tr></thead><tbody>{x}</tbody></table>);
            }
		} else if (this.props.type == "preview") {
			r = <div key="preview" id="preview" style={{backgroundColor:"#AAA", paddingTop:"10px", paddingBottom:"10px"}}></div>;
		} else if (this.props.type == "liability") {
			r = [this.buildConsole(this.showLiability)];
			if (this.state.content != null && this.state.type == this.props.type) {
				for (var e1 in this.state.content) {
					r.push(<fieldset>
						<legend>{e1}</legend>
						{ this.state.content[e1].map(e2 => (<div>
							<b>{e2.title}</b>
							{ e2.content.map(e3 => {
								if (e3.type == "text") {
									return (<div>{e3.text.replace(new RegExp("[\n]", "gm"), "<br/>")}</div>);
								} else if (e3.type == "table") {
									let head = e3.head.map(t3 => {
										let row = t3.map(t4 => {
											if (t4 == null) return null;
											return (<th colSpan={t4.col} rowSpan={t4.row}>{t4.text}</th>);
										});
										return (<tr>{row}</tr>);
									});
									let body = e3.body.map(t3 => {
										let row = t3.map(t4 => {
											return (<td>{t4}</td>);
										});
										return (<tr style={{textAlign:"center"}}>{row}</tr>);
									});
									return (<div style={{padding:"10px"}}><table className="tableB"><thead>{head}</thead><tbody>{body}</tbody></table></div>);
								}
							})}
							<br/>
						</div>))}
						<br/>
					</fieldset>);
				}
			}
		} else {
			var str;
			if (this.props.content != null) {
				str = common.formatJson(JSON.stringify(this.props.content));
			}
			r = <pre>{str}</pre>;
		}
		return <div id="show">{r}</div>;
	}
});

function render(planId) {
	ReactDOM.render(<Main planId={planId}/>, document.getElementById("content"));
}

$(document).ready( function() {
	env.proposalId = common.param("proposalId");
	if (env.proposalId == null || env.proposalId == "") {
		common.req("proposal/create.json", {applicant:env.applicant, insurants:[env.insurant]}, r => {
			env.proposalId = r.proposalId;
			render(r.detail[0]);
		});
	} else {
		common.req("proposal/load.json", {proposalId:env.proposalId}, r => {
			env.applicant = r.applicant;
			if (r.detail == null || r.detail.length == 0) {
				common.req("proposal/create_plan.json", {insurants:[env.insurant]}, r => {
					render(r.detail[0]);
				});
			} else {
				render(r.detail[0]);
			}
		});
	}
});
