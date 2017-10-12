"use strict";

// import React, { Component, PropTypes } from 'react'
import React from 'react';
import ReactDOM from 'react-dom';
import Inputer from '../common/widget.inputer.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import Occupation from '../common/widget.occupation.jsx';
import CertEditor from '../common/widget.cert.jsx';
import DateEditor from '../common/widget.date.jsx';
import IdCard from '../common/widget.idcard.jsx';
import CertValidEditor from '../common/widget.certValidate.jsx';
import OccupationPicker from '../common/widget.occupationPicker.jsx';
import CityPicker from '../common/widget.cityPicker.jsx';
import Form from '../common/widget.form2.jsx';

env.company = 'hqlife';
env.dict = {
    relation: [["1","本人"]],
	relation2: [["4","父母"],["2","配偶"],["3","子女"]]
};

env.parseDict = function(dict) {
	return dict.map(v => [v.code, v.text]);
};

env.checkCustomer = function(f) {
	let r = {};
	if ((f.certType == "1") && (f.certNo != null && f.certNo.length == 18)) {
		if (f.birthday != null && f.birthday.length == 10) {
			let y1 = f.certNo.substr(6, 4);
			let m1 = f.certNo.substr(10, 2);
			let d1 = f.certNo.substr(12, 2);
			let y2 = f.birthday.substr(0, 4);
			let m2 = f.birthday.substr(5, 2);
			let d2 = f.birthday.substr(8, 2);
			if (y1 != y2 || m1 != m2 || d1 != d2) {
				r.birthday = "生日与证件不符";
			}
		}
		let crx = Number(f.certNo.substr(16,1)) % 2;
		if ((f.gender == "M" && crx == 0) || (f.gender == "F" && crx == 1))
			r.gender = "性别与证件不符";
	}
	return r;
}

