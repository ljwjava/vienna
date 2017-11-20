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
			return <tr style={{textAlign:"center"}}>
				<td><input style={{width:"60px"}} ref={"age"+v}/> 周岁</td>
				<td><input style={{width:"120px"}} ref={"apd"+v}/> 元</td>
				<td><input style={{width:"120px"}} ref={"draw"+v}/> 元</td>
				<td><input style={{width:"120px"}} ref={"amt"+v}/> 元</td>
			</tr>
		});
		return (<table className="bordered"><thead><th>年龄</th><th>追加保费</th><th>部分领取</th><th>调整保额</th></thead><tbody>{list}</tbody></table>);
	}
});

var Console = React.createClass({
	getInitialState() {
		return {};
	},
	componentDidMount() {
		common.req("proposal/list_clause.json", {}, r => {
			this.setState({clauses: r.map(v => (
				<li key={v.id}>
					<table style={{margin:"10px 24px 10px 10px"}}>
						<tbody onClick={this.addProduct.bind(this, v.id)} style={{whiteSpace:"nowrap"}}>
							<tr>
								<td rowSpan="2" style={{width:"60px"}}><img src={common.link(v.logo == null ? "images/logo/iyb.png" : v.logo)} style={{width:"50px", height:"50px"}}/></td>
								<td>
									<span>{v.name}</span>&nbsp;
									<span style={{color:"#F00"}}>{v.tag}</span>
								</td>
							</tr>
							<tr><td style={{fontSize:"10px", color:"#AAA"}}>{v.remark}</td></tr>
						</tbody>
					</table>
				</li>
			))});
		});
	},
	addProduct(productId) {
		common.req("proposal/plan/clause.json", {productId:productId, planId:this.props.plan.planId}, r => {
			this.props.refresh(r);
		});
	},
	render() {
		let list = [];
		if (this.props.plan.applicant != null)
			list.push(<Customer key="1" tag="applicant" show="投保人" plan={this.props.plan} val={this.props.plan.applicant} refresh={this.props.refresh}/>);
		if (this.props.plan.insurant != null)
			list.push(<Customer key="2" tag="insurant" show="被保险人" plan={this.props.plan} val={this.props.plan.insurant} refresh={this.props.refresh}/>);
		return (
			<nav className="navbar navbar-default">
				<div className="container-fluid">
					<div className="collapse navbar-collapse">
						{list}
						<ul className="nav navbar-nav navbar-right">
							<li className="dropdown">
								<a href="#" className="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
									添加产品 <span className="caret"></span>
								</a>
								<ul className="dropdown-menu" style={{width:"auto"}}>{this.state.clauses}</ul>
							</li>
						</ul>
					</div>
				</div>
			</nav>
		);
	}
});

var OptionBar = React.createClass({
	save() {
		common.req("proposal/save.json", {proposalId:env.proposalId}, r => {
			alert("已保存");
		});
	},
	apply() {
		common.req("proposal/apply.json", {proposalId:env.proposalId}, r => {
			console.log(r);
			common.req("order/save.json", {orderId:r.id}, r => {
				alert(r);
			});
		});
	},
	render() {
		return (
			<div className="container-fluid">
				<div className="collapse navbar-collapse">
					<div className="nav navbar-nav">
						<button type="button" className="btn btn-primary" onClick={this.save}>保存</button>
						<button type="button" className="btn btn-primary" onClick={this.apply}>在线投保</button>
					</div>
					<div className="nav navbar-nav navbar-right">
						{this.props.plan.premium}
					</div>
				</div>
			</div>
		);
	}
});

