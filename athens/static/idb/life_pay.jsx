"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/widget.navi.jsx';
import Inputer from '../common/widget.inputer.jsx';
import Selecter from '../common/widget.selecter.jsx';
import Switcher from '../common/widget.switcher.jsx';

var apply = {};

var Ground = React.createClass({
	getInitialState() {
		return {
			insurant:apply.order.detail.insurant != null,
			applyNo:apply.order.detail.pay.applyNo,
			asking:0
		};
    },
    componentDidMount() {
		common.req("ware/detail.json", {type:1, packId: apply.packId}, r => {
			this.setState({docs:r.docs.pay});
		});
		common.req("ware/perform.json", {platformId:3, opt:"getBanks"}, r => {
			this.setState({banks:r});
		});
    },
	left() {
		if (apply.brokerId)
			document.location.href = "life_apply.mobile?wareId=" + common.param("wareId") + "&brokerId=" + apply.brokerId + "&packId=" + apply.packId;
	},
	openDoc(link) {
		if ((/iphone|ipod|ipad/gi).test(navigator.platform))
			native.jsCallH5View(link, "");
		else
			document.location.href = link;
	},
	submit() {
		if (this.state.asking > 0)
			return;
		if (apply.order.status == 3) {
			alert("该保单已成功出单");
			return;
		}
		if (!this.onCardChange()) {
			alert("请确认银行卡号输入正确");
			return;
		}
		if (this.state.applyNo == null || this.state.applyNo == "") {
			alert("请确认投保单串号已申请");
			return;
		}
		if (!this.checkPhoto()) {
			alert("请确认证件(每人要求两张)及银行卡照片已都拍摄");
			return;
		}
		if (this.refs.agree && !this.refs.agree.checked) {
			alert("请客户确认已阅读相关文档");
			return;
		}
		if (!this.checkOtherPhoto()) { //alert
			alert("目前暂不需要其他资料的照片，照片中的其他资料将被忽略");
		}
		this.countDown(100);
		apply.order.detail.pay = {
			bankCode: this.refs.bank.val(),
			bankCard: this.refs.bankCard.val(),
			bankName: apply.order.detail.applicant.name,
			applyNo: this.state.applyNo,
			applySc: this.refs.applySc.val(),
			spill: this.refs.spill.val(),
			overdue: this.refs.overdue.val(),
			//batch: this.refs.batch.val(),
			renew: this.refs.renew.val()
		};
		apply.order.detail.photo = {
    		applicant:this.getPhoto("photoApp1,photoApp2"),
    		insurant:this.getPhoto("photoIns1,photoIns2"),
    		bankCard:this.getPhoto("photoBankCard"),
    		other:[] //this.getPhoto("photoOther1,photoOther2,photoOther3,photoOther4,photoOther5,photoOther6")
    	};
		apply.order.bizNo = this.state.applyNo;
		common.req("order/save.json", apply.order, function(r) {
			document.location.href = "life_wait.mobile?wareId=" + common.param("wareId") + "&orderId=" + apply.orderId;
		});
    },
    reqApplyNo() {
		this.onAppScChange();
		if (this.state.applySc != null) {
			alert("请正确输入投保确认书号");
			return;
		}
		if (this.state.asking <= 0) {
			this.countDown(30);
			let self = this;
			if (apply.order.detail.pay == null) apply.order.detail.pay = {};
			apply.order.detail.pay.applySc = this.refs.applySc.val();
			common.req("ware/perform.json", {platformId:3, opt: "reqApplyNo", content: apply.order}, function (r) {
				self.setState({applyNo: r, asking: 0});
			});
		}
    },
	countDown(n) {
		this.state.asking = n;
		this.setState({asking: this.state.asking});
		let intervalId = setInterval(() => {
			this.state.asking--;
			this.setState({asking: this.state.asking});
			if (this.state.asking <= 0)
				clearInterval(intervalId);
		}, 1000);
	},
	checkOtherPhoto() {
		let n = "photoOther1,photoOther2,photoOther3,photoOther4,photoOther5,photoOther6";
		let pass = true;
		n.split(",").map(name => {
			let r = $("#" + name).attr("src");
			if (r != null && r != "" && r.indexOf("images/camera.png") < 0)
				pass = false;
		});
		return pass;
	},
	checkPhoto() {
		let n = "photoApp1,photoApp2,photoBankCard";
		if (this.state.insurant)
			n += ",photoIns1,photoIns2";
		let pass = true;
		n.split(",").map(name => {
			let r = $("#" + name).attr("src");
			if (r != null && r != "" && r.indexOf("images/camera.png") >= 0)
				pass = false;
		});
		return pass;
	},
    takePhoto(name) {
    	if (native) {
    		env.current = name;
    		native.jsOpenCamera("photoCallBack");
    	}
    },
    getPhoto(names) {
    	let res = [];
    	names.split(",").map(function(name) {
	    	let r = $("#" + name).attr("src");
	    	if (r != null && r != "" && r.indexOf("images/camera.png") < 0)
	    		res.push(r);
    	});
    	return res;
    },
	onCardChange() {
		let s1 = this.refs.bankCard.verify();
		let s2 = this.refs.bankCard.val() != this.refs.bankCard2.val() ? "请确认银行卡号输入正确" : null;
		this.setState({bankCard: s1, bankCard2: s2});
		return s1 == null && s2 == null;
	},
	onAppScChange() {
		this.state.applySc = this.refs.applySc.verify();
		this.setState({applySc: this.state.applySc});
	},
	render() {
		var pay = apply.order.detail.pay;
		var v = [
			['自动续保', (<Switcher ref="renew" options={[['1','是'],['0','否']]} value={pay.renew}/>)],
			['银行', (<Selecter ref="bank" options={this.state.banks} value={pay.bankCode}/>)],
			['卡号', (<Inputer ref="bankCard" onChange={this.onCardChange} valReq="yes" value={pay.bankCard} placeholder="请输入银行卡号"/>)],
			['确认卡号', (<Inputer ref="bankCard2" onChange={this.onCardChange} valReq="yes" value={pay.bankCard} placeholder="请再次输入银行卡号"/>)],
			['户名', (<span ref="1">{apply.order.detail.applicant.name}</span>)],
			['付款金额', (<span ref="2">{apply.order.price}</span>)],
			['溢交保险费', (<Switcher ref="spill" options={[['2','抵交续期'],['1','退费']]} value={pay.spill}/>)],
			['非逾期未付', (<Switcher ref="overdue" options={[['N','终止合同']]}/>)],
			//['线下批量收费', (<Switcher ref="batch" options={[['N','否'],['Y','是']]} value={pay.batch}/>)],
			['投保确认书号', (<Inputer ref="applySc" onChange={this.onAppScChange} valReq="yes" valReg="^105002[0-9]{8}88$" value={pay.applySc} placeholder="请输入投保确认书号"/>)],
			['投保单串号', (<span><span ref="applyNo">{this.state.applyNo}</span><span ref="reqApplyNo" className="blockSel" onClick={this.reqApplyNo}>{this.state.asking>0?this.state.asking:"申请"}</span></span>)]
		];
		let r = v.map(v => (
			<tr key={v[1].ref}>
				<td className="left">{v[0]}</td>
				<td className="right">
					<div>{v[1]}</div>
					<div className="alert">{this.state[v[1].ref]}</div>
				</td>
			</tr>
		));
		let docs = [];
		if (this.state.docs) {
			for (let d in this.state.docs) {
				if (docs.length > 0)
					docs.push("、");
				docs.push(<a key={d} onClick={this.openDoc.bind(this, common.link(this.state.docs[d]))}>{"《"+d+"》"}</a>);
			}
		}
		return (
			<div>
				<Navi title="投保" left={[this.left, "<返回"]}/>
				<table>
					<tbody>{r}</tbody>
				</table>
				<div className="title">投保人证件拍照</div>
				<div className="content">
					<img id="photoApp1" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoApp1")}/>
					<img id="photoApp2" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoApp2")}/>
				</div>
				{this.state.insurant ? (<div className="title">被保险人证件拍照</div>) : null}
				{this.state.insurant ? (<div className="content">
					<img id="photoIns1" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoIns1")}/>
					<img id="photoIns2" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoIns2")}/>
				</div>) : null}
				<div className="title">银行卡拍照</div>
				<div className="content">
					<img id="photoBankCard" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoBankCard")}/>
				</div>
				<div className="title">其他资料拍照</div>
				<div className="content">
					<img id="photoOther1" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoOther1")}/>
					<img id="photoOther2" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoOther2")}/>
					<img id="photoOther3" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoOther3")}/>
					<img id="photoOther4" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoOther4")}/>
					<img id="photoOther5" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoOther5")}/>
					<img id="photoOther6" src="../images/camera.png" className="photo" onClick={this.takePhoto.bind(this, "photoOther6")}/>
				</div>
				<div className="title">客户声明信息</div>
				<div className="content">
					<input type="checkbox" ref="agree"/>我已阅读{docs}。
				</div>
				<div className="console" onClick={this.submit}>{this.state.asking>0?this.state.asking:"提交"}</div>
			</div>
		);
	}
});

$(document).ready( function() {
	apply.orderId = common.param("orderId");
	common.req("order/view.json", {orderId: apply.orderId}, function (r) {
		if (r.detail.pay == null) r.detail.pay = {};
		apply.order = r;
		apply.packId = r.productId;
		apply.brokerId = r.owner;
		common.save("idb/orderId", apply.orderId);

		ReactDOM.render(
			<Ground/>, document.getElementById("content")
		);
	});
});