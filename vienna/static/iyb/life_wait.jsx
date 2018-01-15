"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import ToastIt from '../common/widget.toast.jsx';

var ModalLottery = React.createClass({
	getInitialState(){
        let cb = this.props.cb;
		return {isShow: false, giftNum: 1, giftName: "", giftNote: "", giftFreight: "", callback: cb};
	},
	componentDidMount(){
	},
    getGift(){
		if(this.state.callback){
            this.state.callback();
		}
        this.setState({isShow: false});
	},
	close(){
		this.setState({isShow: false});
	},
	render(){
		// console.log(this.state);
		return (<div className="ui-modal" style={{animationDuration: "300ms", display: this.state.isShow ? "" : "none"}}>
			<div className="ui-modal-wrapper">
				<div className="ui-modal-dialog zoom-enter">
					<div className="ui-modal-body">
						<div className="paysuccess-modal-image">
							<img src={"./images/lottery/toast-gift"+this.state.giftNum+".png"}/>
						</div>
						<div className="paysuccess-modal-content">
							<div className="paysuccess-modal-title">
								<p>恭喜你!</p>
								<p>
									<span>获得</span>
									<span>{this.state.giftName}</span>
								</p>
								<p className="freight">{this.state.giftFreight}</p>
							</div>
							<div className="paysuccess-modal-notes">{this.state.giftNote}</div>
							<div className="paysuccess-modal-btn-getprize" onClick={this.getGift}>立即领奖</div>
						</div>
					</div>
					<div className="ui-modal-footer">
						<div className="ui-icon ui-icon-wrong-round theme-default paysuccess-modal-btn-close" onClick={this.close}></div>
					</div>
				</div>
			</div>
		</div>);
	}
});
var LotteryBox = React.createClass({
	getInitialState(){
		let cb = this.props.cb;
		return {isShow: false, isStart: false, luckyOption: null, prizeFlag: -1, lastAngle: 0, option: [
			{
                id: 1,
				num: 6,
                typeid: 39,
                name: "美团60元景点抵用券",
                image: './images/lottery/item6.png',
                modalNote: '点击领取后通过短信进入专属领券页面。登录美团客户端，打开“我的—抵用券”查看并使用',
                btnTxt: '立即领奖',
                listName: '美团60元抵用券',
                freight: {
                    hasFreight: false,
                    freightContent: ''
                }
            },{
                id: 2,
                num: 5,
                typeid: 41,
                name: "九阳原汁机1台",
                image: './images/lottery/item5.png',
                modalNote: '2个工作日内由众安客服将致电您联系确认寄送地址，并在地址确认后的10个工作日寄出，无运费',
                btnTxt: '立即领奖',
                listName: '九阳原汁机1台',
                freight: {
                    hasFreight: false,
                    freightContent: ''
                }
            },{
                id: 3,
                num: 4,
                typeid: 40,
                name: "京东e卡50元1张",
                image: './images/lottery/item4.png',
                modalNote: '奖品将以短信电子券码形式发放至投保人手机，请至京东商城使用',
                btnTxt: '立即领奖',
                listName: '京东e卡50元1张',
                freight: {
                    hasFreight: false,
                    freightContent: ''
                }
            },{
                id: 4,
                num: 3,
                typeid: 42,
                name: "Blueair空气净化器",
                image: './images/lottery/item3.png',
                modalNote: '2个工作日内由众安客服将致电您联系确认寄送地址，并在地址确认后的10个工作日寄出，无运费',
                btnTxt: '立即领奖',
                listName: 'Blueair空气净化器',
                freight: {
                    hasFreight: false,
                    freightContent: ''
                }
            },{
                id: 5,
                num: 2,
                typeid: 37,
                name: "蓝牙耳机一副",
                image: './images/lottery/item2.png',
                modalNote: '点击领取后通过短信获取专属兑换码进入www.feiniu.com进行兑换并使用',
                btnTxt: '立即领奖',
                listName: '蓝牙耳机一副',
                freight: {
                    hasFreight: true,
                    freightContent: '(运费15元需自理)'
                }
            },{
                id: 6,
                num: 1,
                typeid: 38,
                name: "iPhone7 一台",
                image: './images/lottery/item1.png',
                modalNote: '2个工作日内由众安客服将致电您联系确认寄送地址，并在地址确认后的10个工作日寄出，无运费',
                btnTxt: '立即领奖',
                listName: 'iPhone7 一台',
                freight: {
                    hasFreight: false,
                    freightContent: ''
                }
            }
        ], callback: cb};
	},
	componentDidMount(){
	},
	// 开始抽奖
    start(){
		if(this.state.isStart){
			return false;
		}
        this.rotation();
        let _this = this;
        // common.req("/iybapi/open/activity/iybForwardActivityAjax/lucky.json", {orderNo: this.state.orderNo}, r => {
        common.req("sale/lucky.json", {orderNo: this.state.orderNo}, r => {
            console.log(r);
            let luckyOption, luckyNumber;
            if(r.isSuccess){
				if (r.result && r.result.targetId) {
					luckyOption = this.state.option.filter((obj)=>{
						return obj.typeid == r.result.targetId
					});
                    luckyOption = luckyOption[0];
					luckyNumber = luckyOption.id-1;	// 第几个奖品
					this.setState({
                        luckyOption: luckyOption,
                        luckyNumber: luckyNumber
					});
				}
				else{
					// 点击转盘，发起ajax请求接口，待返回中奖id后，改变state中id的值
					this.setState({
						luckyNumber: -1,
                        prizeFlag: -2,
						isStart: true,
					});
				}
			} else {
				this.setState({isStart: false, prizeFlag: -2, luckyNumber: -1});
                ToastIt(r.errorMsg);
			}
        });

		setTimeout(function(){
			if(_this.state.luckyNumber != null && _this.state.luckyNumber >= 0){
				_this.setState({prizeFlag: _this.state.luckyNumber}, ()=>{
					_this.state.callback({prizeFlag: _this.state.luckyNumber, option: _this.state.luckyOption});	// , option: this.state.option[this.state.luckyNumber]
				});
			}
		}, 3000);
	},
    rotation(){
        let _this = this;
        if(this.state.prizeFlag != -1){
            let toAngle = 360 + (this.state.prizeFlag != -2 ? 60 * this.state.prizeFlag + 60/2 : 0);
            if(this.state.prizeFlag == -2){toAngle = 360;}
			// console.log(toAngle);
            let lastAngle = this.state.lastAngle;
            $(this.refs.rotate).rotate({
                angle: lastAngle,
                animateTo: toAngle,
                callback: function(){
                    lastAngle = $(this).getRotateAngle()[0] % 360;
                    lastAngle = lastAngle == 360 ? 0 : lastAngle;
                    if(_this.state.prizeFlag != -2)
                        _this.showLottery(_this.state.prizeFlag);
                    _this.setState({isStart: false, lastAngle: lastAngle});
                    return false;
                }
            });
            return false;
        }
        $(this.refs.rotate).rotate({
            angle: this.state.lastAngle,
            animateTo: this.state.lastAngle + 360,
            duration: 300,
            callback: _this.rotation,
            easing: function(x, t, b, c, d) {
                return c * (t / d) + b;
            }
        });
	},
    showLottery(prize){
    	// console.log(prize);
	},
    onComplete(){
		console.log('onComplete',this.state);
	},
	render(){
        this.onComplete();
		return (<div className="paysuccess-content show" style={{display: this.state.isShow ? "" : "none"}}>
			<div className="box">
				<h2 className="chouv">投保就抽奖,100%中奖</h2>
				<p className="dianxia">
					<span>点击下方</span>
					<em>“开始抽奖”</em>
					<span>按钮，马上获得精美礼品</span>
				</p>
				<div className="dazhuanb lottery-bg">
					<div className="lottery-wrapper">
						<div className="lottery-area">
							<div className="rotate-area" ref="rotate">
								<div className="award-item award-item1"></div>
								<div className="award-item award-item2"></div>
								<div className="award-item award-item3"></div>
								<div className="award-item award-item4"></div>
								<div className="award-item award-item5"></div>
								<div className="award-item award-item6"></div>
							</div>
							<div className="lottery-btn" onClick={this.start}></div>
						</div>
					</div>
					<div className="baoxiang"></div>
					<div className="baoxiang1"></div>
					<div className="baoxiang2"></div>
				</div>
			</div>
			<div className="box box2">
				<div className="title2">奖品展示</div>
				<ul className="list">
					<li>
						<div className="list-hed b1"><img src="./images/lottery/gift2.png"/></div>
						<div className="list-foot">蓝牙耳机一副</div>
					</li>
					<li>
						<div className="list-hed b2"><img src="./images/lottery/gift6.png"/></div>
						<div className="list-foot">美团60元抵用券</div>
					</li>
					<li>
						<div className="list-hed b3"><img src="./images/lottery/gift4.png"/></div>
						<div className="list-foot">京东e卡50元1张</div>
					</li>
					<li>
						<div className="list-hed b4"><img src="./images/lottery/gift5.png"/></div>
						<div className="list-foot">九阳原汁机1台</div>
					</li>
					<li>
						<div className="list-hed b5"><img src="./images/lottery/gift3.png"/></div>
						<div className="list-foot">Blueair空气净化器</div>
					</li>
					<li>
						<div className="list-hed b6"><img src="./images/lottery/gift1.png"/></div>
						<div className="list-foot">iPhone7 一台</div>
					</li>
				</ul>
			</div>
		</div>);
	}
});