var Customer = React.createClass({
	ages: [0,1,2,3,4,5,6,7,8,9,10,15,18,20,25,30,40,50,60,70],
	setAge(age) {
		this.props.val.age = age;
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
		let ages = this.ages.map(v => (<li key={v}><a onClick={this.setAge.bind(this, v)}>{v}</a></li>));
		return (
			<ul className="nav navbar-nav">
				<li><a href="#" style={{color:"#AAA"}}>{this.props.show}</a></li>
				<li className={this.props.val.gender=="M"?"active":null}><a onClick={this.setGender.bind(this, "M")}>男</a></li>
				<li className={this.props.val.gender=="F"?"active":null}><a onClick={this.setGender.bind(this, "F")}>女</a></li>
				<li className="dropdown">
					<a href="#" className="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						{this.props.val.age}周岁 <span className="caret"></span>
					</a>
					<ul className="dropdown-menu">{ages}</ul>
				</li>
			</ul>
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
			<div className="modal fade" id="clauseEdit" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div className="modal-dialog">
					<div className="modal-content">
						<div className="modal-header">
							<button type="button" className="close" data-dismiss="modal" aria-hidden="true">
								&times;
							</button>
							<h4 className="modal-title" id="myModalLabel">
								{this.state.clause.name}
							</h4>
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
				return (<li key={j.code} style={{padding:"5px 16px 5px 16px"}} onClick={this.addRider.bind(this, i, j.code)}>{j.text}</li>);
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
					append = [<a onClick={this.openChild.bind(this, i)}>{this.state.child ? "▲" : "▼"}</a>];
					if (this.state.child) v.children.map(v => {
						append.push(<div style={{fontSize:"12px", lineHeight:"24px", marginLeft:"5px"}}>● {v.name}</div>);
					});
				}
				return (
					<tr key={i}>
						<td>{v.parent != null ? null :
							<div className="dropdown">
								<a href="#" onClick={this.showRiders.bind(this, i)} className="dropdown-toggle"
								   data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
									<img src="images/add.png" style={{width:"30px", height:"30px"}}/>
								</a>
								<ul className="dropdown-menu" style={{width:"320px"}}>{this.state.riders[i]}</ul>
							</div>}
						</td>
						<td>
							{v.parent == null ? <font color="#F00">[主]</font> : <font color="#0A0">[附]</font>}
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
						<td>
							{v.auto ? null: (<a onClick={this.remove.bind(this, i)}><img src="images/delete.png" style={{width:"30px", height:"30px"}}/></a>)}
						</td>
					</tr>
				);
			});
		}

		return (
			<div>
				<table className="bordered">
					<thead>
						<tr>
							<th style={{width:"4%"}}></th>
							<th style={{width:"37%"}}>产品名称</th>
							<th style={{width:"15%"}}>保额/份数/档次</th>
							<th style={{width:"10%"}}>保障期间</th>
							<th style={{width:"10%"}}>缴费期间</th>
							<th style={{width:"10%"}}>保费(元)</th>
							<th style={{width:"9%"}}></th>
						</tr>
					</thead>
					<tbody>
						{list}
					</tbody>
				</table>
				<ClauseWindow ref="win" parent={this} planId={this.props.plan.planId}/>
			</div>
		);
	}
});

