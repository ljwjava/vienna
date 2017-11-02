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
	// bank: [["0101","中国工商银行"], ["0102","中国农业银行"], ["0103","中国银行"], ["0104","中国建设银行"], ["0108","交通银行"], ["0109","中信银行"], ["0110","中国光大银行"], ["0111","华夏银行"], ["0112","中国民生银行"], ["0113","广东发展银行"], ["0115","招商银行"], ["0116","兴业银行"], ["0117","上海浦东发展银行"], ["0128","中国邮政储蓄银行"], ["0194","北京银行"], ["0197","宁波银行"], ["0198","深圳平安银行"], ["0203","东莞银行"]]
	bank: [["0101", "中国工商银行"], ["0102", "中国农业银行"], ["0103", "中国银行"], ["0104", "中国建设银行"], ["0108", "交通银行"], ["0109", "中信银行"], ["0110", "中国光大银行"], ["0111", "华夏银行"], ["0112", "中国民生银行"], ["0113", "广东发展银行"], ["0115","招商银行"], ["0116", "兴业银行"], ["0117", "上海浦东发展银行"], ["0128", "中国邮政储蓄银行"], ["0198", "平安银行"]]
};

class PayForm extends Form {
	form() {
	    let bc = null;
	    let bk = null;
	    let bcity = null;
	    if(env.order.extra != null && env.order.extra.pay != null) {
            bc = env.order.extra.pay.bankCard;
            bk = env.order.extra.pay.bank;
            bcity = env.order.extra.pay.bankCity;
		}
		let v = [
			{name:'开户银行', code:"bank", type:"select", req:"yes", value: bk, options:env.dict.bank, onChange: (r)=>{console.log(r);}},
            {name:'银行帐号', code:"bankCard", type:"number", req:"yes", value: bc, mistake:"请输入正确的银行卡号", desc:"银行卡号码"},
            {name:'开户行所在地区', code:"bankCity", type:"bankCity", value: bcity, company: env.company, refresh:"yes", req:"yes", desc:"开户行所在地"},
			{name:'开户人', code:"bankUser", type:"static", req:"yes", value:env.order.detail.applicant.name},
		];
		return this.buildForm(v);
	}
}

