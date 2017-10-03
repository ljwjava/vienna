"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/widget.navi.jsx';
import Inputer from '../common/widget.inputer.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import Occupation from '../common/widget.occupation.jsx';
import CertEditor from '../common/widget.cert.jsx';
import DateEditor from '../common/widget.date.jsx';
import City from '../common/widget.city.jsx';
import Form from '../common/widget.form.jsx';
import Quest from './quest.jsx';

var env = {dict:{
	nation: [["CHN","中国"],["XXX","其他"]],
	marriage: [["5","有配偶"],["6","无配偶"]],
	education: [["0","博士"],["1","硕士"],["2","本科"],["3","大专"],["4","中专或高中"],["5","初中及以下"]]
}};

env.parseDict = function(dict) {
	return dict.map(v => [v.code, v.text]);
};

var QuestForm = React.createClass({
	getValue(def, n) {
		let r = {}, x = {};
		$("#" + n + "Quest").find("input").each(function() {
			var name = $(this).attr("name");
			if (name) {
				var type = $(this).attr("type");
				var str = $(this).val();
				if (type == "checkbox") {
					if (!$(this).is(':checked'))
						str = null;
				} else if (type == "radio") {
					if (!$(this).is(':checked'))
						str = null;
				}
				if (str != null && str != "") {
					r[name] = r[name] == null ? str : r[name] + "," + str;
					if (x[name] == null) x[name] = [];
					x[name].push(str);
				}
			}
		});
		let res = {};
		for (let k in r) {
			let pos = k.indexOf("_");
			if (pos > 0) {
				let v = k.substring(0, pos);
				if (res[v] == null) 
					res[v] = def[v] == null ? {} : {text:def[v].text, version:def[v].version};
				res[v].answer = r[k];
			} else {
				if (res[k] == null) 
					res[k] = def[k] == null ? {} : {text:def[k].text, version:def[k].version};
				res[k].code = k;
				res[k].select = r[k];
			}
		}
		if (!$.isEmptyObject(res)) res.orion = x;
		return res;
	},
	verify() {
		let r = true;
		for (let i in this.refs) {
			r = this.refs[i].verify() && r;
		}
		return r;
	},
	val() {
		let r = {};
		for (let i in this.refs) {
			if (this.refs[i].text)
				common.copy(r, this.refs[i].text());
		}
		return {
			applicant: this.getValue(r, "applicant"),
			insurant: this.getValue(r, "insurant")
		};
	},
	resetAll(n) {
		this.resetForm("applicant");
		this.resetForm("insurant");
	},
	resetForm(n) {
		let r = this.props.defVal == null ? null : this.props.defVal[n];
		if (r == null || $.isEmptyObject(r.orion)) return;
		r = r.orion;
		let temp = {};
		$("#" + n + "Quest").find("input").each(function() {
			var name = $(this).attr("name");
			if (name) {
				var type = $(this).attr("type");
				var x = r[name];
				if (x != null) {
					if (temp[name] == null) temp[name] = 0;
					if (type == "checkbox") {
						if ($(this).val() == x[temp[name]]) {
							$(this).attr("checked",true);
							temp[name]++;
						}
					} else if (type == "radio") {
					} else {
						let str = x[temp[name]++];
						var now = $(this).val();
						if (now == null || now == "")
							$(this).val(str);
					}
				}
			}
		});
	},
	render() {
		let q = [];
		let i = 0;
		if (this.props.quests.applicant != null) {
			q.push(<div key={i++} className="title">投保人告知</div>);
			q.push(<div key={i++} id="applicantQuest" className="quest">
				{this.props.quests.applicant.map(v => (<Quest ref={i} key={i++} options={v}/>))}
			</div>);
		}
		if (this.props.quests.insurant != null) {
			q.push(<div key={i++} className="title">被保险人告知</div>);
			q.push(<div key={i++} id="insurantQuest" className="quest">
				{this.props.quests.insurant.map(v => (<Quest ref={i} key={i++} options={v}/>))}
			</div>);
		}
		return (<div>{q}</div>);
	}
});

