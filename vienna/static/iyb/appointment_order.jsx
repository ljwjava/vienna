"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Inputer from '../common/widget.inputer.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import Occupation from '../common/widget.occupation.jsx';
import CertEditor from '../common/widget.cert.jsx';
import DateEditor from '../common/widget.date.jsx';
import City from '../common/widget.city.jsx';
import IdCard from '../common/widget.idcard.jsx';
import CityPicker from '../common/widget.cityPicker.jsx';
import Form from '../common/widget.form2.jsx';
import Photo from '../common/widget.photo.jsx';
import ToastIt from '../common/widget.toast.jsx';

env.dict = {
    relation: {
    	"self": "本人",
        "coupon": "配偶",
        "lineal": "父子/母子/父女/母女"
	},
	bank: []
};

class PayForm extends Form {
    form() {
        let bc = null;
        let bk = null;
        let bcity = null;
        let bauto = null;
        if (env.order.extra != null && env.order.extra.pay != null) {
            bc = env.order.extra.pay.bankCard;
            bk = env.order.extra.pay.bank;
            bcity = env.order.extra.pay.bankCity;
            bauto = env.order.extra.pay.autoPayFlag;
        }
        let v = [
            {name:'开户银行', code:"bank", type:"select", req:"yes", value: bk, options:env.dict.bank, onChange: this.changeBank.bind(this)},
            {name:'银行帐号', code:"bankCard", type:"number", req:"yes", value: bc, mistake:"请输入正确的银行卡号", desc:"银行卡号码"}
        ];
        if(env.company == "shlife" || env.company == "citicpru" || env.company == "tmlife") {
            v.push({name:'开户行所在地区', code:"bankCity", type:"bankCity", value: bcity, company: env.company, refresh:"yes", req:"yes", desc:"开户行所在地"});
        }
        v.push({name:'开户人', code:"bankUser", type:"static", req:"yes", value:env.order.detail.applicant.name});
        if(env.company == "citicpru"){
            v.push({name:'是否自动垫交保费', code:"autoPayFlag", type:"switch", req:"yes", value: bauto, options:[["Y","是"],["N","否"]]});
            v.push({name:'自动垫交保费释义', code:"autoPayFlagDesc", type:"label", value: "自动垫交保险费是一项权益。如果选择本权益，续期保费可以用现金价值扣除未还借款本息之后的余额垫缴，同时保险公司会收取一定的利息。"});
        }
        let forms = this.buildForm(v);
        return forms;
    }
    changeBank() {
    	if(env.company != "fosun"){
    		return false;
		}
    	if(",9002,9003,9004,9007,9008,9009,9010,9013,9014,9015,9017,9018,9021,9022,9023,9024,9025,9026,9027,9028,9029,9030,9039,".indexOf(","+this.refs.bank.val()+",") >= 0 && !common.isWeixin()) {
			// 是否分享至微信端
			$("#payTipsDiv").show();
		}
	}
}

class PayRenewForm extends Form {
    form() {
        let bc = null;
        let bk = null;
        let bcity = null;
        let bauto = null;
        if (env.order.extra != null && env.order.extra.payRenew != null) {
            bc = env.order.extra.payRenew.bankCard;
            bk = env.order.extra.payRenew.bank;
            bcity = env.order.extra.payRenew.bankCity;
            bauto = env.order.extra.payRenew.autoPayFlag;
        }
        let v = [
            {name:'开户银行', code:"bank", type:"select", req:"yes", value: bk, options:env.dict.bank},
            {name:'银行帐号', code:"bankCard", type:"number", req:"yes", value: bc, mistake:"请输入正确的银行卡号", desc:"银行卡号码"}
        ];
        if(env.company == "shlife" || env.company == "citicpru" || env.company == "tmlife") {
            v.push({name:'开户行所在地区', code:"bankCity", type:"bankCity", value: bcity, company: env.company, refresh:"yes", req:"yes", desc:"开户行所在地"});
        }
        v.push({name:'开户人', code:"bankUser", type:"static", req:"yes", value:env.order.detail.applicant.name});
		if(env.company == "citicpru"){
            v.push({name:'是否自动垫交保费', code:"autoPayFlag", type:"switch", req:"yes", value: bauto, options:[["Y","是"],["N","否"]]});
            v.push({name:'自动垫交保费释义', code:"autoPayFlagDesc", type:"label", value: "自动垫交保险费是一项权益。如果选择本权益，续期保费可以用现金价值扣除未还借款本息之后的余额垫缴，同时保险公司会收取一定的利息。"});
		}
        return this.buildForm(v);
    }
};

