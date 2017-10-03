"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Tabs from '../common/widget.tabs.jsx';
import Form from '../common/widget.form2.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
// import City from '../common/widget.city.jsx';
import DateEditor from '../common/widget.date.jsx';
// import Occupation from '../common/widget.occupation.jsx';
import OccupationPicker from '../common/widget.occupationPicker.jsx';
import CityPicker from '../common/widget.cityPicker.jsx';
import Summary from './summary.jsx';

class PlanForm extends Form {
	form() {
		if (this.props.fields == null)
			return [];
		var company = this.props.company;
		return this.buildForm(this.props.fields.map(function(v) {
			return {name:v.label, code:v.name, company: company, type:v.widget, refresh:"yes", options:v.detail, value:v.value};
		}));
	}
}

var Ware = React.createClass({
	openQuest() {
		common.save("iyb/temp", JSON.stringify(this.refs.plan.val()));
		this.quest();
	},
	quest() {
		this.setState({quest: true, alertQuest: false});
	},
	alertQuest() {
		this.setState({quest: true, alertQuest: true});
	},
	back() {
		this.setState({quest: false, alertQuest: false});
	},
    apply() {
		let plus = window.location.search;
		if (plus != null && plus != "")
			plus = "&" + plus.substring(1);

		common.save("iyb/orderId", "");
		document.location.href = "life_apply.mobile?packId=" + env.packId + plus;
    },
    getInitialState() {
    	let r = {quest:false, alertQuest:false, vendor:{}};
    	let v = this.props.detail;
    	if (v != null && v.detail != null && v.detail.length > 0) {
    		if (v.detail.length > 1)
    			r.packs = v.detail.map(d => [d.code, d.name]);
    		r.detail = v.detail[0];
    		if (r.detail.summary != null) {
    			r.exps = [];
				for (let label in r.detail.summary) {
					r.exps.push([label, label]);
					if (r.summary == null)
	    				r.summary = r.detail.summary[label];
				}
    		}
    	}
    	return r;
    },
    componentDidMount() {
		env.packId = this.state.detail.target;
		common.req("ware/detail.json", {type:1, packId: env.packId}, r => {
			env.docs = r.docs;
			env.vendor = r.vendor;
			env.company = r.vendor.code;
			this.setState({factors:r.factors, vendor:r.vendor, company: r.vendor.code}, () => {this.render();this.refreshPremium();});
		});
    },
    getPlanFactors() {
    	let factors = {packId: this.state.detail.target};
    	let self = this.refs.plan;
    	self.props.fields.map(v => {
    		if (self.refs[v.name]) {
				factors[v.name] = self.refs[v.name].val();
    		}
    	});
    	return factors;
    },
    refreshPremium() {
		let factors = this.refs.plan.val();
		factors.packId = this.state.detail.target;
        if(factors.ZONE != null && factors.ZONE.code != null) {
            factors.ZONE = factors.ZONE.code;
		}else{
            factors.ZONE = null;
		}
		common.req("ware/do/life.json", {platformId:2, opt:"try", content:factors}, r => {
			var factors = this.state.factors;
			if (r.form != null) factors.map(function(e) {
				var res = r.form[e.name];
				if (res != null)
					e.value = res;
			});
			this.setState({premium: r.total, rules: r.rules, vals:r, factors:factors});
		});
	},
    onSummary(code) {
    	this.setState({summary: this.state.detail.summary[code]});
    },
	openPoster() {
		document.location.href = "iyunbao://poster?code=" + env.ware.code;
	},
   	render() {
		if (this.state.quest) {
			return (
				<div className="common">
					<div className="title">健康及财务告知</div>
					<div className="text">
						{ env.docs.quests.map(v => <p>{v}</p>) }
					</div>
					<div className="console">
						<div className="tab">
							<div className="row">
								<div className="col left" onClick={this.alertQuest}>是</div>
								<div className="col right" onClick={this.apply}>全否，继续投保</div>
							</div>
						</div>
					</div>
					{ !this.state.alertQuest ? null :
						<div className="notice">
							<div className="content">
								<br/>
								很抱歉，被保险人的健康状况不满足该保险的投保规定<br/>
								如有疑问，请联系{this.state.vendor.name}客服<br/>
								<br/>
								<a href="tel:{env.docs.telephone}">{env.docs.telephone}</a>
								<div className="console">
									<div className="tab">
										<div className="row">
											<div className="col left" onClick={this.quest}>选错拉</div>
											<div className="col right" onClick={this.back}>返回试算页</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					}
				</div>
			);
		}

		let v = this.props.detail;
		return (
			<div className="common">
				<div>
					<img src={v.banner[0]} style={{width:"100%", height:"auto"}}/>
					<div style={{height:"50px", top:"-50px", position:"relative", paddingTop:"5px", zIndex:"1", textAlign:"center", color:"#FFF", backgroundColor:"rgba(66,66,66,0.7)"}}>
						<div className="font-wl">{this.props.detail.name}</div>
						<div className="font-wm">{this.props.detail.remark}</div>
					</div>
					{ this.state.factors == null ? null :
						<div className="form" style={{marginTop:"-50px"}}>
							<PlanForm ref="plan" parent={this} fields={this.state.factors} company={this.state.company} onRefresh={this.refreshPremium}/>
						</div>
					}
					{ this.state.rules == null ? null :
						<div className="form">
							{ this.state.rules.map(r => (<div className="alert" key={r}>{r}</div>)) }
						</div>
					}
				</div>
				<div className="font-bm" style={{height:"40px", lineHeight:"40px", textAlign:"center", borderTop: "1px solid #CCC", borderBottom: "1px solid #CCC"}}>
					{ this.state.vendor.logo == null ? null : <img src={this.state.vendor.logo} style={{height:"20px",verticalAlign:"middle",marginRight:"10px"}}/> }
					由{this.state.vendor.name}承保并负责理赔
				</div>
				{ this.state.exps == null ? null :
					<Tabs onChange={this.onSummary} options={this.state.exps}/>
				}
				{ this.state.summary == null ? null :
					<Summary content={this.state.summary} vals={this.state.vals == null ? null : this.state.vals.summary}/>
				}
				<div className="console">
					<div className="tab">
						<div className="row">
							{env.frame == "iyb" ? <div className="col rect" onClick={this.openPoster}>海报</div> : null}
							<div className="col left">首年保费：{this.state.premium <= 0 ? "无法计算" : this.state.premium}</div>
							<div className="col right" onClick={this.openQuest}>投保</div>
						</div>
					</div>
				</div>
			</div>
		);
	}
});

env.sharePrd = function() {
	var shareObj = {
		title: env.ware.name,
		desc: env.ware.remark,
		thumb: env.ware.logo,
		imgUrl: env.ware.logo,
		link: window.location.href
	}
	iHealthBridge.doAction("share", JSON.stringify(shareObj));
}

$(document).ready( function() {
	common.req("ware/view.json", {wareId:common.param("wareId")}, function (r) {
		env.ware = r;
		ReactDOM.render(
			<Ware detail={r}/>, document.getElementById("content")
		);

		document.title = r.name;
		if ("undefined" != typeof iHealthBridge) {
			env.frame = "iyb";
			window.iHealthBridge.doAction("setRightButton", JSON.stringify({title: "分享", action: "javascript:env.sharePrd();", color: "#ffffff", font: "17"}));
			window.IYB.setTitle(r.name);
		}

		try {
			initShareInfo();
		} catch (e) {
		}
	});

});