class Beneficiary extends Form {
	form() {
		var v = [
			{name:"是被保险人的", code:"relation", type:"select", options:env.dict.relation2},
			{name:'姓名', code:"name", type:"text", reg:"^([\u4e00-\u9fa5]{2,}|[a-zA-Z]{4,})$", req:"yes", mistake:"至少2个汉字或4个英文字符，不能有空格", desc:"请输入姓名"},
			{name:"性别", code:"gender", type:"switch", options:[["M","男"],["F","女"]]},
			{name:"出生日期", code:"birthday", type:"date", req:"yes", desc:"请选择出生日期"},
			{name:"证件", code:"cert", type:"cert", req:"yes"},
			{name:"国籍", code:"nation", type:"select", options:env.dict.nation},
			{name:"受益次序", code:"order", type:"switch", options:[["1","1"],["2","2"],["3","3"]]},
			{name:"受益比例", code:"scale", type:"select", options:[["10","10%"],["20","20%"],["30","30%"],["40","40%"],["50","50%"],["60","60%"],["70","70%"],["80","80%"],["90","90%"],["100","100%"]]}
		];
		let form = this.buildForm(v);
		form.push(['', (<span className="blockSel" onClick={this.removeSelf.bind(this)}>删除</span>)]);
		return form;
	}
	verifyOther() {
		let f = this.val();
		let r = {};
		if (f.nation == "CHN" && f.cert.certType == "1")
			r.cert = "国籍选择中国时，证件不可选择护照";
		if (f.cert.certType == "4" && common.age(f.birthday) >= 18)
			r.cert = "年龄大于等于18周岁，不可选择户口本";
		if ((f.cert.certType == "0" || f.cert.certType == "4") && (f.cert.certNo != null && f.cert.certNo.length == 18) && (f.birthday != null && f.birthday.length == 10)) {
			if (f.birthday != null && f.birthday.length == 10) {
				let y1 = f.cert.certNo.substr(6, 4);
				let m1 = f.cert.certNo.substr(10, 2);
				let d1 = f.cert.certNo.substr(12, 2);
				let y2 = f.birthday.substr(0, 4);
				let m2 = f.birthday.substr(5, 2);
				let d2 = f.birthday.substr(8, 2);
				if (y1 != y2 || m1 != m2 || d1 != d2) {
					r.birthday = "生日与证件不符";
				}
			}
			let crx = Number(f.cert.certNo.substr(16,1)) % 2;
			if ((f.gender == "M" && crx == 0) || (f.gender == "F" && crx == 1))
				r.gender = "性别与证件不符";
		} else if (f.cert.certType == "4" && (f.cert.certNo != null && f.cert.certNo.length == 8)) {
			if (f.birthday != null && f.birthday.length == 10) {
				let y1 = f.cert.certNo.substr(0, 4);
				let m1 = f.cert.certNo.substr(4, 2);
				let d1 = f.cert.certNo.substr(6, 2);
				let y2 = f.birthday.substr(0, 4);
				let m2 = f.birthday.substr(5, 2);
				let d2 = f.birthday.substr(8, 2);
				if (y1 != y2 || m1 != m2 || d1 != d2) {
					r.birthday = "生日与证件不符";
				}
			}
		}
		if (f.relation == "01") {
			if (f.gender == "M" && common.age(f.birthday) < 22)
				r.birthday = "关系为夫妻时，男性年龄不能小于22周岁";
			else if (f.gender == "F" && common.age(f.birthday) < 20)
				r.birthday = "关系为夫妻时，女性年龄不能小于20周岁";
		}
		if (env.insurant != null && env.insurant.birthday != null) {
			if (f.relation == "01") {
				if (f.gender == env.insurant.gender)
					r.gender = "关系为夫妻时，性别不能相同";
			} else if (f.relation == "04") {
				if (f.birthday >= env.insurant.birthday)
					r.birthday = "关系为父母时，年龄不能小于等于被保险人年龄";
			} else if (f.relation == "03") {
				if (f.birthday <= env.insurant.birthday)
					r.birthday = "关系为子女时，年龄不能大于等于被保险人年龄";
			} else if (f.relation == "19" || f.relation == "20" || f.relation == "22" || f.relation == "23") {
				if (f.birthday >= env.insurant.birthday)
					r.birthday = "关系为爷爷、奶奶、外公、外婆时，年龄不能小于等于被保险人年龄";
			} else if (f.relation == "12" || f.relation == "24") {
				if (f.birthday <= env.insurant.birthday)
					r.birthday = "关系为孙子、孙女、外孙子、外孙女时，年龄不能大于等于被保险人年龄";
			}
		}
		return r;
	}
	removeSelf() {
		this.props.onRemove(this.props.index);
	}
}

