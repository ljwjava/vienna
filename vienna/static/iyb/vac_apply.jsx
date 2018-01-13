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
import ToastIt from '../common/widget.toast.jsx';

env.company = 'zhongan';
env.certType1 = [["1","身份证"]];
env.certType2 = [["101","税务登记证"],["102","营业执照"],["103","组织机构代码证"],["104","统一社会信用代码"]];
env.mapping = {
	certType: {
		"1" : "I",
		"101" : "T",
		"102" : "L",
		"103" : "Z",
		"104" : "TY"
	}
}

env.translate = function(vals) {
	for (var k in env.mapping) {
		if (vals[k]) {
			vals["_" + k] = env.mapping[k][vals[k]];
		}
	}
}

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
};

class ApplicantForm extends Form {
	refreshAll(c) {
		var cusType = c.val();
		if (cusType == "2" && (this.state.cusType == null || this.state.cusType == "1")) {
			this.setState({cusType: cusType}, () => {
				this.refs.certType.change("101");
				this.refs.certNo.change("");
			});
		} else if (cusType == "1" && this.state.cusType == "2") {
			this.setState({cusType: cusType}, () => {
				this.refs.certType.change("1");
				this.refs.certNo.change("");
			});
		}
	}
	form() {
		var cusType ="1";
		if (this.state.cusType != null)
			cusType = this.state.cusType;
		else if (this.props.defVal != null && this.props.defVal.applicant != null)
			cusType = this.props.defVal.applicant.type;
		let v = [
            {name:'投保人类型', code:"type", type:"switch", onChange:this.refreshAll.bind(this), options:[["1","个人"],["2","公司"]], value:cusType},
			{name:'投保人名称', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入名称"}
		];
		if (cusType == "1") {
			v.push({name: '证件类型', code: "certType", type: "switch", options: env.certType1});
			v.push({name: '证件号码', code: "certNo", type: "idcard", req: "yes", succ: this.resetCertNo.bind(this)});
			v.push({name: '性别', code: "gender", type: "switch", options: [["M", "男"], ["F", "女"]]});
			v.push({name: '出生日期', code: "birthday", type: "date", req: "yes", desc: "请选择出生日期"});
		} else if (cusType == "2") {
			v.push({name: '证件类型', code: "certType", type: "select", options: env.certType2});
			v.push({name: '证件号码', code: "certNo", type: "text", req: "yes"});
		}
		return this.buildForm(v);
	}
	verify(code, val) {
		if (this.refs.type.val() == "1")
			return env.checkCustomer(this.val());
		return {};
	}
	resetCertNo(certNo) {
		this.refs.birthday.change(certNo.substr(6, 4) + "-" + certNo.substr(10, 2) + "-" + certNo.substr(12, 2));
		this.refs.gender.change(Number(certNo.substr(16, 1)) % 2 == 1 ? "M" : "F");
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
        form.push({name:'发动机号', code:"engineNo", type:"text", req:"yes", desc:"请输入发动机号"});
        form.push({name:'车架号', code:"frameNo", type:"text", req:"yes", desc:"请输入车架号", reg:"^.{17}$", mistake:"请输入正确的车架号"});
		form.push({name:'车牌号', code:"plateNo", type:"text", desc:"请输入车牌号，新车可为空", reg:"^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4,5}[A-Z0-9挂学警港澳]{1}$", mistake:"请输入正确的车牌号"});
		return this.buildForm(form);
	}
}

class ContactForm extends Form {
	sendSms() {
        if(!!this.state.show && this.state.show > 0){
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
        // console.log(cc);
        if(!cc || cc <= 0){
            return;
        }
        this.setState({show: cc-1});
        setTimeout(()=>{this.countDown()}, 1000);
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
				<span className="blockSel" onClick={this.sendSms.bind(this)}>{!this.state.show || this.state.show <= 0 ? "发送" : (this.state.show+"s")}</span>
			</div>
		), "smsCode"]);
		return form;
	}
}