var ReturnVisitBox = React.createClass({
    getInitialState(){
        let order = this.props.order;
        return {isShow: false, order: order, callback: this.props.cb, isConfirm: false};
    },
    componentDidMount(){
    },
    confirmVisit(){
        if(this.state.callback && !this.state.isConfirm){
            this.state.callback();
        }
        this.setState({isShow: false});
    },
    close(){
        this.setState({isShow: false});
    },
    render(){
        if (!!this.state.order && !!this.state.order.detail && !!this.state.order.detail.returnVisit && this.state.order.detail.returnVisit.length > 0) {
        	let appName = this.state.order.detail.applicant.name;	// 投保人姓名
        	let firstPrem = this.state.order.price;	// 保费
        	let regularPrem = this.state.order.detail.plan.premium;	// 年期保费
        	let payPeriod = "";	// 交费期间
        	let insPeriod = "";	// 保障期间
            this.state.order.detail.plan.product.map(v => {
            	if(v.parent == null){
                    payPeriod = v.pay;
                    insPeriod = v.insure;
				}
			});

            return (<div style={{animationDuration: "300ms", display: this.state.isShow ? "" : "none", position: "fixed", top: 0, left: 0, width: "100%", height: "100%", backgroundColor: "#fff", textAlign: "left"}}>
				<div className="common">
					<div className="title">网销在线回访</div>
					<div className="text">
						{this.state.order.detail.returnVisit.map(v => {
							let content = v.content;
							content = content.replace(/\$APP_NAME\$/g, appName);
							content = content.replace(/\$FIRST_PREM\$/g, firstPrem);
							content = content.replace(/\$PAY_PERIOD\$/g, payPeriod);
							content = content.replace(/\$REGULAR_PREM\$/g, regularPrem);
							content = content.replace(/\$INS_PERIOD\$/g, insPeriod);
							return <p className="html" dangerouslySetInnerHTML={{__html:content}}></p>;
                        })}
                        {/*<Summary content={this.state.order.detail.returnVisit}/>*/}
					</div>
					<div className="console">
						<div className="tab">
							<div className="row">
								<div className="col right" onClick={this.confirmVisit}>{this.state.isConfirm ? "您已完成以上回访内容" : "确认以上回访内容"}</div>
							</div>
						</div>
					</div>
				</div>
			</div>);
        }
        return <div></div>;
    }
});

