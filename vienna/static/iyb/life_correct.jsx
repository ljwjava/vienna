"use strict";

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
import Summary from './summary.jsx';

env.company = 'iyb';
env.def = {
    applicant: {
    	name: false,
		hasCert: false,
        cert: [["1","身份证"]],
        certValidateBegin: false,
        certValidate: false,
        hasGender: false,
		birth: false,
        city: true,
        address: true,
        occupation: false,
        hasIncome: false,
        income: [["gz","工资"],["jj","奖金"],["tz","投资"]]
    },
    insurant: {
        cert: [["1","身份证"]],
        certValidateBegin: false,
        certValidate: true,
        city: false,
        address: false,
        occupation: false,
        weight: false,
        height: false,
        hasSmoke: false,
        smoke: [["2","否"],["1","是"]],
        relation: [["1","本人"]]
    },
    beneficiary: {
        max: 3,
        display: true,
        custom: true,
        order: false,
        cert: [["1","身份证"]],
        certValidateBegin: false,
        certValidate: true,
        relation: [["4","父母"],["2","配偶"],["3","子女"]],
    },
    certTypeId: "1",
    relationSelf: "1",
};

env.parseDict = function(dict) {
    return dict.map(v => [v.code, v.text]);
};

env.isIdCert = function(certType) {
    return env.formOpt.certTypeId == certType;
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

class ApplicantForm extends Form {
    form() {
        let v = [];
		if(env.def.applicant.name)
        	v.push({name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*0-9]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"});
		if(env.def.applicant.hasCert) {
            v.push({name:'证件类型', code:"certType", type:"select", refresh:"yes", options:env.formOpt.applicant.cert});
            v.push({name:'证件号码', code:"certNo", type:"idcard", relation: "certType", refresh:"yes", req:"yes", succ:this.resetCertNo.bind(this), isIdCert:env.isIdCert});
            if (env.formOpt.applicant.certValidateBegin)
                v.push({name:'证件有效起期', code:"certValidateBegin", type:"date", req:"yes"});
            if (env.formOpt.applicant.certValidate)
                v.push({name:'证件有效止期', code:"certValidate", type:"certValidate", req:"yes"});
		}
        if(env.def.applicant.hasGender)
        	v.push({name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]});
        if(env.def.applicant.birth)
        	v.push({name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"});
        if (env.formOpt.applicant.occupation)
            v.push({name:'职业', code:"occupation", type:"occupation", refresh:"yes", req:"yes", desc:"请选择职业"});
        if (env.formOpt.applicant.city)
            v.push({name:'所在地区', code:"city", type:"cityCorrect", company: env.company, refresh:"yes", req:"yes"});
        if (env.formOpt.applicant.address)
            v.push({name:'通讯地址', code:"address", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*]{9,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入通讯地址"});
        if (env.formOpt.applicant.hasIncome) {
            v.push({name:'年收入(万元)', code:"income", type:"number", req:"yes", mistake:"只能输入数字", desc:"请输入年收入"});
            v.push({name:'收入来源', code:"incomeSource", type:"switch", req:"yes", options:env.formOpt.applicant.income});
        }
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
            {name:'姓名', code:"name", type:"text", reg:"^[^\\!\\@\\#\\$\\%\\`\\^\\&\\*0-9]{2,}$", req:"yes", mistake:"字数过少或有特殊符号", desc:"请输入姓名"},
            {name:'证件类型', code:"certType", type:"select", refresh:"yes", certTypeId:env.certTypeId, options:env.formOpt.insurant.cert},
            {name:'证件号码', code:"certNo", type:"idcard", relation: "certType", refresh:"yes", req:"yes", succ:this.resetCertNo.bind(this), isIdCert:env.isIdCert}
        ];
        if (env.formOpt.insurant.certValidateBegin)
            v.push({name:'证件有效起期', code:"certValidateBegin", type:"date", req:"yes"});
        if (env.formOpt.insurant.certValidate)
            v.push({name:'证件有效止期', code:"certValidate", type:"certValidate", req:"yes"});
        v.push({name:'性别', code:"gender", type:"switch", refresh:"yes", options:[["M","男"],["F","女"]]});
        v.push({name:'出生日期', code:"birthday", type:"date", refresh:"yes", req:"yes", desc:"请选择出生日期"});
        if (env.formOpt.insurant.city)
            v.push({name:'所在地区', code:"city", type:"cityCorrect", company: env.company, refresh:"yes", req:"yes"});
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

var Ground = React.createClass({
    getInitialState() {
        return {
            appQuest: false,
            insurant: false,
            dict: false,
            isSubmit: false,
            isConfirmCorrect: false
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
            this.setState({form:r.form, insurant:ins, isConfirmCorrect: (env.order.extra.isConfirmCorrect == true)}, () => {
                if (this.props.defVal.packId && !!this.refs.applicant) {
                    this.refs.applicant.verifyAll();
                    if (this.refs.insurant)
                        this.refs.insurant.verifyAll();
                    if (this.refs.plan)
                        this.refs.plan.verifyAll();
                }
            });
        });
    },
    changeRelation() {
        let relation = this.refs.relation.val();
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
        // 处理证件有效起期
        // env.applicant.certName = this.refs.applicant.refs.certType.text();
        // env.applicant.genderName = this.refs.applicant.refs.gender.text();
        if (env.formOpt.applicant.city)
            env.applicant.cityName = this.refs.applicant.refs.city.val().text;

        let params = {
        	orderId: env.orderId,
            correctData: {
                applicant: env.applicant
			}
		};

        // 判断是否可提交
		if(this.state.isSubmit){
            ToastIt("数据提交中，请耐心等待");
            return false;
        }
        if(this.state.isConfirmCorrect) {
            ToastIt("您已成功完成地址信息修改，无需重复变更");
            return false;
        }
        this.setState({isSubmit: true}, ()=>{
            common.req("sale/correct.json", params, r => {
                if(r.success){
                    ToastIt("修改成功！");
                    // setTimeout(function(){document.location.href = "life_wait.mobile?orderId=" + env.orderId;}, 1000);
                    this.setState({isConfirmCorrect: true});
                }else{
                    ToastIt(r.errMsg);
                    this.setState({isSubmit: false});
                }
            }, r => {
                if(r != null){
                    ToastIt(r);
                }
                console.log(r);
                this.setState({isSubmit: false});
            });
        });
    },
    back() {
        history.back(-1);
    },
    render() {
        // 若没有机构信息，则不进行渲染
        if(!env.vendor){
            return null;
        }
        let app = this.props.defVal.applicant == null ? {} : this.props.defVal.applicant;
        env.insocc = (this.state.insurant || !env.formOpt.applicant.occupation) && env.formOpt.insurant.occupation;
        return (
			<div className="graph" style={{maxWidth: "750px", minWidth: "320px", margin: "0 auto"}}>
                {
                    this.state.isConfirmCorrect == true ?
                        (<div style={{backgroundColor:"01c1f4"}}>
                            <div style={{height:"120px", paddingTop:"10px"}}><img style={{width:"160px", height:"117px", margin:"auto"}} src="images/insure_succ.png"/></div>
                            <div style={{height:"50px", paddingTop:"15px"}} className="font-wxl">修改成功</div>
                            <div style={{paddingTop:"10px"}} className="font-wm">保单号：{env.order.bizNo}</div>
                            <div style={{height:"50px"}} className="font-wm"></div>
                        </div>)
                        :
                        (<div>
                            <div style={{backgroundColor: "rgb(1, 193, 244)", padding: "15px", fontWeight: "bold", color: "#fff"}}>尊敬的客户您好，请您更新并确认下您的投保地址信息准确无误</div>
                            <div className="title" style={{textAlign: "left"}}>投保人信息</div>
                            <div className="form">
                                <ApplicantForm ref="applicant" defVal={app} onRefresh={null}/>
                            </div>
                            <div className="console">
                                <div className="tab">
                                    <div className="row">
                                        <div className="col right" onClick={this.submit}>{this.state.isSubmit ? "提交中..." : (this.state.isConfirmCorrect ? "您已完成变更" : "确认提交")}</div>
                                    </div>
                                </div>
                            </div>
                        </div>)
                }
			</div>
        );
    }
});

var draw = function(defVal) {
    ReactDOM.render(
		<Ground defVal={defVal}/>, document.getElementById("content")
    );
};

$(document).ready( function() {
    env.orderId = common.param("orderId");
    if (env.orderId != null && env.orderId != "") {
        common.req("order/view.json", {orderId: env.orderId}, r => {
            env.order = r;
            env.packId = r.productId;
            env.brokerId = r.owner;
            if (r.detail != null) {
                draw(r.detail);
            } else {
                draw({});
            }
        });
    }
    document.title = "地址在线批改";
    if ("undefined" != typeof iHealthBridge) {
        IYB.setTitle("地址在线批改");
    }
});