var Ground = React.createClass({
	getInitialState() {
		return {};
	},
	componentWillMount() {
	},
	submit() {
		if (!this.refs.pay.verifyAll()) {
			ToastIt("请检查支付信息");
			return;
		}
		if (!this.refs.agree.checked) {
			ToastIt("请确认客户声明信息");
			return;
		}
        env.order.extra = (env.order.extra == null ? {} : env.order.extra);
		env.order.extra.pay = this.refs.pay.val();
        env.order.extra.pay.bankText = this.refs.pay.refs.bank.text();
		if (this.refs.photos)
		{
            env.order.detail.photos = this.refs.photos.val();
            if(env.order.detail.photos.length < 2) {
                ToastIt("请上传完整证件影像");
                return;
            }
            if(env.order.detail.photos.length > 2) {
                ToastIt("请删除多余证件影像");
                return;
            }
		}
		//console.log(env.order);
		common.req("ware/do/apply.json", env.order, r => {
			common.save("iyb/orderId", env.order.id);
			document.location.href = r.nextUrl;
		}, r => {
			if(r != null){
				ToastIt(r);
			}
		});
	},
	// changePhotos() {
     //    env.order.detail.photos = this.refs.photos.val();
     //    common.req("order/save.json", env.order);
	// },
	openDoc(link) {
		try{
            env.order.extra = (env.order.extra == null ? {} : env.order.extra);
			env.order.extra.pay = this.refs.pay.val();
			if(this.refs.photos != null) {
				env.order.detail.photos = this.refs.photos.val();
            }
			common.req("order/save.json", env.order);
        }catch(e){}
		document.location.href = link;
	},
	render() {
		let docs = [];
		if (env.order.detail.read) for (let d in env.order.detail.read) {
			if (docs.length > 0)
				docs.push("、");
			docs.push(<a key={d} onClick={this.openDoc.bind(this, env.order.detail.read[d])}>{"《"+d+"》"}</a>);
		}
		return (
			<div className="common">
				<div className="title">投保信息</div>
				<div className="view">
					<div><span>投保人{env.order.detail.insurant == null ? " / 被保险人" : ""}</span></div>
					<div><span>　姓名</span>{env.order.detail.applicant.name}</div>
					<div><span>　证件</span>{env.order.detail.applicant.certName} {env.order.detail.applicant.certNo}</div>
					<div><span>　证件有效期</span> {env.order.detail.applicant.certValidate.certLong ? '长期' : env.order.detail.applicant.certValidate.certExpire}</div>
					<div><span>　性别</span>{env.order.detail.applicant.genderName}</div>
					<div><span>　出生日期</span>{env.order.detail.applicant.birthday}</div>
					<div><span>　所在地区</span>{env.order.detail.applicant.cityName}</div>
					<div><span>　通讯地址</span>{env.order.detail.applicant.address}</div>
					{env.order.detail.applicant.occupation == null ? null : (<div><span>　职业</span>{env.order.detail.applicant.occupation.text}({env.order.detail.applicant.occupation.level}类)</div>)}
					{/* env.order.detail.insurant ? null : <div><span>　身高</span>{env.order.detail.applicant.height}cm</div> */}
					{/* env.order.detail.insurant ? null : <div><span>　体重</span>{env.order.detail.applicant.weight}kg</div> */}
				</div>
				{ env.order.detail.insurant == null ? null :
					<div className="view">
						<div><span>被保险人</span></div>
						<div><span>　姓名</span>{env.order.detail.insurant.name}</div>
						<div><span>　证件</span>{env.order.detail.insurant.certName} {env.order.detail.insurant.certNo}</div>
						<div><span>　证件有效期</span> {env.order.detail.insurant.certValidate.certLong ? '长期' : env.order.detail.insurant.certValidate.certExpire}</div>
						<div><span>　性别</span>{env.order.detail.insurant.genderName}</div>
						<div><span>　出生日期</span>{env.order.detail.insurant.birthday}</div>
						<div><span>　所在地区</span>{env.order.detail.insurant.cityName}</div>
						<div><span>　通讯地址</span>{env.order.detail.insurant.address}</div>
						{ env.order.detail.insurant != null ? null : <div><span>　身高</span>{env.order.detail.insurant.height}cm</div> }
						{ env.order.detail.insurant != null ? null : <div><span>　体重</span>{env.order.detail.insurant.weight}kg</div> }
					</div>
				}
				<div className="view">
					<div><span>通讯信息</span></div>
					<div><span>　电子邮箱</span>{env.order.detail.applicant.email}</div>
					<div><span>　手机号　</span>{env.order.detail.applicant.mobile}</div>
				</div>
				<div className="view">
					<div><span>{env.order.productName}</span></div>
					{
						env.order.detail.packDesc.map(v => {
							return (<div><span>　{v.name}</span>{v.text}</div>);
						})
					}
				</div>
				<div className="view">
					<div><span>受益人</span></div>
					{
						env.order.detail.beneficiaryDeathType == "law" ? <div><span>　法定受益人</span></div> :
						env.order.detail.beneficiaryDeath.map(v => {
							return (
								<div>
									(第{v.order}顺位)
									<span>　姓名</span>{v.name}
									<br/>
									<span>　　　　　 {v.certName}</span>{v.certNo}
									<br/>
									<span>　　　　　 证件有效期</span>{v.certValidate.certLong ? '长期' : v.certValidate.certExpire}
									<br/>
									<span>　　　　　 受益比例</span>{v.scale}%
								</div>
							);
						})
					}
				</div>
				<div className="title">支付信息</div>
				<div className="form">
					<PayForm ref="pay"/>
				</div>
				{ env.order.detail.photo == null || env.order.detail.photo <= 0 ? null :
					<div className="title">身份证拍照</div>
				}
				{ env.order.detail.photo == null || env.order.detail.photo <= 0 ? null :
					<div className="form">
						<Photo ref="photos" value={env.order.detail.photos}/>{/* onChange={this.changePhotos}*/}
					</div>
				}
				<div className="title">客户声明信息</div>
				<div className="view">
					<div className="doc">
						<input type="checkbox" ref="agree"/>我已阅读并同意{docs}，且声明本人仅为中国税收居民。
					</div>
				</div>
				<div className="console">
					<div className="tab">
						<div className="row">
							<div className="col left">
								首年保费：{!env.order.price || env.order.price <= 0 ? "无法计算" : env.order.price.toFixed(2)}
							</div>
							<div className="col right" onClick={this.submit}>下一步</div>
						</div>
					</div>
				</div>
			</div>
		);
	}
});

$(document).ready( function() {
	common.req("order/view.json", {orderId: common.param("orderId")}, r => {
		env.order = r;
		if(r.detail.vendor != null && r.detail.vendor.code != null){
            env.company = r.detail.vendor.code;
		}
		common.save("iyb/orderId", env.order.id);

        common.req("dict/view.json", {company: env.company, name: "bank", version: "new"}, r => {
            env.dict.bank = r.bank;
            ReactDOM.render(
				<Ground/>, document.getElementById("content")
            );
        }, f => {
        	ToastIt("开户行列表加载失败");
		});
	});

	document.title = "投保确认";
	if ("undefined" != typeof iHealthBridge) {
		IYB.setTitle("投保确认");
        window.IYB.setRightButton(JSON.stringify([]));
	}
});