var Main = React.createClass({
	getInitialState() {
		return {plan: {}, show: null};
	},
	componentWillMount() {
		this.refresh();
	},
	refresh(r) {
		if (r != null)
			this.setState({plan: r});
		else common.req("proposal/plan/edit.json", {planId: this.props.planId}, r => {
			this.setState({plan: r});
		});
	},
	doEvent(r) {
		if (r == "0") {
			common.req("proposal/fee.json", {proposalId: env.proposalId}, r => {
				this.setState({show:JSON.stringify(r)});
			});
		} else if (r == "1") {
			common.req("proposal/plan/show_list.json", {planId: this.props.planId, type: "liability"}, r => {
				this.setState({show:r, type:"liability"});
			});
		} else if (r == "2") {
			common.req("proposal/plan/show_list.json", {planId: this.props.planId, type: "table"}, r => {
				this.setState({show:r, type:"table"});
			});
		} else if (r == "3") {
			common.req("proposal/plan/show_list.json", {planId: this.props.planId, type: "chart"}, r => {
				this.setState({show:r, type:"chart"});
			});
		} else if (r == "4") {
			common.req("proposal/plan/overview.json", {planId: this.props.planId}, r => {
				this.setState({show:r, type:"overview"});
			});
		} else if (r == "5") {
			common.req("proposal/print.json", {proposalId: env.proposalId}, r => {
				this.setState({type:"preview"}, () => {
					printer.preview("iyb_proposal", "preview", r);
				});
			});
		} else {
			this.setState({show:null});
		}
	},
	render() {
		return (
			<div>
				<br/>
				<Console plan={this.state.plan} refresh={this.refresh}/>
				<Plan plan={this.state.plan} refresh={this.refresh}/>
				<br/>
				<OptionBar plan={this.state.plan} refresh={this.refresh}/>
				<br/>
				<TabCard onChange={this.doEvent} options={[["1","保险责任"],["2","利益表"],["3","利益图"],["4","总览"],["5","预览"],["6","设置"]]}/>
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
							axisLabel : { formatter: '{value}万' }
						}],
						series : cc.data
					};
					chart.setOption(option);
				}
			});
		});
	},
	showLiability(i) {
		common.req("proposal/plan/show.json", {planId: this.props.plan.planId, type: "liability", index:i}, r => {
			this.setState({select:i, content:r, type:"liability"});
		});
	},
	buildConsole(func) {
		let prds = null;
		if (this.props.content.length > 0) {
			prds = this.props.content.map(v => {
				return <li className={this.state.select == v ? "active" : null}><a
					onClick={func.bind(this, v)}>{this.props.plan.product[v].name}</a></li>;
			});
			if (this.state.content == null || this.state.type != this.props.type) {
				func(this.props.content[0]);
			}
		}
		return (
			<nav className="navbar navbar-default" style={{marginTop:"10px"}}>
				<div className="container-fluid">
					<div className="collapse navbar-collapse">
						<ul className="nav navbar-nav">{prds}</ul>
					</div>
				</div>
			</nav>
		);
	},
	render() {
		let r = null;
		if (this.props.type == "table") {
			r = [this.buildConsole(this.showTable)];
			if (this.state.content != null && this.state.type == this.props.type) {
				let v = this.state.content;
				for (let t1 in v) {
					let tables = v[t1].map(t2 => {
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
						return (<table className="bordered"><thead>{head}</thead><tbody>{body}</tbody></table>);
					});
					r.push(<div>{tables}</div>);
				}
			}
		} else if (this.props.type == "chart") {
			r = [this.buildConsole(this.showChart)];
			if (this.state.content != null && this.state.type == this.props.type) {
				r.push(<div><div id="chart"></div></div>);
			}
		} else if (this.props.type == "overview") {
			r = JSON.stringify(this.props.content);
		} else if (this.props.type == "preview") {
			r = <div id="preview" style={{backgroundColor:"#AAA", paddingTop:"10", paddingBottom:"10"}}></div>;
		} else if (this.props.type == "liability") {
			r = [this.buildConsole(this.showLiability)];
			if (this.state.content != null && this.state.type == this.props.type) {
				for (var e1 in this.state.content) {
					r.push(<div>{e1}</div>);
					this.state.content[e1].map(e2 => {
						r.push(<div>{e2.title}</div>);
						e2.content.map(e3 => {
							if (e3.type == "text") {
								r.push(<div>{e3.text.replace(new RegExp("[\n]", "gm"), "<br/>")}</div>);
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
									return (<tr style={{textAlign:"right"}}>{row}</tr>);
								});
								r.push(<table className="bordered"><thead>{head}</thead><tbody>{body}</tbody></table>);
							}
						});
					});
				}
			}
		} else {
			r = JSON.stringify(this.props.content);
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
		common.req("proposal/create.json", {applicant:env.applicant, insurants:[env.insurant], owner:500260001, platformId:2}, r => {
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
