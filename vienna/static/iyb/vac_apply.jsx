"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Inputer from '../common/widget.inputer.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import CertEditor from '../common/widget.cert.jsx';
import DateEditor from '../common/widget.date.jsx';
import IdCard from '../common/widget.idcard.jsx';
import CertValidEditor from '../common/widget.certValidate.jsx';
import CityPicker from '../common/widget.cityPicker.jsx';
import Form from '../common/widget.form2.jsx';

env.company = 'zhongan';
env.parseDict = function(dict) {
	return dict.map(v => [v.code, v.text]);
};

class ApplicantForm extends Form {
	form() {
		let v = [
            {name:'投保人类型', code:"type", type:"switch", refresh:"yes", options:[["1","公司"],["2","个人"]]},
			{name:'投保人名称', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入名称"},
			{name:'证件类型', code:"certType", type:"switch", options:[["1","身份证"]]},
			{name:'证件号码', code:"certNo", type:"idcard", req:"yes"},
            {name:'所在地区', code:"city", type:"city", company: env.company, req:"yes"},
			{name:'通讯地址', code:"address", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{9,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入通讯地址"},
		];
		return this.buildForm(v);
	}
	verify(code, val) {
		return {};
	}
}

class InsurantForm extends Form {
	form() {
        let form = this.props.fields.map(v => {
            if (v.scope == "insurant") {
                return {
                    name: v.label,
                    code: v.name,
                    type: v.widget,
                    refresh: "yes",
                    options: v.detail,
                    value: v.value
                };
            }
        });
        form.push({name:'发动机号', code:"engine", type:"text", req:"yes", desc:"请输入发动机号"});
        form.push({name:'车架号', code:"frameNo", type:"text", req:"yes", desc:"请输入车架号"});
		form.push({name:'车牌号', code:"plateNo", type:"text", desc:"请输入车牌号，新车可为空"});
		return this.buildForm(form);
	}
}

class ContactForm extends Form {
	sendSms() {
		let phone = this.refs.mobile.val();
		common.req("ware/do/sms.json", {platformId: 2, tokenId:env.tokenId, phone:phone}, r => {
			env.smsKey = phone;
		});
	}
	form() {
		let v = [
            {name:'联系人姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入联系人姓名"},
			{name:'电子邮箱', code:"email", type:"text", reg:"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", req:"yes", mistake:"请输入正确的电子邮箱", desc:"用于接收电子保单"},
			{name:'手机', code:"mobile", type:"number", reg:"^1[34578]\\d{9}$", req:"yes", mistake:"请输入正确的手机号码", desc:"请输入手机号码"}
		];
		let form = this.buildForm(v);
		form.push(['短信验证码', (
			<div>
				<div style={{display:"inline-block"}}><Inputer ref="smsCode" valCode="smsCode" valType="number" valReg="^\d{6}$" valMistake="验证码为6位数字" valReq="yes" onChange={this.onChange} placeholder="请输入验证码"/></div>
				<span className="blockSel" onClick={this.sendSms.bind(this)}>发送</span>
			</div>
		), "smsCode"]);
		return form;
	}
}

class PlanForm extends Form {
	form() {
		let form = this.props.fields == null ? [] : this.props.fields.map(v => {
            if (v.scope == null || (v.scope != "insurant" && v.scope != "applicant")) {
                return {
                    name: v.label,
                    code: v.name,
                    type: v.widget,
                    refresh: "yes",
                    options: v.detail,
                    value: v.value
                };
            }
		});
		form.push({name: "保单生效日", code: "insureTime", type: "static", value: "次日0时"});
		return this.buildForm(form);
	}
}

var Ground = React.createClass({
	getInitialState() {
		return {
			premium: -1,
			dict: false
		};
    },
    componentWillMount() {
		common.req("ware/detail.json", {packId: env.packId}, r => {
			env.pack = r;
			env.vendor = r.vendor;
			env.vendorId = r.vendor.id;
			env.company = r.vendor.code;
			this.setState({factors:r.factors}, () => {
                this.render();
				if (this.props.defVal.factors != null)
					this.refreshPremium();
			});

            document.title = r.name;
            if ("undefined" != typeof iHealthBridge) {
                IYB.setTitle(r.name);
            }
		});
    },
	getPlanFactors() {
    	let factors = {packId: env.packId};
        this.refs.plan.props.fields.map(v => {
        	var x = this.refs.plan.refs[v.name];
        	if (x == null)
        		x = this.refs.insurant.refs[v.name];
			if (x != null)
				factors[v.name] = x.val();
		});
		factors["ZONE"] = this.refs.applicant.refs.city.val().code;
    	return factors;
    },
	getPlanDesc() {
		let desc = {};
		let r = [];
        this.refs.plan.props.fields.map(v => {
            if (v.scope == null || (v.scope != "insurant" && v.scope != "applicant")) {
                var x = this.refs.plan.refs[v.name];
                if (x == null)
                    x = this.refs.insurant.refs[v.name];
                if (x != null) r.push({
					name: v.label,
					val: v.widget == 'static' ? v.value : x.val(),
					text: v.widget == 'static' ? v.value : (x.text ? x.text() : x.val())
				});
			}
		});
		return r;
	},
    refreshPremium() {
    	let factors = this.getPlanFactors();
		common.req("ware/do/life.json", {platformId:2, opt:"try", content:factors}, r => {
			let factors = this.state.factors;
			if (r.form != null) factors.map(function(e) {
				var res = r.form[e.name];
				if (res != null)
					e.value = res;
			});
			this.setState({premium: r.total, rules: r.rules, factors:factors});
		});
	},
	submit() {
		if (env.brokerId == null || env.brokerId == "") {
			alert("缺少代理人信息");
			return;
		}
		//投保人信息校验
		if (!this.refs.applicant.verifyAll()) {
			alert("请检查投保人信息");
			return;
		}
		env.applicant = this.refs.applicant.val();
		env.applicant.certName = this.refs.applicant.refs.certType.text();
		env.applicant.cityName = this.refs.applicant.refs.city.val().text;
		// 被保险人信息校验
		if (!this.refs.insurant.verifyAll()) {
            alert("请检查被车辆信息");
            return;
		}
		//规则保费
		if (this.state.rules != null && this.state.rules.length > 0) {
			alert("请检查投保规则");
			return;
		}
		if (typeof this.state.premium != "number") {
			alert("请确认保费已正确计算");
			return;
		}
		if (!this.refs.contact.verifyAll()) {
			alert("请检查通讯信息");
			return;
		}
        if (env.smsKey == null) {
            alert("请获取并输入验证码");
            return;
        }
        let contact = this.refs.contact.val();
		let apply = {
			packId: env.packId,
			packCode: env.pack.code,
			packDesc: this.getPlanDesc(),
            shareType: common.param("shareType"),
            couponCode: common.param("couponCode"),
			factors: this.getPlanFactors(),
			applicant: env.applicant,
			insurant: this.refs.insurant.val(),
            contact: contact,
			premium: this.state.premium,
			smsCode: contact.smsCode,
			smsKey: env.smsKey,
			photo: env.photo,
			read: env.pack.docs.read,
			pay: this.props.defVal.pay,
			vendor: env.vendor
		};
		let order = {	
			orderId: env.orderId,
			productId: env.packId,
			productName: env.pack.name,
            productType: env.pack.type,
			vendorId: env.vendorId,
			price: apply.premium,
			bizNo: null,
			platformId: 2,
			type: 2,
			owner: env.brokerId,
			detail: apply
		};
		common.req("ware/do/verify.json", order, r => {
			common.req("ware/do/apply.json", order, r => {
                common.save("iyb/orderId", order.id);
                document.location.href = r.nextUrl;
            });
        });
	},
	render() {
		// 若没有机构信息，则不进行渲染
        if (!env.vendor)
            return null;
		let app = this.props.defVal.applicant == null ? {} : this.props.defVal.applicant;
		let ins = this.props.defVal.insurant == null ? {} : this.props.defVal.insurant;
		return (
			<div className="common">
				<div className="title">投保人信息</div>
				<div className="form">
					<ApplicantForm ref="applicant" defVal={app} onRefresh={this.refreshPremium}/>
				</div>
				<div className="title">车辆信息</div>
				<div className="form">
					<InsurantForm ref="insurant" defVal={ins} fields={this.state.factors} onRefresh={this.refreshPremium}/>
				</div>
				<div className="title">保险计划</div>
				<div className="form">
					<PlanForm ref="plan" parent={this} defVal={this.props.defVal.factors} fields={this.state.factors} onRefresh={this.refreshPremium}/>
				</div>
				{ this.state.rules == null ? null :
					<div className="form">
						{ this.state.rules.map(r => (<div className="alert" key={r}>{r}</div>)) }
					</div>
				}
				<div className="title">通讯信息</div>
				<div className="form">
					<ContactForm ref="contact" defVal={app}/>
				</div>
				<div className="console">
					<div className="tab">
						<div className="row">
							<div className="col left">
								首年保费：{this.state.premium <= 0 ? "无法计算" : this.state.premium}
							</div>
							<div className="col right" onClick={this.submit}>下一步</div>
						</div>
					</div>
				</div>
			</div>
		);
	}
});

var draw = function(defVal) {
	ReactDOM.render(
		<Ground defVal={defVal}/>, document.getElementById("content")
	);
}

$(document).ready( function() {
	env.orderId = common.param("orderId");
	if (env.orderId == null)
		env.orderId = common.load("iyb/orderId", 1800000);
	if (env.orderId == null || env.orderId == "") {
		env.packId = common.param("packId");
		env.brokerId = common.param("accountId");
		common.req("order/create.json", {}, r => {
			env.orderId = r.id;
			let planFactors = JSON.parse(common.load("iyb/temp", 600000));
			let init = {};
			if (planFactors) {
				init.applicant = {};
				if (planFactors.ZONE)
					init.applicant.city = planFactors.ZONE;
				init.factors = planFactors;
			}
			draw(init);
		});
	} else {
		common.req("order/view.json", {orderId: env.orderId}, r => {
			env.packId = r.productId;
			env.brokerId = r.owner;
			if (r.detail != null) {
				draw(r.detail);
			} else {
				draw({});
			}
		});
	}

	pointman.use('do', () => {
		let config = pointman.getConfig();
		env.tokenId = encodeURIComponent(config.token);
	});
});