class PlanForm extends Form {
	form() {
		var hasEffe = false;
		let form = this.props.fields == null ? [] : this.props.fields.map(v => {
            if (v.scope == null || (v.scope != "insurant" && v.scope != "applicant")) {
            	if(v.name == "EFFECTIVE_DATE" || v.name == "EFFECTIVE_DAYS"){
            		hasEffe = true;
				}
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
		if(!hasEffe){
            form.push({name: "保单生效日", code: "insureTime", type: "static", value: "次日0时"});
		}
		return this.buildForm(form);
	}
}

var Ground = React.createClass({
	getInitialState() {
		return {
			premium: -1,
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
			this.setState({form:r.form}, () => {
                if (this.props.defVal.packId) {
                    this.refs.applicant.verifyAll();
                    this.refs.insurant.verifyAll();
                    this.refs.plan.verifyAll();
                }
				if (this.props.defVal.factors) {
                    this.refreshPremium();
                }
			});

            document.title = r.name;
            if ("undefined" != typeof iHealthBridge) {
                IYB.setTitle(r.name);
            }
		});
    },
	getPlanFactors() {
        let factors = {...this.refs.insurant.val(), ...this.refs.plan.val()};
        // console.log(factors);
        factors.packId = env.packId;
		// factors["ZONE"] = this.refs.applicant.refs.city.val().code;
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
					val: v.widget == 'static' || v.widget == 'label' ? v.value : x.val(),
					text: v.widget == 'static' || v.widget == 'label' ? v.value : (x.text ? x.text() : x.val())
				});
			}
		});
		return r;
	},
    refreshPremium() {
    	let factors = this.getPlanFactors();
		common.req("sale/perform.json", {platformId:2, opt:"try", content:factors}, r => {
            var form = r.form == null ? this.state.form : r.form;
			if (r.factors != null) form.map(function(e) {
				var res = r.factors[e.name];
				if (res != null)
					e.value = res;
			});
			this.setState({premium: r.total, rules: r.rules, form:form});
		});
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
		env.applicant = this.refs.applicant.val();
		env.applicant.certName = this.refs.applicant.refs.certType.text();
		// env.applicant.cityName = this.refs.applicant.refs.city.val().text;
		env.translate(env.applicant);
		// 被保险人信息校验
		if (!this.refs.insurant.verifyAll()) {
            ToastIt("请检查车辆信息");
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
        if (!this.refs.plan.verifyAll()) {
            ToastIt("请检查保险计划信息");
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
		let apply = {
			wareId: env.pack.wareId,
			wareCode: env.pack.wareCode,
			packId: env.packId,
			packCode: env.pack.code,
            prizes: env.pack.extra.prizes == null ? false : env.pack.extra.prizes,
            applyMode: env.pack.applyMode,
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
			read: env.pack.extra.read,
            tips: env.pack.extra.tips,
			pay: this.props.defVal.pay,
			vendor: env.vendor
		};
		let order = {	
			orderId: env.orderId,
			productId: env.packId,
            productCode: env.pack.code,
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

        // 判断是否可提交
        if(this.state.isSubmit){
            ToastIt("数据提交中，请耐心等待");
            return false;
        }
        this.setState({isSubmit: true}, ()=>{
            common.req("sale/check.json", order, r => {
                common.save("iyb/orderId", r.orderId);
                let checkRes = r.result;	// 校验规则
                if(checkRes != null && checkRes.success != true && checkRes.rule != null && checkRes.rule != ''){
                    ToastIt(checkRes.rule);
                    this.setState({isSubmit: false});
                    return;
                }

                common.req("sale/apply.json", order, r => {
                    this.setState({isSubmit: false});
                    // document.location.href = r.nextUrl;
                    if (r.success != false && r.nextUrl != null) {
                        if(r.params != null){
                            var f = common.initForm(r.nextUrl, r.params, r.method);
                            f.submit();
                        }else{
                            document.location.href = r.nextUrl;
                        }
                    }else{
                        ToastIt(r.errCode + " - " + r.errMsg);
                    }
                }, r => {
                    if(r != null){
                        ToastIt(r);
                    }
                    this.setState({isSubmit: false});
                });
            }, r => {
                if(r != null){
                    ToastIt(r);
                }
                this.setState({isSubmit: false});
            });
        });
	},
	render() {
		// 若没有机构信息，则不进行渲染
        if (!env.vendor)
            return null;
		let app = this.props.defVal.applicant == null ? {} : this.props.defVal.applicant;
		let ins = this.props.defVal.insurant == null ? {} : this.props.defVal.insurant;
		let cnt = this.props.defVal.contact == null ? {} : this.props.defVal.contact;
		return (
			<div className="common">
				<div className="title">投保人信息</div>
				<div className="form">
					<ApplicantForm ref="applicant" defVal={app}/>
				</div>
				<div className="title">车辆信息</div>
				<div className="form">
					<InsurantForm ref="insurant" defVal={ins} fields={this.state.form} onRefresh={this.refreshPremium}/>
				</div>
				<div className="title">保险计划</div>
				<div className="form">
					<PlanForm ref="plan" parent={this} defVal={this.props.defVal.factors} fields={this.state.form} onRefresh={this.refreshPremium}/>
				</div>
				{ this.state.rules == null ? null :
					<div className="form">
						{ this.state.rules.map(r => (<div className="error" key={r}>{r}</div>)) }
					</div>
				}
				<div className="title">通讯信息</div>
				<div className="form">
					<ContactForm ref="contact" defVal={cnt}/>
				</div>
				<div className="console">
					<div className="tab">
						<div className="row">
							<div className="col left">
								首年保费：{!this.state.premium || this.state.premium <= 0 ? "无法计算" : this.state.premium.toFixed(2)}
							</div>
							<div className="col right" onClick={this.submit}>{this.state.isSubmit ? "核保中..." : "下一步"}</div>
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
				// if (planFactors.ZONE)
				// 	init.applicant.city = planFactors.ZONE;
				init.factors = planFactors;
				init.insurant = planFactors;
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