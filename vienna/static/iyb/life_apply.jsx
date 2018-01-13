"use strict";

// import React, { Component, PropTypes } from 'react'
import React from 'react';
import ReactDOM from 'react-dom';
import Inputer from '../common/widget.inputer.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import Occupation from '../common/widget.occupation.jsx';
import DateEditor from '../common/widget.date.jsx';
import IdCard from '../common/widget.idcard.jsx';
import CertEditor from '../common/widget.cert.jsx';
import CertValidEditor from '../common/widget.certValidate.jsx';
import OccupationPicker from '../common/widget.occupationPicker.jsx';
import CityPicker from '../common/widget.cityPicker.jsx';
import Form from '../common/widget.form2.jsx';
import ToastIt from '../common/widget.toast.jsx';

env.company = 'iyb';
env.def = {
    applicant: {
        cert: [["1","身份证"]],
        certValidate: true,
        city: true,
        address: true
    },
    insurant: {
        cert: [["1","身份证"]],
        certValidate: false,
        city: false,
        address: false,
		occupation: false,
		weight: false,
		height: false,
        relation: [["1","本人"]]
    },
    beneficiary: {
    	display: true,
        custom: true,
        cert: [["1","身份证"]],
        certValidate: true,
		relation: [["4","父母"],["2","配偶"],["3","子女"]],
    },
    certTypeId: "1",
    relationSelf: "1",
};

env.parseDict = function(dict) {
	return dict.map(v => [v.code, v.text]);
};

