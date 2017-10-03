"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Ground = React.createClass({
	intervalId: null,
	getInitialState() {
		return {asking:0, title:"处理中", text:"正在处理，请稍候...", memo:"", modify:0, icon:"images/insure_succ.png"};
	},
	back(step) {
		common.req("order/restore.json", {orderId: env.order.id}, r => {
			history.back(step);
			//document.location.href = "life_pay.mobile?orderId=" + env.order.id;
		});
	},
	componentDidMount() {
		let orderId = common.param("orderId");
		let orderId2 = common.load("iyb/orderId", 1800000);
		if (orderId == null || orderId == "" || orderId != orderId2) {
			alert("已过期");
		} else {
			common.req("order/view.json", {orderId: orderId}, r => {
				env.order = r;
				this.countDown(60);
			});
		}
	},
	finish(t, text) {
		if (this.intervalId != null) {
			clearInterval(this.intervalId);
		}

		let s = {};

		if (t == 1)
			s = {modify:0, title:"投保成功", text:"您的保单已承保，可登录官网查看保单详情", memo:text, icon:"images/insure_succ.png"};
		else if (t == 20)
			s = {modify:1, title:"核保失败", text:text, memo:"请修改后重新提交", icon:"images/insure_fail.png"};
		else if (t == 21)
			s = {modify:1, title:"投保失败", text:text, memo:"请修改后重新提交", icon:"images/insure_fail.png"};
		else if (t == 30)
			s = {modify:2, title:"支付失败", text:text, memo:"请修改支付信息后重新提交", icon:"images/insure_fail.png"};
		else if (t == 40)
			s = {modify:0, title:"已进入人工核保", text:text, icon:"images/insure_fail.png"};
		else if (t == 90)
			s = {modify:0, title:"服务器无响应", text:text, icon:"images/insure_fail.png"};
		else if (t == 91)
			s = {modify:0, title:"服务器连接错误", text:text, icon:"images/insure_fail.png"};
		else if (t == 92)
			s = {modify:0, title:"投保失败", text:text, icon:"images/insure_fail.png"};
		else
			s = {modify:0, title:"处理中", text:text, icon:"images/insure_succ.png"};

		s.asking = 0;

		this.setState(s);
		common.save("iyb/orderId", "");
	},
	countDown(n) {
		this.state.asking = n;
		this.setState({asking: this.state.asking});
		this.intervalId = setInterval(() => {
			this.state.asking--;
			this.setState({asking: this.state.asking});
			if (this.state.asking <= 0) {
				clearInterval(this.intervalId);
				this.finish(90, "请致电客服，确认投保结果");
			} else if(this.state.asking%5 == 0) {
				common.req("order/view.json", {orderId: env.order.id}, r => {
					env.order = r;
                    if (r.status == 3) {
                        this.finish(1, "保单号："+ r.bizNo);
					} else if (r.pay == 5) {
						this.finish(30, r.bizMsg); //支付失败
                    } else if (r.status == 4 || r.status == 1) {
                        this.finish(20, r.bizMsg); //核保失败
                    } else if (r.status == 9 || r.pay == 9) {
                        this.finish(92, r.bizMsg); //未知错误
                    } else if (r.status != 2) {
						this.finish(92, r.bizMsg); //未知错误
					}
                }, r => {
                    this.finish(91, r);
                });
			}
		}, 1000);
	},
   	render() {
		return (
			<div className="graph">
				<div style={{backgroundColor:"01c1f4"}}>
					<div style={{height:"120px", paddingTop:"10px"}}><img style={{width:"160px", height:"117px", margin:"auto"}} src={this.state.icon}/></div>
					<div style={{height:"50px", paddingTop:"15px"}} className="font-wxl">{this.state.title}</div>
					<div style={{paddingTop:"10px"}} className="font-wm">
						{this.state.text}
					</div>
					<div style={{height:"30px"}} className="font-wm">
						{this.state.asking > 0 ? this.state.asking : this.state.memo}
					</div>
					{
						this.state.asking > 0 || this.state.modify == 0 ? null :
						<div style={{paddingBottom:"5px"}}>
							<div style={{height:"40px", lineHeight:"40px", margin:"10px", backgroundColor:"#ffba34"}} className="font-wl" onClick={this.back.bind(this,-this.state.modify)}>修改信息</div>
						</div>
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

	document.title = "投保结果";
	if ("undefined" != typeof iHealthBridge) {
		IYB.setTitle("投保结果");
	}
});