class Beneficiary extends Form {
	form() {
		var v = [
            {name:"是被保险人的", code:"relation", type:"select", options:env.dict.relation2},
			{name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"},
			{name:'证件类型', code:"certType", type:"switch", refresh:"yes", options:[["1","身份证"]]},
			{name:'证件号码', code:"certNo", type:"idcard", refresh:"yes", req:"yes"},
			{name:'证件有效期', code:"certValidate", type:"certValidate", refresh:"yes", req:"yes"},
			/*{name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]},
			{name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"},*/
			// {name:"受益次序", code:"order", type:"switch", options:[["1","第1顺位"],["2","第2顺位"],["3","第3顺位"]]},
			{name:"受益比例", code:"scale", type:"select", options:[["10","10%"],["20","20%"],["30","30%"],["40","40%"],["50","50%"],["60","60%"],["70","70%"],["80","80%"],["90","90%"],["100","100%"]]}
		];
		let form = this.buildForm(v);
		form.push(['', (<span className="blockSel" onClick={this.removeSelf.bind(this)}>－ 删除</span>)]);
		return form;
	}
	verify(code, val) {
		return env.checkCustomer(this.val());
	}
	removeSelf() {
		this.props.onRemove(this.props.index);
	}
}

class ApplicantForm extends Form {
	form() {
		let v = [
			{name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"},
			{name:'证件类型', code:"certType", type:"switch", refresh:"yes", options:[["1","身份证"]]},
			{name:'证件号码', code:"certNo", type:"idcard", refresh:"yes", req:"yes", succ:this.resetCertNo.bind(this)},
            {name:'证件有效期', code:"certValidate", type:"certValidate", refresh:"yes", req:"yes"},
			{name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]},
			{name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"},
            {name:'所在地区', code:"city", type:"city", company: env.company, refresh:"yes", req:"yes"},
			{name:'通讯地址', code:"address", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{9,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入通讯地址"},
            //{name:'邮政编码', code:"zipcode", type:"text", reg:"^(?!\\d00000)(?!\\d11111)(?!\\d22222)(?!\\d33333)(?!\\d44444)(?!\\d55555)(?!\\d66666)(?!\\d77777)(?!\\d88888)(?!\\d99999)\\d{6}$", req:"yes", mistake:"请输入正确的邮政编码", desc:"请输入邮政编码"}
		];
		return this.buildForm(v);
	}
	verify(code, val) {
		return env.checkCustomer(this.val());
	}
	resetCertNo(certNo) {
		this.refs.birthday.change(certNo.substr(6, 4) + "-" + certNo.substr(10, 2) + "-" + certNo.substr(12, 2));
		this.refs.gender.change(Number(certNo.substr(16,1)) % 2 == 1 ? "M" : "F");
	}
}

class InsurantForm extends Form {
	form() {
		let v = [
			{name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"},
			{name:'证件类型', code:"certType", type:"switch", refresh:"yes", options:[["1","身份证"]]},
			{name:'证件号码', code:"certNo", type:"idcard", refresh:"yes", req:"yes", succ:this.resetCertNo.bind(this)},
            {name:'证件有效期', code:"certValidate", type:"certValidate", refresh:"yes", req:"yes"},
			{name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]},
			{name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"},
			{name:'所在地区', code:"city", type:"city", company: env.company, refresh:"yes", req:"yes"},
			{name:'通讯地址', code:"address", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{9,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入通讯地址"} //,
            //{name:'邮政编码', code:"zipcode", type:"text", reg:"^(?!\\d00000)(?!\\d11111)(?!\\d22222)(?!\\d33333)(?!\\d44444)(?!\\d55555)(?!\\d66666)(?!\\d77777)(?!\\d88888)(?!\\d99999)\\d{6}$", req:"yes", mistake:"请输入正确的邮政编码", desc:"请输入邮政编码"}
		];
		return this.buildForm(v);
	}
	verify(code, val) {
		return env.checkCustomer(this.val());
	}
	resetCertNo(certNo) {
		this.refs.birthday.change(certNo.substr(6, 4) + "-" + certNo.substr(10, 2) + "-" + certNo.substr(12, 2));
		this.refs.gender.change(Number(certNo.substr(16,1)) % 2 == 1 ? "M" : "F");
	}
}

class InsurantMore extends Form {
	form() {
		let v = [
			// {name:'职业类别', code:"occupationCategory", type:"static", value:"1-4类职业"},
            {name:'职业', code:"occupation", type:"occupation", refresh:"yes", req:"yes", desc:"请选择职业"}
			// {name:'身高(cm)', code:"height", type:"number", req:"yes", desc:"请输入身高，单位：厘米）"},
			// {name:'体重(kg)', code:"weight", type:"number", req:"yes", desc:"请输入体重，单位：公斤）"}
		];
		return this.buildForm(v);
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
		if (this.props.fields == null)
			return [];
		let form = this.props.fields.map(v => {
			if (v.scope == null || (v.scope.indexOf("insurant") < 0 && v.scope.indexOf("applicant") < 0)) {
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
		let l = common.ifNull(this.props.defVal.beneficiaryLive, []).length;
		let d = common.ifNull(this.props.defVal.beneficiaryDeath, []).length;
		let ll = [], dd = [];
		for (let i=0;i<l;i++)
			ll.push(i);
		for (let i=0;i<d;i++)
			dd.push(i);
		return {
			insurant: this.props.defVal.insurant != null,
			premium: -1,
			benefitLiveType: common.ifNull(this.props.defVal.beneficiaryLiveType, "insurant"),
			benefitLiveNum: l,
			benefitLive: ll,
			benefitDeathType: common.ifNull(this.props.defVal.beneficiaryDeathType, "law"),
			benefitDeathNum: d,
			benefitDeath: dd,
			dict: false
		};
    },
    componentWillMount() {
		common.req("ware/detail.json", {type:1, packId: env.packId}, r => {
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
		//common.req("dict/view.json", {company: env.company, name:"relation"}, r => {
		//	// env.dict.relation = env.parseDict(r.relation);
		//	env.dict.relation2 = env.parseDict(r.relation).slice(1);
		//	//env.dict.nation = env.parseDict(r.nation);
		//	this.setState({dict:true});
		//});
    },
	getPlanFactors() {
    	let factors = {packId: env.packId};
    	let self = this.refs.plan;
		self.props.fields.map(function(v) {
			if (self.refs[v.name])
				factors[v.name] = self.refs[v.name].val();
		});
    	if (this.state.insurant) {
	    	factors["GENDER"] = this.refs.insurant.refs.gender.val();
	    	factors["BIRTHDAY"] = this.refs.insurant.refs.birthday.val();
			factors["ZONE"] = this.refs.insurant.refs.city.val().code;
			factors["A_GENDER"] = this.refs.applicant.refs.gender.val();
			factors["A_BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["RELATION"] = this.refs.relation.val();
	    } else {
	    	factors["GENDER"] = this.refs.applicant.refs.gender.val();
	    	factors["BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["ZONE"] = this.refs.applicant.refs.city.val().code;
			factors["A_GENDER"] = this.refs.applicant.refs.gender.val();
			factors["A_BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["RELATION"] = "self";
		}
    	return factors;
    },
	getPlanDesc() {
		let desc = {};
		let self = this.refs.plan;
		let r = [];
		self.props.fields.map(v => {
			if (v.scope == null || (v.scope.indexOf("insurant") < 0 && v.scope.indexOf("applicant") < 0)) {
				let c = self.refs[v.name];
				r.push({
					name: v.label,
					val: v.widget == 'static' ? v.value : c.val(),
					text: v.widget == 'static' ? v.value : (c.text ? c.text() : c.val())
				});
			}
		});
		return r;
	},
    refreshPremium() {
    	let factors = this.getPlanFactors();
    	if (factors["BIRTHDAY"] == null || factors["BIRTHDAY"] == "") {
			this.setState({premium: -1, rules: null});
    	} else {
			common.req("ware/do/life.json", {platformId:2, opt:"try", content:factors}, r => {
                let factors = this.state.factors;
                if (r.form != null) factors.map(function(e) {
                    var res = r.form[e.name];
                    if (res != null)
                        e.value = res;
                });
                env.photo = r.photo;
                this.setState({premium: r.total, rules: r.rules, factors:factors});
			});
		}
	},
    changeRelation() {
		let relation = this.refs.relation.val();
		this.setState({insurant: relation != "00"}, () => {
			this.refreshPremium();
		})
	},
	benefitDeath(comp, code) {
		this.setState({benefitDeathType:code});
	},
	benefitLive(comp, code) {
		this.setState({benefitLiveType:code});
	},
	addBeneficiaryDeath() {
		if (this.state.benefitDeath.length >= 3) return;
		this.state.benefitDeath.push(this.state.benefitDeathNum);
		this.setState({benefitDeathNum:this.state.benefitDeathNum + 1});
	},
	addBeneficiaryLive() {
		this.state.benefitLive.push(this.state.benefitLiveNum);
		this.setState({benefitLiveNum:this.state.benefitLiveNum + 1});
	},
	removeBeneficiaryDeath(key) {
		for (let i in this.state.benefitDeath) {
			if (this.state.benefitDeath[i] == key) {
				this.state.benefitDeath.splice(i, 1);
				this.setState({benefitDeath:this.state.benefitDeath});
				return;
			}
		}
	},
	removeBeneficiaryLive(key) {
		for (let i in this.state.benefitLive) {
			if (this.state.benefitLive[i] == key) {
				this.state.benefitLive.splice(i, 1);
				this.setState({benefitLive:this.state.benefitLive});
				return;
			}
		}
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
		env.relation = this.refs.relation.val();
		env.applicant = this.refs.applicant.val();
		env.applicant.certName = this.refs.applicant.refs.certType.text();
		env.applicant.genderName = this.refs.applicant.refs.gender.text();
		env.applicant.cityName = this.refs.applicant.refs.city.val().text;
		// 被保险人信息校验
		if (!!this.refs.more && !this.refs.more.verifyAll()) {
            alert("请检查被保险人信息");
            return;
		}

		let m = !this.refs.more ? null : this.refs.more.val();
		//被保人信息校验
		if (this.state.insurant) {
			if (!this.refs.insurant.verifyAll()) {
				alert("请检查被保险人信息");
				return;
			}
			env.insurant = this.refs.insurant.val();
			env.insurant.certName = this.refs.insurant.refs.certType.text();
			env.insurant.genderName = this.refs.insurant.refs.gender.text();
			env.insurant.cityName = this.refs.insurant.refs.city.val().text;
			env.insurant.relation = this.refs.relation.val();
			if(!!m){
                env.insurant.occupation = m.occupation;
                env.insurant.height = m.height;
                env.insurant.weight = m.weight;
			}
		} else {
            env.insurant = env.applicant;
            if(!!m){
                env.applicant.height = m.height;
                env.applicant.weight = m.weight;
                env.applicant.occupation = m.occupation;
            }
		}
		//受益人
		let b1 = true;
		let vv = {};
		let beneficiaryLive = null;
		let beneficiaryDeath = null;
		if (this.state.benefitLiveType == "other") {
			beneficiaryLive = this.state.benefitLive.map(i => {
				let c = this.refs["l"+i];
				b1 = c.verifyAll() && b1;
				let r = c.val();
				r.certName = c.refs.certType.text();
				r.order = 1;	// 固定第一顺位
				if (vv["l" + r.order] == null) vv["l" + r.order] = 0;
				vv["l" + r.order] += Number(r.scale);
				return r;
			});
			if (beneficiaryLive == null || beneficiaryLive.length == 0)
				b1 = false;
		}
		if (this.state.benefitDeathType == "other") {
			beneficiaryDeath = this.state.benefitDeath.map(i => {
				let c = this.refs["d"+i];
				b1 = c.verifyAll() && b1;
				let r = c.val();
                r.order = 1;	// 固定第一顺位
				r.certName = c.refs.certType.text();
				if (vv["d" + r.order] == null) vv["d" + r.order] = 0;
 				vv["d" + r.order] += Number(r.scale);
				return r;
			});
			if (beneficiaryDeath == null || beneficiaryDeath.length == 0)
				b1 = false;
		}
		for (let ss in vv) {
			if (vv[ss] != 100) {
				alert("受益人同一次序下比例之和需要为100%");
				return;
			}
		}
		if (!b1) {
			alert("请检查受益人信息");
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
		env.applicant.mobile = contact.mobile;
		env.applicant.email = contact.email;
		let apply = {
			packId: env.packId,
			packCode: env.pack.code,
			packDesc: this.getPlanDesc(),
            shareType: common.param("shareType"),
            couponCode: common.param("couponCode"),
			factors: this.getPlanFactors(),
			applicant: env.applicant,
			insurant: this.state.insurant ? env.insurant : null,
			beneficiaryLiveType: this.state.benefitLiveType,
			beneficiaryLive: beneficiaryLive,
			beneficiaryDeathType: this.state.benefitDeathType,
			beneficiaryDeath: beneficiaryDeath,
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
			vendorId: env.vendorId,
			price: apply.premium,
			bizNo: null,
			platformId: 2,
			owner: env.brokerId,
			type: 2,
			detail: apply
		};
		common.req("ware/do/verify.json", order, r => {
			document.location.href = "life_pay.mobile?orderId=" + r.orderId;
		});
	},
	render() {
		// 若没有机构信息，则不进行渲染
        if(!env.vendor){
            return null;
        }

		let b1, b2;
		if (this.state.benefitLiveType == "other") {
			b1 = this.state.benefitLive.map(i => {
				let defVal = null;
				let list = this.props.defVal.beneficiaryLive;
				if (list != null && list.length > i)
					defVal = list[i];
				return (<Beneficiary key={"l"+i} ref={"l"+i} index={i} defVal={defVal} onRemove={this.removeBeneficiaryLive.bind(this, i)}/>);
			});
			b1.push((
				<tbody key="add">
					<tr>
						<td className="left">增加受益人</td>
						<td className="right" onClick={this.addBeneficiaryLive}><span className="blockSel">＋ 增加</span></td>
					</tr>
				</tbody>
			));
		}
		if (this.state.benefitDeathType == "other") {
			b2 = this.state.benefitDeath.map(i => {
				let defVal = null;
				let list = this.props.defVal.beneficiaryDeath;
				if (list != null && list.length > i)
					defVal = list[i];
				return (<Beneficiary key={"d"+i} ref={"d"+i} index={i} defVal={defVal} onRemove={this.removeBeneficiaryDeath.bind(this, i)}/>);
			});
			b2.push((
				<div className="tab">
					<div className="row">
						<div className="col line left">增加受益人</div>
						<div className="col line right" onClick={this.addBeneficiaryDeath}><span className={this.state.benefitDeath.length>=3?"block":"blockSel"}>＋ 增加</span></div>
					</div>
				</div>
			));
		}

		let app = this.props.defVal.applicant == null ? {} : this.props.defVal.applicant;
		let ins = this.props.defVal.insurant == null ? {} : this.props.defVal.insurant;
		return (
			<div className="common">
				<div className="title">投保人信息</div>
				<div className="form">
					<ApplicantForm ref="applicant" defVal={app} onRefresh={this.refreshPremium}/>
				</div>
				<div className="title">被保险人信息</div>
				<div className="form">
					<div className="tab">
						<div className="row">
							<div className="col line left">是投保人的</div>
							<div className="col line right"><Selecter ref="relation" onChange={this.changeRelation} options={env.dict.relation} value={ins.relation}/></div>
						</div>
					</div>
					{this.state.insurant ? (<InsurantForm ref="insurant" defVal={ins} onRefresh={this.refreshPremium}/>) : null}
					{env.company == "hqlife" ? (<InsurantMore ref="more" defVal={this.state.insurant ? ins : app}/>) : null}
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
				<div className="title">身故受益人</div>
				<div className="form">
					<div className="tab">
						<div className="row">
							<div className="col line left">受益人</div>
							<div className="col line right"><Switcher ref="benefitDeath" value={this.props.defVal.beneficiaryDeathType} onChange={this.benefitDeath} options={[["law","法定"],["other","指定"]]}/></div>
						</div>
					</div>
					{b2}
				</div>
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
				if (planFactors.BIRTHDAY)
					init.applicant.birthday = planFactors.BIRTHDAY;
				if (planFactors.GENDER)
					init.applicant.gender = planFactors.GENDER;
				if (planFactors.OCCUPATION_C)
					init.applicant.occupation = planFactors.OCCUPATION_C;
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