env.checkCustomer = function(f) {
	let r = {};
	if ((f.certType == env.formOpt.certTypeId) && (f.certNo != null && f.certNo.length == 18)) {
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
};

class Beneficiary extends Form {
	form() {
		let v = [
            {name:"是被保险人的", code:"relation", type:"select", options:env.formOpt.beneficiary.relation, showAddit: false},
			{name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"},
			{name:'证件类型', code:"certType", type:"select", refresh:"yes", options:env.formOpt.beneficiary.cert},
			{name:'证件号码', code:"certNo", type:"idcard", relation: "certType", refresh:"yes", req:"yes", succ:this.resetCertNo.bind(this)}
		];
		if (env.formOpt.beneficiary.certValidate)
            v.push({name:'证件有效期', code:"certValidate", type:"certValidate", req:"yes"});
        v.push({name:'性别', code:"gender", type:"switch", options:[["M","男"],["F","女"]]});
		v.push({name:'出生日期', code:"birthday", type:"date", req:"yes", desc:"请选择出生日期"});
        v.push({name:"受益比例", code:"scale", type:"select", options:[["10","10%"],["20","20%"],["30","30%"],["40","40%"],["50","50%"],["60","60%"],["70","70%"],["80","80%"],["90","90%"],["100","100%"]]});
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
    resetCertNo(certNo) {
        if (this.refs.certType.val() == env.formOpt.certTypeId){
            this.refs.birthday.change(certNo.substr(6, 4) + "-" + certNo.substr(10, 2) + "-" + certNo.substr(12, 2));
            this.refs.gender.change(Number(certNo.substr(16,1)) % 2 == 1 ? "M" : "F");
        }
    }
}

class ApplicantForm extends Form {
	form() {
		let v = [
			{name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"},
			{name:'证件类型', code:"certType", type:"select", refresh:"yes", options:env.formOpt.applicant.cert},
			{name:'证件号码', code:"certNo", type:"idcard", relation: "certType", refresh:"yes", req:"yes", succ:this.resetCertNo.bind(this)}
		];
        if (env.formOpt.applicant.certValidate)
            v.push({name:'证件有效期', code:"certValidate", type:"certValidate", req:"yes"});
        v.push({name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]});
        v.push({name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"});
		if (env.formOpt.applicant.city)
			v.push({name:'所在地区', code:"city", type:"city", company: env.company, refresh:"yes", req:"yes"});
        if (env.formOpt.applicant.address)
            v.push({name:'通讯地址', code:"address", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{9,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入通讯地址"});
		return this.buildForm(v);
	}
	verify(code, val) {
		return env.checkCustomer(this.val());
	}
	resetCertNo(certNo) {
		if (this.refs.certType.val() == env.formOpt.certTypeId){
            this.refs.birthday.change(certNo.substr(6, 4) + "-" + certNo.substr(10, 2) + "-" + certNo.substr(12, 2));
            this.refs.gender.change(Number(certNo.substr(16,1)) % 2 == 1 ? "M" : "F");
		}
	}
}

class InsurantForm extends Form {
	form() {
		let v = [
			{name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"},
			{name:'证件类型', code:"certType", type:"select", refresh:"yes", options:env.formOpt.insurant.cert},
			{name:'证件号码', code:"certNo", type:"idcard", refresh:"yes", req:"yes", succ:this.resetCertNo.bind(this)}
		];
        if (env.formOpt.insurant.certValidate)
            v.push({name:'证件有效期', code:"certValidate", type:"certValidate", req:"yes"});
		v.push({name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]});
		v.push({name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"});
        if (env.formOpt.insurant.city)
            v.push({name:'所在地区', code:"city", type:"city", company: env.company, refresh:"yes", req:"yes"});
        if (env.formOpt.insurant.address)
            v.push({name:'通讯地址', code:"address", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{9,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入通讯地址"});
		return this.buildForm(v);
	}
	verify(code, val) {
		return env.checkCustomer(this.val());
	}
	resetCertNo(certNo) {
        if (this.refs.certType.val() == env.formOpt.certTypeId) {
            this.refs.birthday.change(certNo.substr(6, 4) + "-" + certNo.substr(10, 2) + "-" + certNo.substr(12, 2));
            this.refs.gender.change(Number(certNo.substr(16, 1)) % 2 == 1 ? "M" : "F");
        }
	}
}

class InsurantMore extends Form {
	form() {
		let v = [];
        if (env.formOpt.insurant.occupation)
            v.push({name:'职业', code:"occupation", type:"occupation", refresh:"yes", req:"yes", desc:"请选择职业"});
        if (env.formOpt.insurant.height)
            v.push({name:'身高', code:"height", type:"text", req:"yes", desc:"请填写身高"});
        if (env.formOpt.insurant.weight)
            v.push({name:'体重', code:"weight", type:"text", req:"yes", desc:"请填写体重"});
		return this.buildForm(v);
	}
}

class ContactForm extends Form {
	sendSms() {
        if(!!this.state.show && this.state.show > 0){
            return;
        }
        if(!!this.refs.mobile.verify()){
			ToastIt("请先输入有效手机号");
			return;
		}
        this.countDown(-1);
		let phone = this.refs.mobile.val();
		common.req("util/sms.json", {platformId: 2, tokenId:env.tokenId, phone:phone}, r => {
			env.smsKey = phone;
		});
	}
    countDown(k){
		let cc = this.state.show;
		if((!cc || cc <= 0) && k == -1){
			cc = 60;
		}
		if(!cc || cc <= 0){
			return;
		}
		this.setState({show: cc-1});
		setTimeout(()=>{this.countDown()}, 1000);
	}
	form() {
		let v = [
			{name:'电子邮箱', code:"email", type:"text", reg:"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", req:"yes", mistake:"请输入正确的电子邮箱", desc:"用于接收电子保单"},
			{name:'手机', code:"mobile", type:"number", reg:"^1[34578]\\d{9}$", req:"yes", mistake:"请输入正确的手机号码", desc:"请输入手机号码"}
		];
		let form = this.buildForm(v);
		form.push(['短信验证码', (
			<div>
				<div style={{display:"inline-block", width:"50%"}}><Inputer ref="smsCode" valCode="smsCode" valType="number" valReg="^\d{6}$" valMistake="验证码为6位数字" valReq="yes" onChange={this.onChange} placeholder="请输入验证码"/></div>
				<span className="blockSel" onClick={this.sendSms.bind(this)}>{!this.state.show || this.state.show <= 0 ? "发送" : (this.state.show+"s")}</span>
			</div>
		), "smsCode"]);
		return form;
	}
}

class PlanForm extends Form {
	form() {
		if (this.props.fields == null)
			return [];
		var effDays = false;
		let form = this.props.fields.map(v => {
            if (v.var == "EFFECTIVE_DAYS" || v.var == "EFFECTIVE_DATE")
                effDays = true;
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
		if (!effDays)
			form.push({name: "保单生效日", code: "effectiveTime", type: "static", value: "次日0时"});
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
			insurant: false,
			premium: -1,
			benefitLiveType: common.ifNull(this.props.defVal.beneficiaryLiveType, "insurant"),
			benefitLiveNum: l,
			benefitLive: ll,
			benefitDeathType: common.ifNull(this.props.defVal.beneficiaryDeathType, "law"),
			benefitDeathNum: d,
			benefitDeath: dd,
			dict: false,
			isSubmit: false
		};
    },
    componentDidMount() {
		common.req("sale/detail.json", {packId:env.packId}, r => {
			env.pack = r;
			env.vendor = r.vendor;
			env.vendorId = r.vendor.id;
			env.company = r.vendor.code;
			env.formOpt = r.formOpt == null ? {} : r.formOpt;

			for (var x1 in env.def) {
				if (env.formOpt[x1] == null) {
                    env.formOpt[x1] = env.def[x1];
                } else if (typeof env.def[x1] == "object") {
                    for (var x2 in env.def[x1])
                    	if (env.formOpt[x1][x2] == null)
                            env.formOpt[x1][x2] = env.def[x1][x2];
				}
			}
			var ins = this.props.defVal.insurant != null || (env.formOpt.insurant.relation.length > 0 && (env.formOpt.insurant.relation[0][0] != env.formOpt.relationSelf));
            var opts = [["law","法定"]];
			if (env.formOpt.beneficiary.custom)
                opts.push(["other","指定"]);
			this.setState({form:r.form, benefit:opts, insurant:ins}, () => {
				if (this.props.defVal.packId) {
                    this.refs.applicant.verifyAll();
                    if (this.refs.insurant)
                        this.refs.insurant.verifyAll();
                    if (this.refs.plan)
                        this.refs.plan.verifyAll();
                }
                if (this.props.defVal.factors) {
                    this.refreshPremium();
                }
            });

			/*
            common.req("dict/view.json", {company: env.company, name: "cert", version: "new"}, r0 => {
                env.dict.cert = r0.cert;
                this.setState({factors:r.factors}, () => {
                    this.render();
                    if (this.props.defVal.factors != null)
                        this.refreshPremium();
                });
            }, f => {
                ToastIt("证件类型加载失败");
            });
            */

            document.title = r.name;
            if ("undefined" != typeof iHealthBridge) {
                IYB.setTitle(r.name);
            }
		});
    },
	getPlanFactors() {
		let factors = this.refs.plan.val();
		factors.packId = env.packId;
    	if (this.state.insurant) {
	    	factors["GENDER"] = this.refs.insurant.refs.gender.val();
	    	factors["BIRTHDAY"] = this.refs.insurant.refs.birthday.val();
            if (env.formOpt.insurant.city)
				factors["ZONE"] = this.refs.insurant.refs.city.val().code;
			factors["A_GENDER"] = this.refs.applicant.refs.gender.val();
			factors["A_BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["RELATIVE"] = this.refs.relation.val();
			if (env.formOpt.relationMapping != null)
                factors["RELATIVE"] = env.formOpt.relationMapping[factors["RELATIVE"]];
	    } else {
	    	factors["GENDER"] = this.refs.applicant.refs.gender.val();
	    	factors["BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
            if (env.formOpt.applicant.city)
				factors["ZONE"] = this.refs.applicant.refs.city.val().code;
			factors["A_GENDER"] = this.refs.applicant.refs.gender.val();
			factors["A_BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["RELATIVE"] = "self";
		}
		if (this.refs.more) {
            if (env.formOpt.insurant.occupation) {
                factors["OCCUPATION_L"] = this.refs.more.refs.occupation.val().level;
                factors["OCCUPATION_C"] = this.refs.more.refs.occupation.val().code;
            }
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
					val: v.widget == 'static' || v.widget == 'label' ? v.value : c.val(),
					text: v.widget == 'static' || v.widget == 'label' ? v.value : (c.text ? c.text() : c.val())
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
			common.req("sale/perform.json", {platformId:2, opt:"try", content:factors}, r => {
                var form = r.form == null ? this.state.form : r.form;
                if (r.factors != null) form.map(function(e) {
                    var res = r.factors[e.name];
                    if (res != null)
                        e.value = res;
                });
                env.photo = r.photo;
                this.setState({premium: r.total, rules: r.rules, form:form});
			});
		}
	},
    changeRelation() {
		let relation = this.refs.relation.val();
		this.setState({insurant: relation != env.formOpt.relationSelf}, () => {
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
            ToastIt("缺少代理人信息");
			return;
		}
		//投保人信息校验
		if (!this.refs.applicant.verifyAll()) {
            ToastIt("请检查投保人信息");
			return;
		}
		env.relation = this.refs.relation.val();
		env.applicant = this.refs.applicant.val();
		env.applicant.certName = this.refs.applicant.refs.certType.text();
		env.applicant.genderName = this.refs.applicant.refs.gender.text();
		if (env.formOpt.applicant.city)
			env.applicant.cityName = this.refs.applicant.refs.city.val().text;
		// 被保险人信息校验
		if (!!this.refs.more && !this.refs.more.verifyAll()) {
            ToastIt("请检查被保险人信息");
            return;
		}

		let m = !this.refs.more ? null : this.refs.more.val();
		//被保人信息校验
		if (this.state.insurant) {
			if (!this.refs.insurant.verifyAll()) {
                ToastIt("请检查被保险人信息");
				return;
			}
			env.insurant = this.refs.insurant.val();
			env.insurant.certName = this.refs.insurant.refs.certType.text();
			env.insurant.genderName = this.refs.insurant.refs.gender.text();
            if (env.formOpt.insurant.city)
				env.insurant.cityName = this.refs.insurant.refs.city.val().text;
			env.insurant.relation = this.refs.relation.val();
			if (m != null) {
                env.insurant.occupation = m.occupation;
                env.insurant.height = m.height;
                env.insurant.weight = m.weight;
			}
		} else {
            env.insurant = env.applicant;
            if (m != null) {
                env.applicant.occupation = m.occupation;
                env.applicant.height = m.height;
                env.applicant.weight = m.weight;
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
                ToastIt("受益人同一次序下比例之和需要为100%");
				return;
			}
		}
		if (!b1) {
            ToastIt("请检查受益人信息");
			return;
		}
		//规则保费
		if (this.state.rules != null && this.state.rules.length > 0) {
            ToastIt("请检查投保规则");
			return;
		}
		if (typeof this.state.premium != "number") {
            ToastIt("请确认保费已正确计算");
			return;
		}
		if (!this.refs.contact.verifyAll()) {
            ToastIt("请检查通讯信息");
			return;
		}
        if (env.smsKey == null) {
            ToastIt("请获取并输入验证码");
            return;
        }
		let contact = this.refs.contact.val();
		env.applicant.mobile = contact.mobile;
		env.applicant.email = contact.email;
		let apply = {
			wareId: env.pack.wareId,
            wareCode: env.pack.wareCode,
			packId: env.packId,
			packCode: env.pack.code,
			prizes: env.pack.extra.prizes == null ? false : env.pack.extra.prizes,
			packDesc: this.getPlanDesc(),
            applyMode: env.pack.applyMode,
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
            read: env.pack.extra.read,
            readAddDesc: env.pack.extra.readAddDesc,
			tips: env.pack.extra.tips,
			pay: this.props.defVal.pay,
			vendor: env.vendor
		};
		let order = {	
			orderId: env.orderId,
			productId: env.packId,
            productCode: env.pack.code,
			productName: env.pack.wareName,
			productType: env.pack.type,
			vendorId: env.vendorId,
			price: apply.premium,
			bizNo: null,
			platformId: 2,
			owner: env.brokerId,
			type: 2,
			detail: apply
		};

		// 判断是否可提交
		if(this.state.isSubmit){
			ToastIt("数据提交中，请耐心等待");
			return false;
		}
		this.setState({isSubmit: true}, ()=>{
            common.req("sale/check.json", order, r => {
                this.setState({isSubmit: false});

                let checkRes = r.result;	// 校验规则
                if(checkRes != null && checkRes.success != true && checkRes.rule != null && checkRes.rule != ''){
                    ToastIt(checkRes.rule);
                    return;
                }

                document.location.href = "life_pay.mobile?orderId=" + r.orderId;
            }, r => {
                if(r != null){
                    ToastIt(r);
                }
                console.log(r);
                this.setState({isSubmit: false});
            });
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
							<div className="col line right"><Selecter ref="relation" onChange={this.changeRelation} options={env.formOpt.insurant.relation} value={ins.relation} showAddit={false}/></div>
						</div>
					</div>
					{this.state.insurant ? (<InsurantForm ref="insurant" defVal={ins} onRefresh={this.refreshPremium}/>) : null}
					{env.formOpt.insurant.occupation || env.formOpt.insurant.height || env.formOpt.insurant.weight ? (<InsurantMore ref="more" defVal={this.state.insurant ? ins : app} onRefresh={this.refreshPremium}/>) : null}
				</div>
				<div className="title">保险计划（{env.pack.name}）</div>
				<div className="form">
					<PlanForm ref="plan" parent={this} defVal={this.props.defVal.factors} fields={this.state.form} onRefresh={this.refreshPremium}/>
				</div>
				{ this.state.rules == null ? null :
					<div className="form">
						{ this.state.rules.map(r => (<div className="error" key={r}>{r}</div>)) }
					</div>
				}
                { !env.formOpt.beneficiary.display ? null : <div className="title">身故受益人</div> }
                { !env.formOpt.beneficiary.display ? null :
					<div className="form">
						<div className="tab">
							<div className="row">
								<div className="col line left">受益人</div>
								<div className="col line right"><Switcher ref="benefitDeath" value={this.props.defVal.beneficiaryDeathType} onChange={this.benefitDeath} options={this.state.benefit}/></div>
							</div>
						</div>
						{b2}
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
                                {env.pack != null && env.pack.applyMode == 1 ? "首期" : ""}保费：{!this.state.premium || this.state.premium <= 0 ? "无法计算" : this.state.premium.toFixed(2)}
							</div>
							<div className="col right" onClick={this.submit}>{this.state.isSubmit ? "提交中..." : "下一步"}</div>
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

    try {
        pointman.use('do', () => {
            let config = pointman.getConfig();
            env.tokenId = encodeURIComponent(config.token);
        });
    } catch(e){}

    if ("undefined" != typeof iHealthBridge) {
        window.IYB.setRightButton(JSON.stringify([]));
    }
});