class CustomerForm extends Form {
	form() {
		let reg2 = null;
		if (this.props.tag != "insurant") {
			reg2 = "^1[34578]\\d{9}$";
		}
		let v = [
			{name:'姓名', code:"name", type:"text", reg:"^([\u4e00-\u9fa5]{2,}|[a-zA-Z]{4,})$", req:"yes", mistake:"至少2个汉字或4个英文字符，不能有空格", desc:"请输入姓名"},
			{name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]},
			{name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"},
			{name:'证件', code:"cert", type:"cert", req:"yes"},
			{name:'国籍', code:"nation", type:"select", options:env.dict.nation},
			{name:'婚姻状况', code:"marriage", type:"switch", options:env.dict.marriage},
			{name:'职业', code:"occupation", type:"occupation", req:"yes"},
			{name:'教育程度', code:"education", type:"select", options:env.dict.education},
			{name:'工作单位', code:"company", type:"text", req:"yes", desc:"请输入工作单位"},
			{name:'工作内容', code:"workDetail", type:"text", req:"yes", desc:"请输入工作内容"},
			{name:'单位地址', code:"companyAddress", type:"text", req:"yes", desc:"请输入工作单位地址"},
			{name:'兼职', code:"partTimeJob", type:"text", req:"yes", desc:"请输入兼职"},
			{name:'手机号码', code:"mobile", type:"text", reg:reg2, mistake:"请输入正确的手机号码", req:"yes", desc:"请输入手机号码"},
			{name:'所在地区', code:"city", type:"city", req:"yes"},
			{name:'通讯地址', code:"address", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{8,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入通讯地址"},
			{name:'邮政编码', code:"zipcode", type:"text", reg:"^(?!\\d00000)(?!\\d11111)(?!\\d22222)(?!\\d33333)(?!\\d44444)(?!\\d55555)(?!\\d66666)(?!\\d77777)(?!\\d88888)(?!\\d99999)\\d{6}$", mistake:"请输入正确的邮编", req:"yes", desc:"请输入邮政编码"}
		];
		//if (this.props.defVal)
		//	v.map(i => {i.value = this.props.defVal[i.code];});
		return this.buildForm(v);
	}
	verifyOther() {
		let f = this.val();
		let r = {};
		if (f.nation == "CHN" && f.cert.certType == "1")
			r.cert = "国籍选择中国时，证件不可选择护照";
		if (f.cert.certType == "4" && common.age(f.birthday) >= 18)
			r.cert = "年龄大于等于18周岁，不可选择户口本";
		if ((f.cert.certType == "0" || f.cert.certType == "4") && (f.cert.certNo != null && f.cert.certNo.length == 18)) {
			if (f.birthday != null && f.birthday.length == 10) {
				let y1 = f.cert.certNo.substr(6, 4);
				let m1 = f.cert.certNo.substr(10, 2);
				let d1 = f.cert.certNo.substr(12, 2);
				let y2 = f.birthday.substr(0, 4);
				let m2 = f.birthday.substr(5, 2);
				let d2 = f.birthday.substr(8, 2);
				if (y1 != y2 || m1 != m2 || d1 != d2) {
					r.birthday = "生日与证件不符";
				}
			}
			let crx = Number(f.cert.certNo.substr(16,1)) % 2;
			if ((f.gender == "M" && crx == 0) || (f.gender == "F" && crx == 1))
				r.gender = "性别与证件不符";
		} else if (f.cert.certType == "4" && (f.cert.certNo != null && f.cert.certNo.length == 8)) {
			if (f.birthday != null && f.birthday.length == 10) {
				let y1 = f.cert.certNo.substr(0, 4);
				let m1 = f.cert.certNo.substr(4, 2);
				let d1 = f.cert.certNo.substr(6, 2);
				let y2 = f.birthday.substr(0, 4);
				let m2 = f.birthday.substr(5, 2);
				let d2 = f.birthday.substr(8, 2);
				if (y1 != y2 || m1 != m2 || d1 != d2) {
					r.birthday = "生日与证件不符";
				}
			}
		}
		if (common.age(f.birthday) < 10 && f.education != "5")
			r.education = "年龄小于10周岁，学历只可选择初中及以下";
		if (f.marriage == "5") {
			if (f.gender == "M" && common.age(f.birthday) < 22)
				r.marriage = "男性年龄小于22周岁，不可选择有配偶";
			else if (f.gender == "F" && common.age(f.birthday) < 20)
				r.marriage = "女性年龄小于20周岁，不可选择有配偶";
		}
		if (this.props.tag == "insurant") {
			if (env.applicant != null && env.applicant.birthday != null) {
				if (env.relation == "01") {
					if (f.gender == "M" && common.age(f.birthday) < 22)
						r.birthday = "男性年龄小于22周岁，关系不可为夫妻";
					else if (f.gender == "F" && common.age(f.birthday) < 20)
						r.birthday = "女性年龄小于20周岁，关系不可为夫妻";
					if (f.gender == env.applicant.gender)
						r.gender = "关系为夫妻时，性别不能相同";
				} else if (env.relation == "04") {
					if (f.birthday >= env.applicant.birthday)
						r.birthday = "关系为父母时，年龄不能小于等于投保人年龄";
				} else if (env.relation == "03") {
					if (f.birthday <= env.applicant.birthday)
						r.birthday = "关系为子女时，年龄不能大于等于投保人年龄";
				} else if (env.relation == "19" || env.relation == "20" || env.relation == "22" || env.relation == "23") {
					if (f.birthday >= env.applicant.birthday)
						r.birthday = "关系为爷爷、奶奶、外公、外婆时，年龄不能小于等于投保人年龄";
				} else if (env.relation == "12" || env.relation == "24") {
					if (f.birthday <= env.applicant.birthday)
						r.birthday = "关系为孙子、孙女、外孙子、外孙女时，年龄不能大于等于投保人年龄";
				}
			}
		}
		return r;
	}
}

class PlanForm extends Form {
	form() {
		let form = this.props.fields == null ? null : this.props.fields.map(v => {
			if (v.scope == null || (v.scope.indexOf("insurant") < 0 && v.scope.indexOf("applicant") < 0)) {
				return {name:v.label, code:v.name, type:v.widget, refresh:"yes", options:v.detail, value:v.value};
			}
		});
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
			premium: "?",
			benefitLiveType: common.ifNull(this.props.defVal.beneficiaryLiveType, "insurant"),
			benefitLiveNum: l,
			benefitLive: ll,
			benefitDeathType: common.ifNull(this.props.defVal.beneficiaryDeathType, "law"),
			benefitDeathNum: d,
			benefitDeath: dd,
			docs: null,
			quests: [],
			dict: false
		};
    },
    componentWillMount() {
		common.req("ware/detail.json", {type:1, packId: env.packId}, r => {
			this.setState({factors:r.factors, docs:r.docs.agree}, () => {
				if (this.props.defVal.factors != null)
					this.refreshPremium();
			});
		});
		common.req("dict/view.json", {company:"gwlife", name:"relation"}, r => {
			env.dict.relation = env.parseDict(r.relation);
			env.dict.relation2 = env.dict.relation.slice(1);
			//env.dict.nation = env.parseDict(r.nation);
			this.setState({dict:true});
		});
    },
	left() {
		document.location.href = "life_ware.mobile?wareId=" + common.param("wareId") + "&brokerId=" + env.brokerId;
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
			factors["A_GENDER"] = this.refs.applicant.refs.gender.val();
			factors["A_BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["RELATION"] = this.refs.relation.val();
	    } else {
	    	factors["GENDER"] = this.refs.applicant.refs.gender.val();
	    	factors["BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["A_GENDER"] = this.refs.applicant.refs.gender.val();
			factors["A_BIRTHDAY"] = this.refs.applicant.refs.birthday.val();
			factors["RELATION"] = "self";
		}
    	return factors;
    },
    refreshPremium() {
    	let factors = this.getPlanFactors();
    	if (factors["BIRTHDAY"] == null || factors["BIRTHDAY"] == "") {
			this.setState({premium: "?", rules: null});
    	} else {
			common.req("ware/perform.json", {platformId:3, opt:"try", type:1, content:factors}, r => {
				this.setState({premium: r.total, rules: r.rules});
			});
			common.req("ware/perform.json", {platformId:3, opt:"quest", type:1, content:factors}, r => {
				this.setState({quests: r}, () => {
					this.refs.quest.resetAll();
				});
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
	alertRule() {
		let text = "";
		this.state.rules.forEach(function(r) {
			text += r + "\n";
		});
		alert(text);
	},
	openDoc(link) {
		if ((/iphone|ipod|ipad/gi).test(navigator.platform))
			native.jsCallH5View(link, "");
		else
			document.location.href = link;
	},
	submit() {
		if (env.brokerId == null || env.brokerId == "") {
			alert("缺少代理人信息");
			return;
		}
		//投保人信息校验
		if (!this.refs.applicant.verify()) {
			alert("请检查投保人信息");
			return;
		}
		env.relation = this.refs.relation.val();
		env.applicant = this.refs.applicant.val();
		env.applicant.cityText = this.refs.applicant.refs.city.text();
		//被保人信息校验
		if (this.state.insurant) {
			if (!this.refs.insurant.verify()) {
				alert("请检查被保险人信息");
				return;
			}
			env.insurant = this.refs.insurant.val();
			env.insurant.relation = this.refs.relation.val();
			env.insurant.cityText = this.refs.insurant.refs.city.text();
		} else {
			env.insurant = env.applicant;
		}
		if (!this.refs.quest.verify()) {
			alert("请检查告知项");
			return;
		}
		//受益人
		let b1 = true;
		let vv = {};
		let beneficiaryLive = null;
		let beneficiaryDeath = null;
		if (this.state.benefitLiveType == "other") {
			beneficiaryLive = this.state.benefitLive.map(i => {
				let c = this.refs["l"+i];
				b1 = c.verify() && b1;
				let r = c.val();
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
				b1 = c.verify() && b1;
				let r = c.val();
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
		if (this.state.premium == "?") {
			alert("请确认保费已正确计算");
			return;
		}
		if (!this.refs.agree.checked || !this.refs.unstand.checked) {
			alert("请客户确认声明信息并阅读风险提示");
			return;
		}
		let apply = {
			packId: env.packId,
			applicant: env.applicant,
			insurant: this.state.insurant ? env.insurant : null,
			factors: this.getPlanFactors(),
			quest: this.refs.quest.val(),
			beneficiaryLiveType: this.state.benefitLiveType,
			beneficiaryLive: beneficiaryLive,
			beneficiaryDeathType: this.state.benefitDeathType,
			beneficiaryDeath: beneficiaryDeath,
			premium: this.state.premium,
			pay: this.props.defVal.pay
		};
		let order = {	
			orderId: env.orderId,
			productId: env.packId,
			price: apply.premium,
			bizNo: null,
			platformId: 3,
			owner: env.brokerId,
			pay: 1,
			status: 2,
			detail: apply
		};
		common.req("order/save.json", order, r => {
			common.save("idb/orderId", env.orderId);
			common.req("ware/perform.json", {platformId:3, opt:"check", type:1, content:order}, r => {
				if (r) {
					alert(r);
				} else {
					document.location.href = "life_pay.mobile?wareId=" + common.param("wareId") + "&orderId=" + env.orderId;
				}
			});
		});
	},
	render() {
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
						<td className="right" onClick={this.addBeneficiaryLive}><span className="blockSel">增加</span></td>
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
				<tbody key="add">
					<tr>
						<td className="left">增加受益人</td>
						<td className="right" onClick={this.addBeneficiaryDeath}><span className="blockSel">增加</span></td>
					</tr>
				</tbody>
			));
		}
		let docs = [];
		if (this.state.docs) {
			for (let d in this.state.docs) {
				if (docs.length > 0)
					docs.push("、");
				docs.push(<a key={d} onClick={this.openDoc.bind(this, common.link(this.state.docs[d]))}>{"《"+d+"》"}</a>);
			}
		}
		let bottom = (<td>首年保费：{this.state.premium}</td>);
		if (this.state.rules != null && this.state.rules.length > 0) {
			bottom = (<td onClick={this.alertRule}>请注意投保规则 ▲</td>);
		}
		return (
			<div>
				<Navi title="填写投保单" left={[this.left, "<返回"]}/>
				<div className="title">投保人信息</div>
				<table>
					<CustomerForm ref="applicant" defVal={this.props.defVal.applicant} onRefresh={this.refreshPremium}/>
				</table>

				<div className="title">被保险人信息</div>
				<table>
					<tbody>
						<tr>
							<td className="left">是投保人的</td>
							<td className="right"><Selecter ref="relation" onChange={this.changeRelation} options={env.dict.relation} value={this.props.defVal.insurant==null?null:this.props.defVal.insurant.relation}/></td>
						</tr>
					</tbody>
					{this.state.insurant ? (<CustomerForm ref="insurant" tag="insurant" defVal={this.props.defVal.insurant} onRefresh={this.refreshPremium}/>) : null}
				</table>

				<div className="title">保险计划</div>
				<table>
					<PlanForm ref="plan" parent={this} defVal={this.props.defVal.factors} fields={this.state.factors} onRefresh={this.refreshPremium}/>
				</table>

				<QuestForm ref="quest" defVal={this.props.defVal.quest} quests={this.state.quests}/>

				<div className="title">生存受益人</div>
				<table>
					<tbody>
						<tr>
							<td className="left">受益人</td>
							<td className="right"><Switcher ref="benefitLive" value={this.props.defVal.beneficiaryLiveType} onChange={this.benefitLive} options={[["insurant","被保险人"],["applicant","投保人"],["other","指定"]]}/></td>
						</tr>
					</tbody>
					{b1}
				</table>

				<div className="title">身故受益人</div>
				<table>
					<tbody>
						<tr>
							<td className="left">受益人</td>
							<td className="right"><Switcher ref="benefitDeath" value={this.props.defVal.beneficiaryDeathType} onChange={this.benefitDeath} options={[["law","法定"],["applicant","投保人"],["other","指定"]]}/></td>
						</tr>
					</tbody>
					{b2}
				</table>

				<div className="title">客户声明信息</div>
				<div className="content">
					<input type="checkbox" ref="agree"/>我已阅读并同意{docs}。
				</div>

				<div className="title">风险提示</div>
				<div className="content">
					<input type="checkbox" ref="unstand"/>
					本人已阅读保险条款，产品说明书和投保提示书，了解本产品的特点和保单利益的不确定性。
				</div>
				<table className="console">
					<tbody>
						<tr>
							{bottom}
							<th onClick={this.submit}>下一步</th>
						</tr>
					</tbody>
				</table>
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
	env.packId = common.param("packId");
	env.brokerId = common.param("brokerId");
	env.orderId = common.load("idb/orderId", 600000);
	if (env.orderId == null || env.orderId == "") {
		common.req("order/create.json", {}, r => {
			env.orderId = r.id;
			common.save("idb/orderId", env.orderId);
			draw({});
		});
	} else {
		common.req("order/view.json", {orderId: env.orderId}, r => {
			if (r.detail != null) {
				draw({
					applicant: r.detail.applicant,
					insurant: r.detail.insurant,
					factors:r.detail.factors,
					beneficiaryLiveType: r.detail.beneficiaryLiveType,
					beneficiaryLive: r.detail.beneficiaryLive,
					beneficiaryDeathType: r.detail.beneficiaryDeathType,
					beneficiaryDeath: r.detail.beneficiaryDeath,
					quest: r.detail.quest,
					pay: r.detail.pay
				});
			} else {
				draw({});
			}
		});
	}
});