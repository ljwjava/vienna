"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import ToastIt from '../common/widget.toast.jsx';
import Switcher from '../common/widget.switcher.jsx';

var Ground = React.createClass({
	intervalId: null,
	getInitialState() {
		// ，投保成功后可获得抽奖机会哦
		return {asking:0, title:"处理中", text:"请耐心等待，不要离开页面", memo:"", modify:0, icon:"images/insure_succ.png", order: null, isConfirmReturnVisit: false, isConfirmCorrect: false, hasCorrect: false, correctUrl: null};
	},
	back(step) {
		common.req("order/restore.json", {orderId: env.order.id}, r => {
			history.go(4+step-history.length);
			//document.location.href = "life_pay.mobile?orderId=" + env.order.id;
		});
	},
	componentDidMount() {
		let orderId = common.param("orderId");
		let orderNo = common.param("orderNo");
		let orderId2 = common.load("iyb/orderId", 1800000);
		if(!orderId && !!orderNo) {
			if(orderNo.startsWith("IYB")) {
                orderId = orderNo.replace("IYB","");
			} else if (orderNo.startsWith("YBD")) {
                orderId = orderNo.replace("YBD","");
			}
		}
		if(!orderId) {
            ToastIt("已过期");
		}
		console.log(orderId);
		this.setState({orderId: orderId}, ()=>{
            this.countDown(160);
		});
	},
	finish(t, text) {
		if (this.intervalId != null) {
			clearInterval(this.intervalId);
		}

		let s = {};

        var vd = env.order.detail.vendor;
        var succText, failText;
        if (!!env.order.detail.tips) {
            succText = env.order.detail.tips.success;
            failText = env.order.detail.tips.fail;
        }
        let succTopText = "";
        if (!succText)
            succText = !!vd.succTips ? vd.succTips : "预约成功，请保持手机号码通畅，工作人员将在1-2个工作日内联系您。";
        if (!failText)
        	failText = !!vd.failTips ? vd.failTips : "请修改后重新提交";

        succText = '尊敬的客户，感谢您的预约，您的预约订单号'+env.order.applyNo+'，请保持手机号畅通，工作人员将在将在1-2个工作日内联系您。';

        if (t == 1) {
			s = {modify:0, title:"预约成功", text:text, memo:succText, titleMemo: succTopText, icon:"images/insure_succ.png"};	// , hasReturnVisit: false, isConfirmReturnVisit: false, hasCorrect: false, isConfirmCorrect: false, correctUrl: null
            // try{this.getUseableCountByOrderNo();}catch (e){}
            // try{this.refs.returnVisit.setState({order: env.order, isConfirmReturnVisit: env.order.extra.isConfirmReturnVisit == true});}catch(e){}
        } else if (t == 20)
			s = {modify:2, title:"核保失败", text:text, memo:failText, icon:"images/insure_fail.png"};
		else if (t == 21)
			s = {modify:2, title:"投保失败", text:text, memo:failText, icon:"images/insure_fail.png"};
		else if (t == 30)
			s = {modify:1, title:"预约失败", text:text, memo:"", icon:"images/insure_fail.png"};
		else if (t == 40)
			s = {modify:0, title:"预约取消", text:text, icon:"images/insure_fail.png"};
		else if (t == 50)
			s = {modify:0, title:"订单已撤销", text:text, icon:"images/insure_fail.png"};
		else if (t == 90)
			s = {modify:0, title:"服务器无响应", text:text, icon:"images/insure_fail.png"};
		else if (t == 91)
			s = {modify:0, title:"服务器连接错误", text:text, icon:"images/insure_fail.png"};
		else if (t == 92)
			s = {modify:0, title:"预约失败", text:text, icon:"images/insure_fail.png"};
		else
			s = {modify:0, title:"处理中", text:text, icon:"images/insure_succ.png"};

		s.asking = 0;
		this.setState(s);
		common.save("iyb/orderId", "");
	},
	countDown(n) {
		this.setState({asking: n}, ()=>{
            this.intervalId = setInterval(() => {
                let asking = this.state.asking;
                this.setState({asking: asking-1});
                if (asking <= 0) {
                    clearInterval(this.intervalId);
                    this.finish(90, "请致电客服，确认投保结果");
                } else if(asking % 5 == 0) {
                    common.req("order/view.json", {orderId: this.state.orderId, oSign: common.param("oSign")}, r => {
                        env.order = r;
                        if (r.status == 32) {	// 32体检通过
                            // r.extra = {iybOrderNo: 'IYB201710161408193477'};
                            this.finish(1, "保单号："+ r.bizNo);
                        } else if (r.status == 31) {	// 31体检不通过
                            this.finish(1, r.bizMsg);
                        } else if (r.status == 22) {	// 22已预约
                            this.finish(1, r.bizMsg);
                        } else if (r.status == 24) {	// 24预约失败
                            this.finish(30, r.bizMsg);
                        } else if (r.status == 25) {	// 25预约取消
                            this.finish(40, r.bizMsg);
                        } else if (r.status == 7) {	// 7撤销订单
                            this.finish(50, r.bizMsg);
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
		});
	},
    getUseableCountByOrderNo(){
	},
	// 抽奖完成回调
	lotteryCallBack(data){
	},
	// 立即领取回调
	getLotteryBack(){
	},
    shareCallback(){
	},
    onClickShareInApp(){
	},
    showReturnVisit(){
	},
	onConfirmVisit(obj){
	},
    gotoCorrect(){
    },
   	render() {
		return (
			<div className="graph" style={{maxWidth: "750px", minWidth: "320px", marginLeft: "auto", marginRight: "auto"}}>
				<div style={{backgroundColor:"01c1f4"}}>
					<div style={{height:"120px", paddingTop:"10px"}}><img style={{width:"160px", height:"117px", margin:"auto"}} src={this.state.icon}/></div>
					<div style={{height:"50px", paddingTop:"15px"}} className="font-wxl">{this.state.title}</div>
					<div style={{paddingTop:"10px"}} className="font-wm">
						{this.state.text}
					</div>
                    {this.state.titleMemo == null || this.state.titleMemo == "" ? "" : (
						<div style={{height:"auto", padding: "5px 0px"}} className="font-wm">{this.state.titleMemo}</div>
                    )}
					<div style={{height:"50px"}} className="font-wm">
						{this.state.asking > 0 ? this.state.asking : this.state.memo}
					</div>
					{
						this.state.asking > 0 || this.state.modify == 0 ? null :
						<div style={{paddingBottom:"5px"}}>
							<div style={{height:"40px", lineHeight:"40px", margin:"10px", backgroundColor:"#ffba34"}} className="font-wl" onClick={this.back.bind(this,-1*this.state.modify)}>修改信息</div>
						</div>
					}
					{
						(!this.state.hasReturnVisit || this.state.isConfirmReturnVisit) ? null :
							<div style={{paddingBottom:"5px"}}>
								<div style={{height:"40px", lineHeight:"40px", margin:"10px", backgroundColor:"#ffba34"}} className="font-wl" onClick={this.showReturnVisit.bind(this)}>在线回访</div>
							</div>
					}
					{
						(!this.state.hasCorrect || this.state.isConfirmCorrect || !this.state.isConfirmReturnVisit) ? null :
							<div style={{paddingBottom:"5px"}}>
								<div style={{height:"40px", lineHeight:"40px", margin:"10px", backgroundColor:"#ffba34"}} className="font-wl" onClick={this.gotoCorrect.bind(this)}>变更详细地址</div>
							</div>
					}
				</div>
				<div className="common" style={{textAlign:"left"}}>
					<div className="title">高保额财务收入证明相关事项说明</div>
					<div className="text" style={{padding:"5px 10px 5px 10px"}}>
                        <p className=""><div>一、累计保额SUM≤300万元，无需递交资料。</div></p>
                        <p className="">
							<div>二、累计保额300万＜SUM≤500万，需递交如下资料：</div>
							<div>　1.《高额保险/保费财务状况告知书》</div>
							<div>　2.被保人有条件的情况下提供以下财务资料：</div>
							<div>　　1)收入证明</div>
							<div>　　　a)受薪人士：提供个人收入证明。如：个人所得税税单、显示工资发放的银行流水、工资单、聘书或聘用合同、有公司抬头信纸打印并加盖公章的收入证明信等；</div>
							<div>　　　b)企业法定代表人、所有者或经营者：提供能证明其企业经营状况和个人财务状况的文件；</div>
							<div>　　2)个人资产资料：包括房产、汽车、银行存款的复印件等。</div>
						</p>
                        <p className="">
							<div>三、累计保额500万＜SUM≤1000万，需递交如下资料：</div>
							<div>　1.《高额保险/保费财务状况告知书》</div>
							<div>　2.被保人必须至少提供以下财务资料之一：</div>
							<div>　　1)收入证明</div>
							<div>　　　a)受薪人士：提供个人收入证明（收入金额需要满足年收入与最高累计保额比例关系）。如：上一年度个人所得税税单、显示工资发放的银行流水，有公司抬头信纸打印并加盖公章的收入证明信；</div>
							<div>　　　b)企业法定代表人、所有者或经营者：提供能证明其企业经营状况和个人财务状况的文件；</div>
							<div>　　2)个人资产资料：包括房产、汽车、银行存款的复印件等。（金额或价值至少达到投保金额）</div>
						</p>
                        <p className="">
							<div>四、累计保额1000万＜SUM≤2500万，需递交如下资料：</div>
							<div>　1.《高额保险/保费财务状况告知书》</div>
							<div>　2.被保人必须提供以下财务资料：</div>
							<div>　　1)收入证明</div>
							<div>　　　a)受薪人士：提供个人收入证明。如：个人所得税税单、显示工资发放的银行流水、工资单、聘书或聘用合同、有公司抬头信纸打印并加盖公章的收入证明信等；</div>
							<div>　　　b)企业法定代表人、所有者或经营者：提供能证明其企业经营状况和个人财务状况的文件；</div>
							<div>　　2)个人资产资料：包括房产、汽车、银行存款的复印件等。（金额或价值至少达到投保金额）</div>
							<div>　　3)接受保险公司的契约调查。</div>
						</p>
					</div>
				</div>
			</div>
		);
	}
});

$(document).ready( function() {
	ReactDOM.render(
		<Ground/>, document.getElementById("content")
	);
	document.title = "预约结果";
	if ("undefined" != typeof iHealthBridge) {
        env.frame = "iyb";
        window.IYB.setTitle("预约结果");
        window.IYB.setRightButton(JSON.stringify([{
            title: '关闭',
            func: 'IYB.back()'
        }]));
	}
});