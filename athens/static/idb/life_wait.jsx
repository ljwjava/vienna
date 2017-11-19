"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/widget.navi.jsx';

var Ground = React.createClass({
	intervalId: null,
	getInitialState() {
		return {asking:0, title:"处理中", text:"正在处理，请稍后...", icon:"images/running.gif"};
	},
	componentDidMount() {
		let orderId = common.param("orderId");
		let orderId2 = common.load("idb/orderId", 600000);
		if (orderId == null || orderId == "" || orderId != orderId2) {
			alert("已过期");
			this.home();
		} else {
			common.req("order/view.json", {orderId: orderId}, r => {
				env.order = r;
				this.countDown(300);
				common.req("ware/perform.json", {platformId:3, opt: r.detail.pay.repay == 1 ? "repay" : "insure", type: 1, content: r}, r => {
					if (r.code == 1) {
						this.finish(1, "已出单成功，保单号：" + r.policyCode);
					} else if (r.code == 20) {
						this.finish(20, r.msg); //核保失败
					} else if (r.code == 30) {
						this.finish(30, r.msg); //支付失败
					} else if (r.code == 40) {
						this.finish(40, r.msg); //人核
					} else if (r.code == 101) {
						this.finish(21, r.msg); //一般投保错误
					} else {
						this.finish(92, r.msg); //未知错误
					}
				}, r => {
					this.finish(91, r);
				});
			});
		}
	},
	left() {
		if (native)
			native.jsPopNativeVC("");
			//native.jsCallNativeView('ADBHome', '')
	},
	finish(t, text) {
		if (this.intervalId != null) {
			clearInterval(this.intervalId);
		}

		let s = {};

		if (t == 1)
			s = {pay:2, order:3, title:"已成功出单", text:text, icon:"images/insure_succ.png"};
		else if (t == 20)
			s = {pay:1, order:1, title:"核保失败", text:text, icon:"images/check_fail.png"};
		else if (t == 21)
			s = {pay:1, order:1, title:"投保失败", text:text, icon:"images/check_fail.png"};
		else if (t == 30)
			s = {pay:1, order:1, repay:1, title:"支付失败", text:text, icon:"images/pay_fail.png"};
		else if (t == 40)
			s = {pay:1, order:2, title:"已进入人工核保", text:text, icon:"images/check_manual.png"};
		else if (t == 90)
			s = {pay:1, order:2, title:"服务器无响应", text:text, icon:"images/check_fail.png"};
		else if (t == 91)
			s = {pay:1, order:2, title:"服务器连接错误", text:text, icon:"images/check_fail.png"};
		else if (t == 92)
			s = {pay:1, order:2, title:"未知错误", text:text, icon:"images/check_fail.png"};
		else
			s = {pay:1, order:2, title:"处理中", text:text, icon:"images/running.gif"};

		s.asking = 0;
		env.order.pay = s.pay;
		env.order.status = s.order;

		this.setState(s);

		common.save("idb/orderId", "");
		common.req("order/save.json", env.order, r => {});
	},
	countDown(n) {
		this.state.asking = n;
		this.setState({asking: this.state.asking});
		this.intervalId = setInterval(() => {
			this.state.asking--;
			this.setState({asking: this.state.asking});
			if (this.state.asking <= 0) {
				clearInterval(this.intervalId);
				this.finish(90, "请查询相关订单，确认投保结果");
			}
		}, 1000);
	},
	back() {
		document.location.href = "life_pay.mobile?wareId=" + common.param("wareId") + "&orderId=" + env.order.id;
	},
	repay() {
		document.location.href = "life_repay.mobile?wareId=" + common.param("wareId") + "&orderId=" + env.order.id;
	},
	home() {
		if (native)
			native.jsCallNativeView('ADBHome', '')
	},
   	render() {
		return (
			<div className="graph">
				<Navi title="投保结果" left={[this.left, "<首页"]}/>
				<br/>
				<br/>
				<div>{this.state.title}</div>
				<br/>
				<div>
					<img style={{width:"240px", height:"240px", margin:"auto"}} src={this.state.icon}/>
				</div>
				<br/>
				<div style={{height:"25%"}}>
					{this.state.text}
					{this.state.asking > 0 ? <br/> : null}
					{this.state.asking > 0 ? this.state.asking : null}
				</div>
				<div style={{height:"15%"}}>
					{
						this.state.asking > 0 ? null :
						this.state.repay == 1 ?
							<span className="blockSel" onClick={this.repay}>&nbsp;&nbsp;重新支付&nbsp;&nbsp;</span> :
						env.order != null && env.order.status == 1 ?
							<span className="blockSel" onClick={this.back}>&nbsp;&nbsp;返回修改&nbsp;&nbsp;</span> :
							<span className="blockSel" onClick={this.home}>&nbsp;&nbsp;返回首页&nbsp;&nbsp;</span>
					}
				</div>
			</div>
		);
	}
});

$(document).ready( function() {
	ReactDOM.render(
		<Ground/>, document.getElementById("content")
	);
});