/** 支付选择 **/
var PaySwich = React.createClass({
	getInitialState() {
		return {payOptions: [], hasShow: false};
	},
	componentDidMount(){
	},
	reSetOptions(options){
        if(!!options) {
            this.setState({payOptions: options, hasShow: true});
        }
	},
	toPay(idx){
		if(this.state.payOptions == null || this.state.payOptions.length <= 0) {
			return false;
		}
		if(this.state.payOptions[idx] == null) {
			return false;
		}
		var vv = this.state.payOptions[idx];
		// console.log(idx, vv);
        let f = common.initForm(vv.nextUrl, vv.params, vv.method);
        f.submit();
	},
    hide(){
		this.setState({hasShow: false});
	},
	render(){
		let x = 0;
		return (
			<div className="notice" style={{display: this.state.hasShow ? "block" : "none"}}>
				<div className="content">
					{this.state.payOptions == null || this.state.payOptions.length <= 0 ?
						<div style={{backgroundColor: "#e93a3a", color: "#FFFFFF", height: "50px", lineHeight: "50px", margin: "15px 20px 10px", borderRadius: "5px"}}>没有支付方式</div> : this.state.payOptions.map(v => {
                        return (
							<div style={{backgroundColor: !v.bgColor ? "#1da2e5" : v.bgColor, color: !v.color ? "#FFFFFF" : v.color, height: "50px", lineHeight: "50px", margin: "15px 20px 10px", borderRadius: "5px"}} onClick={this.toPay.bind(this, x++)}>{v.showName}</div>
                        );
                    })}
					{<div style={{backgroundColor: "#bdbdbd", color: "#FFFFFF", height: "50px", lineHeight: "50px", margin: "15px 20px 10px", borderRadius: "5px"}} onClick={this.hide.bind(this)}>取消</div>}
				</div>
			</div>
		);
	}
});