var Ground = React.createClass({
	intervalId: null,
	getInitialState() {
		// ，投保成功后可获得抽奖机会哦
		return {asking:0, title:"处理中", text:"请耐心等待，不要离开页面", memo:"", modify:0, icon:"images/insure_succ.png", order: null};
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
		// if (orderId == null || orderId == "" || orderId != orderId2) {
		// 	ToastIt("已过期");
		// } else {
		// 	common.req("order/view.json", {orderId: orderId}, r => {
		// 		env.order = r;
				this.countDown(160);
			// });
		// }
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
        if(env.order.productId	== "23"){	// 百年人寿
            succTopText = "您的保单已承保，可登陆" + vd.name + "官网查看保单详情。";
		}
        if (!succText)
            succText = !!vd.succTips ? vd.succTips : "投保成功，" + vd.name + "会在承保后进行回访，回访重要，请注意接听";
        if (!failText)
        	failText = !!vd.failTips ? vd.failTips : "请修改后重新提交";

        if (t == 1) {
			s = {modify:0, title:"投保成功", text:text, memo:succText, titleMemo: succTopText, icon:"images/insure_succ.png", hasReturnVisit: (env.order.detail.returnVisit != null)};
            try{this.getUseableCountByOrderNo();}catch (e){}
            try{this.refs.returnVisit.setState({order: env.order, isConfirm: (env.order.extra.isConfirmReturnVisit == true)});}catch(e){}
        } else if (t == 20)
			s = {modify:2, title:"核保失败", text:text, memo:failText, icon:"images/insure_fail.png"};
		else if (t == 21)
			s = {modify:2, title:"投保失败", text:text, memo:failText, icon:"images/insure_fail.png"};
		else if (t == 30)
			s = {modify:1, title:"支付失败", text:text, memo:"请修改支付信息后重新提交", icon:"images/insure_fail.png"};
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
		this.setState({asking: n}, ()=>{
            this.intervalId = setInterval(() => {
                let asking = this.state.asking;
                this.setState({asking: asking-1});
                if (asking <= 0) {
                    clearInterval(this.intervalId);
                    this.finish(90, "请致电客服，确认投保结果");
                } else if(asking % 5 == 0) {
                    common.req("order/view.json", {orderId: common.param("orderId")}, r => {
                        env.order = r;
                        if (r.status == 3) {
                            // r.extra = {iybOrderNo: 'IYB201710161408193477'};
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
		});
	},
    getUseableCountByOrderNo(){
		if(env.order.detail.prizes){
            // common.req('/iybapi/open/activity/iybForwardActivityAjax/getUseableCountByOrderNo.json', {orderNo: env.order.extra.iybOrderNo}, (r)=>{
            common.req('sale/get_useable_count.json', {orderNo: env.order.extra.iybOrderNo}, (r)=>{
                if(r.result != null){
                    this.refs.lottery.setState({isShow: true, orderNo: env.order.extra.iybOrderNo});
                    this.setState({shareObj: {
                        title  : r.result.packageName,
                        desc   : r.result.productDesc,
                        imgUrl : r.result.shareImage,
                        link   : r.result.url
                    }}, ()=>{
						/*try {
						 initShareInfo(this.state.shareObj, () => {
						 this.shareCallback();
						 });
						 } catch (e) {
						 }*/
                        window.wxReady({
                            title  : this.state.shareObj.title,
                            desc   : this.state.shareObj.desc,
                            imgUrl : this.state.shareObj.imgUrl,
                            link   : this.state.shareObj.link
                        }, this.shareCallback);
						/*window.wxShare({
						 title  : this.state.shareObj.title,
						 desc   : this.state.shareObj.desc,
						 imgUrl : this.state.shareObj.imgUrl,
						 link   : this.state.shareObj.link
						 }, null);*/
                        try{
                            window.IYB.setTitle(this.state.shareObj.title || "投保结果");
                            try{
                                document.title = (this.state.shareObj.title || "投保结果");
                            }catch(e){}
                        }catch(e){}
                    });

                }
            }, (r)=>{console.log('IybForwardActivityAjax/share接口出错了');});
		}
	},
	// 抽奖完成回调
	lotteryCallBack(data){
		if(data != null && data.prizeFlag >= 0){
			// this.setState({modalConf: data.option});
			this.refs.modal.setState({isShow: true, giftNum: data.option.num, giftName: data.option.name, giftFreight: data.option.freight == null ? "" : data.option.freight.freightContent, giftNote: data.option.modalNote});
		}
	},
	// 立即领取回调
	getLotteryBack(){
		// console.log(this.refs.share);
		this.refs.share.classList.add("show");
	},
    shareCallback(){
        // common.req('/iybapi/open/activity/IybForwardActivityAjax/newShare.json', {platformId: 2, orderNo: env.order.extra.iybOrderNo}, function(r){
        common.req('sale/new_share.json', {orderNo: env.order.extra.iybOrderNo}, function(r){
            console.log(r);
        }, function(r){console.log('IybForwardActivityAjax/share接口出错了');});
	},
    onClickShareInApp(){
        iHealthBridge.doAction("share", JSON.stringify(this.state.shareObj));
        this.shareCallback();
	},
    showReturnVisit(){
    	this.refs.returnVisit.setState({isShow: true});
	},
	onConfirmVisit(){
		// TODO: 提交回访确认请求
		let _this = this;
        common.req("sale/return_visit.json", {orderId: env.order.id}, r => {
        	if(r.success){
                ToastIt("在线回访成功！");
                _this.refs.returnVisit.setState({isConfirm: true});
			}else{
                ToastIt(r.errMsg);
			}
        }, r => {
            if(r != null){
                ToastIt(r);
            }
            _this.refs.returnVisit.setState({isConfirm: false});
        });
	},
   	render() {
		return (
			<div className="graph" style={{maxWidth: "750px", minWidth: "320px", margin: "0 auto"}}>
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
						!this.state.hasReturnVisit ? null :
							<div style={{paddingBottom:"5px"}}>
								<div style={{height:"40px", lineHeight:"40px", margin:"10px", backgroundColor:"#ffba34"}} className="font-wl" onClick={this.showReturnVisit}>在线回访</div>
							</div>
					}
				</div>
				<LotteryBox ref="lottery" cb={this.lotteryCallBack}/>
				<ModalLottery ref="modal" conf={this.state.modalConf} cb={this.getLotteryBack}/>
				<div ref="share" className="ui-modal paysuccess-share-mask">
					{env.frame == 'iyb' ?
						<div className="paysuccess-share-app">
							<img className="paysuccess-share-img" src="./images/lottery/share-app.png"/>
							<p className="paysuccess-modal-btn-getprize" onClick={this.onClickShareInApp}>分享</p>
                        </div>
						:
						<img className="paysuccess-share-img-h5" src="./images/lottery/share.png"/>}
				</div>
				<ReturnVisitBox ref="returnVisit" order={this.state.order} cb={this.onConfirmVisit}></ReturnVisitBox>
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
        env.frame = "iyb";
        window.IYB.setTitle("投保结果");
        window.IYB.setRightButton(JSON.stringify([{
            title: '关闭',
            func: 'IYB.back()'
        }]));
	}
});