var Ground = React.createClass({
	getInitialState() {
		return {isSubmit: false, dict:false, disputeShow: false, disputeList: []};
	},
	refresh() {
        common.req("dict/view.json", {company: env.company, name: "bank,relation", version: "new"}, r => {
            // if (typeof r.bank === 'object' && isNaN(r.bank.length)) {
            if(r.bank != null){
                if (!Array.isArray(r.bank)) {
                    env.dict.bank = r.bank[env.order.detail.applicant.city.code.substr(0, 2)];
                } else {
                    env.dict.bank = r.bank;
                }
            }
            for (var l in r.relation) {
                env.dict.relation[r.relation[l].code] = r.relation[l].text;
            }
            this.setState({dict:true});
        }, f => {
            ToastIt("字典数据加载失败");
        });
        try{this.reloadDisputeData(env.order.detail.applicant.city.code);}catch(e){}
	},
	componentDidMount() {
		this.refresh();
    },
	submit() {
		if (this.refs.pay != null && !this.refs.pay.verifyAll()) {
			ToastIt("请检查支付信息");
			return;
		}
		if (this.refs.payRenew != null && !this.refs.payRenew.verifyAll()) {
			ToastIt("请检查支付信息");
			return;
		}
		if (!this.refs.agree.checked) {
			ToastIt("请确认客户声明信息");
			return;
		}
		if (env.order.detail.otherReadDesc != null && env.order.detail.otherReadDesc.length > 0) {
			for(let ix = 0; ix < env.order.detail.otherReadDesc.length; ix++){
                if (!this.refs["agree"+ix].checked) {
                    ToastIt("请确认客户声明信息");
                    return false;
				}
			}
		}
        env.order.extra = (env.order.extra == null ? {} : env.order.extra);
		if (this.refs.pay) {
            env.order.extra.pay = this.refs.pay.val();
            env.order.extra.pay.bankText = this.refs.pay.refs.bank.text();
            if (env.order.extra.pay.bank == null || env.order.extra.pay.bank == "") {
            	this.refresh();
                ToastIt("请选择银行");
                return;
			}
		}
		if (this.refs.payRenew) {
            env.order.extra.payRenew = this.refs.payRenew.val();
            env.order.extra.payRenew.bankText = this.refs.payRenew.refs.bank.text();
            if (env.order.extra.payRenew.bank == null || env.order.extra.payRenew.bank == "") {
            	this.refresh();
                ToastIt("请选择银行");
                return;
			}
		}
		if (this.refs.photos) {
            env.order.extra.photos = this.refs.photos.val();
            if(env.order.extra.photos.length < 2) {
                ToastIt("请上传完整证件影像");
                return;
            }
            if(env.order.extra.photos.length > env.order.detail.photo) {
                ToastIt("请删除多余证件影像");
                return;
            }
		}

		//console.log(env.order);
        // 判断是否可提交
        if(this.state.isSubmit){
            ToastIt("数据提交中，请耐心等待");
            return false;
        }
        this.setState({isSubmit: true}, ()=>{
        	env.order.extra.isWX = common.isWeixin();
            common.req("sale/apply.json", env.order, r => {
                common.save("iyb/orderId", env.order.id);
                if (r.success != false) {
                    if(r.nextUrl != null && r.params != null){
                        var f = common.initForm(r.nextUrl, r.params, r.method);
                        f.submit();
                    } else if (r.payList != null) {
                        this.refs.paySwich.reSetOptions(r.payList);
                    } else if (r.payWxOther != null) {
                    	if(common.isWeixin() && !!r.payWxOther.wx){	// 微信浏览器直接跳转微信支付
                        	var wxfp = r.payWxOther.wx;
                            var f = common.initForm(wxfp.nextUrl, wxfp.params, wxfp.method);
                            f.submit();
						} else 	{	// 其他浏览器执行其他支付方式
                            var othfp = r.payWxOther.other;
                            if(othfp != null && othfp.length > 0) {
                            	if(othfp.length == 1){
                                    var f = common.initForm(othfp[0].nextUrl, othfp[0].params, othfp[0].method);
                                    f.submit();
								}else{
                                    this.refs.paySwich.reSetOptions(othfp);
								}
							}else{
                                ToastIt("当前浏览器无可用的支付方式，可尝试复制链接至微信内操作");
							}
						}
                    } else {
                        document.location.href = r.nextUrl;
					}
                }else{
                    ToastIt(r.errCode + " - " + r.errMsg);
                }
                this.setState({isSubmit: false});
                /*if (r.success != false && r.nextUrl != null) {
                    document.location.href = r.nextUrl;
                } else {
                    ToastIt(r.errCode + " - " + r.errMsg);
                    this.setState({isSubmit: false});
                }*/
            }, r => {
                if(r != null){
                    ToastIt(r);
                }
                this.setState({isSubmit: false});
            });
        });
	},
	// changePhotos() {
     //    env.order.detail.photos = this.refs.photos.val();
     //    common.req("order/save.json", env.order);
	// },
	openDoc(link) {
		try {
            env.order.extra = (env.order.extra == null ? {} : env.order.extra);
            if (this.refs.pay) {
                env.order.extra.pay = this.refs.pay.val();
            }
            if (this.refs.payRenew) {
                env.order.extra.payRenew = this.refs.payRenew.val();
            }
			if (this.refs.photos) {
				env.order.extra.photos = this.refs.photos.val();
            }
			common.req("order/save.json", env.order);
        } catch(e) {
		}
		document.location.href = link;
	},
	// 纠纷相关div数据
	reloadDisputeData(cityCode){
        //
		if(cityCode == null || cityCode.length < 6){
			return null;
		}
		if(env.company != "citicpru" || !(""+env.order.detail.applicant.city.code).startsWith("44")){
		    return null;
        }
        common.req("sale/dispute.json", {provinceId: cityCode.substr(0, 2) + "0000", cityId: cityCode.substr(0, 4) + "00"}, r => {
        	this.setState({disputeList: r});
        }, f => {
            ToastIt("纠纷数据加载失败");
        });
	},
	openDivDispute() {
        $(this.refs.disputeDiv).show();
	},
    hide() {
		$(this.refs.disputeDiv).hide();
	},
	hidePayTipsDiv(){
		$(this.refs.payTipsDiv).hide();
	},
    sharePage(){
		if(common.isAPP()){
            var shareObj = {
                title: "复星联合康乐e生重大疾病保险",
                desc: "继续投保",
                thumb: "https://static.zhongan.com/website/health/iyb/resource/product/ware12/ware12_share.jpg",
                imgUrl: "https://static.zhongan.com/website/health/iyb/resource/product/ware12/ware12_share.jpg",
                link: window.location.href
            };
            iHealthBridge.doAction("share", JSON.stringify(shareObj));
		}
	},
	render() {
		let docs = [];
		if (env.order.detail.read) for (let d in env.order.detail.read) {
			if (docs.length > 0)
				docs.push("、");
			docs.push(<a key={d} onClick={this.openDoc.bind(this, env.order.detail.read[d].url)}>{env.order.detail.read[d].name}</a>);
		}
		try{
            if(env.company == "citicpru" && (""+env.order.detail.applicant.city.code).startsWith("44") && this.state.disputeList != null && this.state.disputeList.length > 0) {	// 440000
                if (docs.length > 0)
                    docs.push("、");
                docs.push(<a onClick={this.openDivDispute.bind(this)}>《广东纠纷调节处信息》</a>);
            }
		}catch(e){console.log("添加广东纠纷调节处信息失败");}

		let preShow = "";
		if(env.company == "fosun" && common.isWeixin()){
            preShow = "续期";
		}

		let insCategoryMap = {
		    "T1": "标准体",
		    "T2": "优选体",
		    "T3": "超优体"
        };
		let insCategory = insCategoryMap[env.order.detail.factors.INS_CATEGORY];

		return (
			<div className="common" style={{maxWidth: "750px", minWidth: "320px", marginLeft: "auto", marginRight: "auto"}}>
				<div className="title">投保人信息</div>
				<div className="view">
					<div><span>　投保人是被保人</span>本人</div>
					<div><span>　投保人姓名</span>{env.order.detail.applicant.name}</div>
					<div><span>　证件</span>{env.order.detail.applicant.certName} {env.order.detail.applicant.certNo}</div>
                    { env.order.detail.applicant.certValidateBegin == null ? null : <div><span>　证件有效起期</span> {env.order.detail.applicant.certValidateBegin}</div>}
                    { env.order.detail.applicant.certValidate == null ? null : <div><span>　证件有效止期</span> {env.order.detail.applicant.certValidate.certLong ? '长期' : env.order.detail.applicant.certValidate.certExpire}</div>}
					<div><span>　性别</span>{env.order.detail.applicant.genderName}</div>
					<div><span>　出生日期</span>{env.order.detail.applicant.birthday}</div>
                    {env.order.detail.applicant.cityName == null ? null : <div><span>　所在城市</span>{env.order.detail.applicant.cityName}</div>}
					<div><span>　详细地址</span>{env.order.detail.applicant.address}</div>
					<div><span>　评定结果</span>{insCategory != null ? insCategory : ""}</div>
					<div><span>　是否吸烟体</span>{env.order.detail.factors.SMOKE == "1" ? "吸烟体" : (env.order.detail.factors.SMOKE == "2" ? "非吸烟体" : "")}</div>
					<div><span>　保额</span>{env.order.detail.factors.AMOUNT != null ? env.order.detail.factors.AMOUNT + "万" : ""}</div>
				</div>
				{/*<div className="view">
					<div><span>通讯信息</span></div>
					<div><span>　电子邮箱</span>{env.order.detail.applicant.email}</div>
					<div><span>　手机号　</span>{env.order.detail.applicant.mobile}</div>
				</div>*/}
				{/*<div className="view">
					<div><span>{env.order.productName}</span></div>
					{ env.order.detail.packDesc.map(v => {
						var txtshow = v.text;
						if(v.text instanceof Object) {
							txtshow = txtshow.text;
						}
						return (<div><span>　{v.name}</span>{txtshow}</div>);
					})}
					{ env.order.detail.factors.effectiveTime == null ? null : <div><span>　保单生效日</span>{env.order.detail.factors.effectiveTime}</div> }
				</div>*/}
				<div className="console">
					<div className="tab">
						<div className="row">
							<div className="col left">
                                {env.order.detail.applyMode == 1 ? "首期" : ""}保费：{!env.order.price || env.order.price <= 0 ? "无法计算" : env.order.price.toFixed(2)}
							</div>
							<div className="col right" onClick={this.submit}>{this.state.isSubmit ? "处理中..." : "去支付"}</div>
						</div>
					</div>
				</div>
			</div>
		);
	}
});

$(document).ready( function() {
	common.req("order/view.json", {orderId: common.param("orderId"), oSign: common.param("oSign")}, r => {
		env.order = r;
		if(r.detail.vendor != null && r.detail.vendor.code != null){
            env.company = r.detail.vendor.code;
		}
		common.save("iyb/orderId", env.order.id);
		ReactDOM.render(
			<Ground/>, document.getElementById("content")
		);
	}, er =>{
        ToastIt(er);
    });

	document.title = "订单确认";
	if ("undefined" != typeof iHealthBridge) {
		IYB.setTitle("订单确认");
        window.IYB.setRightButton(JSON.stringify([]));
	}
});