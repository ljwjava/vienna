"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Tabs from '../common/widget.tabs.jsx';
import Form from '../common/widget.form2.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Inputer from '../common/widget.inputer.jsx';
import QuestionBox from '../common/widget.question.jsx';
import Selecter from '../common/widget.selecter.jsx';
import DateEditor from '../common/widget.date.jsx';
import OccupationPicker from '../common/widget.occupationPicker.jsx';
import CityPicker from '../common/widget.cityPicker.jsx';
import Summary from './summary.jsx';
import ToastIt from '../common/widget.toast.jsx';
import Inputer from '../common/widget.inputer.jsx';

class PlanForm extends Form {
	form() {
		if (this.props.fields == null)
			return [];
		var company = this.props.company;
		return this.buildForm(this.props.fields.map(function(v) {
			return {name:v.label, code:v.name, company: company, type:v.widget, refresh:"yes", options:v.detail, value:v.value};
		}));
	}
}

/** 销售资格考试 **/
var QualificationTest = React.createClass({
    getInitialState(){
        let quests = this.props.quests;
        return {isShow: false, quests: quests, callback: this.props.cb, isConfirmAnswer: true};
    },
    val() {
        let res = [];
        if(this.refs != null) {
            for(var qq in this.refs) {
                if(this.refs[qq].props != null && this.refs[qq].props.addtData != null) {
                    let ddd = this.refs[qq].props.addtData;
                    ddd.value = this.refs[qq].val();
                    ddd.qKey = qq;
                    res.push(ddd);
                } else {
                    res.push({
                        qKey: qq,
                        value: this.refs[qq].val()
                    });
                }
            }
        }
        return res;
    },
    componentDidMount(){
        let cmdurl = getUrl('commodity');
        common.postOther(cmdurl + "open/v1/question/checkQuestion/" + common.param("accountId"), null, sr => {
            if(sr.code == 0){
                try{this.setState({isConfirmAnswer: sr.result == true});}catch(e){}
            }else{
                console.log('succ',sr);
            }
        }, er => {
            console.log('err', er);
        });
    },
    confirmAnswer(){
        if(this.isCorrect() == false) {
            ToastIt("请正确回答所有试题");
            return false;
        }
        if(this.state.callback && !this.state.isConfirmAnswer) {
            this.state.callback(this);
        }
        let cmdurl = getUrl('commodity');
        common.postOther(cmdurl + "open/v1/question/recordQuestionResult/" + common.param("accountId"), null, sr => {
            if(sr.code == 0){
                ToastIt(sr.message);
                this.setState({isShow: false});
            }else{
                console.log('succ',sr);
                ToastIt(sr.message);
            }
        }, er => {
            console.log('err', er);
            ToastIt("提交失败，请稍后重试");
        });
    },
    close(){
        this.setState({isShow: false});
    },
    isCorrect(){
        let hasRes = false;
        let res = null;
        if(this.refs != null) {
            for(var qq in this.refs) {
                if(this.refs[qq].props != null) {
                    let ic = this.refs[qq].isCorrect();
                    if(ic != null && ic == false){
                        res = false;
                    }
                    if(ic != null){
                        hasRes = true;
                    }
                }
            }
            if(hasRes && res == null){
                res = true;
            }
        }
        return res;
    },
    render(){
        if (!this.state.isConfirmAnswer && !!this.state.quests && this.state.quests.length > 0) {
            return (<div style={{animationDuration: "300ms", display: this.state.isShow ? "" : "none", position: "fixed", zIndex: 2, top: 0, left: 0, width: "100%", height: "100%", backgroundColor: "#fff", textAlign: "left"}}>
				<div className="common">
					<div className="title">销售资格考试</div>
					<div className="text" style={{padding:"5px 10px 95px 10px", overflowY: "scroll", height: "100%"}}>
                        {this.state.quests.map(v => {
                            let content = v.content;
                            if(v.type == "html") {
                                return <div className="html" dangerouslySetInnerHTML={{__html:content}}></div>;
                            } else if (v.type == "quest"){
                                let qIdx = content.seqIdx;
                                let qCode = content.code;
                                let qType = content.type;
                                let qIsReq = content.isReq;
                                let addtData = {
                                    seqIdx: qIdx,
                                    code: qCode,
                                    type: qType,
                                    isReq: qIsReq
                                };

                                return <QuestionBox ref={qCode} code={content.code} options={content.options} answer={content.answer} autoAlert={content.autoAlert} isReq={content.isReq} type={content.type} title={content.title} seqIdx={content.seqIdx} addtData={addtData}/>;
                            }
                        })}
					</div>
					<div className="console">
						<div className="tab">
							<div className="row">
								<div className="col right" onClick={this.confirmAnswer.bind(this)}>完成并提交</div>
							</div>
						</div>
					</div>
				</div>
			</div>);
        }
        return <div></div>;
    }
});

var timer;
var Ware = React.createClass({
    qualifAndNext(func){
        if(this.refs.qualifQuests != null && this.refs.qualifQuests.state.isConfirmAnswer == false) {
            this.refs.qualifQuests.setState({isShow: true, callback: ()=> {
                if(func){
                    func();
                }
            }});
        } else {
            if(func){
                func();
            }
        }
    },
    saveAndNext(func){
        this.qualifAndNext(()=>{
            common.save("iyb/temp", JSON.stringify(this.refs.plan.val()));
            if(func){
                func();
            }
        });
    },
    next(){
        if(!!env.docs && (env.docs.underwriting != null || !!env.docs.quests && env.docs.quests.length > 0)){
            this.saveAndNext(this.openQuest);
        } else {
            this.saveAndNext(this.apply);
        }
    },
	openQuest() {
        if (env.pack.uwUrl) {
            let factors = this.refs.plan.val();
            factors.url = this.getApplyUrl();
        	common.req("underwriting/create.json", {productId:this.state.detail.target, answer:factors}, v => {
                document.location.href = env.pack.uwUrl + "?uwId=" + v + "&accountId=" + common.param("accountId");
			});
        } else {
            this.quest();
        }
        this.setState({isShowActBanner: false});
	},
	quest() {
		this.setState({quest: true, alertQuest: false});
	},
	alertQuest() {
		this.setState({quest: true, alertQuest: true});
	},
	back() {
		this.setState({quest: false, alertQuest: false, isShowActBanner: true});
	},
	getApplyUrl() {
        let plus = window.location.search;
        if (plus != null && plus != "")
            plus = "&" + plus.substring(1);

        var nextUrl = null;
        if (env.packType == 1)
            nextUrl = "life_apply.mobile";
        else if (env.packType == 3)
            nextUrl = "vac_apply.mobile";

        if (nextUrl != null) {
            if(plus.indexOf("packId=") >= 0) {
                nextUrl = nextUrl + "?" + (plus.replace(/(packId=)([^&]*)/gi, "packId=" + env.packId));
            } else {
                nextUrl = nextUrl + "?packId=" + env.packId + plus;
            }
        } else {
        	nextUrl = null;
		}

        return nextUrl;
	},
    apply() {
		let plus = window.location.search;
		if (plus != null && plus != "")
			plus = "&" + plus.substring(1);

		common.save("iyb/orderId", "");

		var nextUrl = null;
		if (env.packType == 1)
			nextUrl = "life_apply.mobile";
		else if (env.packType == 3)
            nextUrl = "vac_apply.mobile";

		if (nextUrl != null) {
			if(plus.indexOf("packId=") >= 0){
                document.location.href = nextUrl + "?" + (plus.replace(/(packId=)([^&]*)/gi, "packId=" + env.packId));
			} else {
                document.location.href = nextUrl + "?packId=" + env.packId + plus;
			}
		} else {
            ToastIt("error");
        }
    },
    getInitialState() {
    	let r = {quest:false, alertQuest:false, vendor:{}, bannerTop: 0, isShowActBanner: false};
    	let v = this.props.detail;
    	if (v != null && v.detail != null && v.detail.length > 1) {
            r.packs = [];
    		for (var i=0;i<v.detail.length;i++) {
                 r.packs.push([i, v.detail[i].name]);
            }
    	}
    	return r;
    },
    componentWillMount(){
        /** GPO 埋点 **/
        if(!common.param("accountId") || !this.props.detail.code) return;
        let gpourl = getUrl('gpo');
        try{common.post(gpourl + "stat/action.json", {action:'PRODUCT/ESTIMATE', plus:{productId: this.props.detail.code}, accountId: common.param("accountId"), url:document.location.href}, function(r){}, function(r){});}catch(e){}
	},
    /** 初始化顶层活动banner  **/
    initTopActivityBanner(accountId, productId, productCode){
        try{
        	if(!accountId || !productId) return;

            common.req("gpo/npo/temp.json", {activity:'product', event:'activity_banner', account: accountId, productId: productId, productCode: productCode},
                r => {
                    if(!r) return;
					$(this.refs.top_activity_banner).html(r.h).show();
					if(r.t == "fixed"){
						$(this.refs.top_banner).css('margin-top',$(this.refs.top_activity_banner).find("div:first-child").css('height'));
					}
                    this.setState({isShowActBanner: true});
                }, function(r){console.log(r);});
        }catch(e){
            console.log(e);
        }
	},
    /** 准备分享数据 **/
    sharePrd() {
        this.qualifAndNext(()=>{
            var shareObj = env.getShareObj();
            // iHealthBridge.doAction("share", JSON.stringify(shareObj));
            window.JSBridge.share(shareObj, null);
        });
    },
    shareApp(shareObj){
        let _this = this;
        // window.iHealthBridge.doAction("setRightButton", JSON.stringify({title: "分享", action: "javascript:env.sharePrd();", color: "#ffffff", font: "17"}));
        if (window.iHealthBridge && typeof window.JSBridge !== 'undefined') {
            // window.IYB.setRightButton(JSON.stringify([{img: 'https://cdn.iyb.tm/app/config/img/share_btn.png', func: 'javascript:env.sharePrd();'}]));
            window.JSBridge.ready(()=>{
                window.JSBridge.setTitle(document.title);
                // window.IYB.setTitle(document.title);
                window.JSBridge.register('regShare', () => {
                    _this.sharePrd();
                });
                window.JSBridge.setRightButton({
                    imageUrl: 'https://cdn.iyb.tm/app/config/img/share_btn.png',
                    action: window.JSBridge.getRegMethod('regShare')
                });
                env.getShareObj(shareObj);
            });
            if(timer) {clearTimeout(timer);}
            return;
        }
        timer = setTimeout(()=>{
            _this.shareApp(shareObj);
        }, 200);
    },
    readyShare(shareObj){
        var UA = window.navigator.userAgent.toLowerCase();
        var isInApp = !!~UA.indexOf('iyunbao') || (typeof iHealthBridge !== 'undefined');

        if (isInApp) {
            env.frame = "iyb";
            this.shareApp(shareObj);
        } else {
            window.wxReady(env.getShareObj(shareObj), null);
        }
    },
    componentDidMount() {
		this.changePlan(0);
		this.initTopActivityBanner(common.param("accountId"), this.props.detail.code, null);
        try{this.readyShare();}catch(e){}
    },
	changePlan(i) {
        let v = this.props.detail;
        let r = {detail:v.detail[i]};
        env.packId = r.detail.target;
        common.req("sale/detail.json", {packId: env.packId}, s => {
        	env.pack = s;
            env.docs = s.extra;
            env.packType = s.type;
            env.vendor = s.vendor;
            env.company = s.vendor.code;
            env.kefuUrl = s.extra.kefuUrl;
            r.form = s.form;
            r.vendor = s.vendor;
            r.company = s.vendor.code;
            if (r.detail.summary == null) {
                r.detail.summary = env.pack.show;
			}
            if (r.detail.summary != null) {
                r.exps = [];
                for (let label in r.detail.summary) {
                    r.exps.push([label, label]);
                    if (r.summary == null)
                        r.summary = r.detail.summary[label];
                }
            }
            env.banner = env.ware.banner[0];
            this.setState(r, () => {
            	// console.log(this.state.packs);
            	if(this.refs.detailTabs) {
                    this.refs.detailTabs.setState({code: this.refs.detailTabs.props.options[0][0]});
				}
            	this.refreshPremium();
            });
            // 判断是否指定单个计划
			let packIds = common.param("packIds");
			if(packIds != null && packIds.indexOf(",") < 0) {
				if(s.extra.shareObject != null && JSON.stringify(s.extra.shareObject) != "{}") {
                    if(env.shareObj == null) {
                        env.shareObj = {
                            title: s.extra.shareObject.title,
                            desc: s.extra.shareObject.desc,
                            thumb: s.extra.shareObject.imgUrl,
                            imgUrl: s.extra.shareObject.imgUrl,
                            link: window.location.href
                        };
                    } else {
                        env.shareObj = {
                            title: s.extra.shareObject.title,
                            desc: s.extra.shareObject.desc,
                            thumb: s.extra.shareObject.imgUrl,
                            imgUrl: s.extra.shareObject.imgUrl,
                            link: env.shareObj.link
                        };
                    }
                    this.readyShare(env.shareObj);
				}
				if(s.extra.banner != null) {
					env.banner = s.extra.banner;
				}
			}
        });
	},
    /* getPlanFactors() {
    	let factors = {packId: this.state.detail.target};
    	let self = this.refs.plan;
    	self.props.fields.map(v => {
    		if (self.refs[v.name]) {
				factors[v.name] = self.refs[v.name].val();
    		}
    	});
    	return factors;
    }, */
    refreshPremium() {
		let factors = this.refs.plan.val();
		factors.packId = this.state.detail.target;
        if (factors.ZONE != null && factors.ZONE.code != null) {
            factors.ZONE = factors.ZONE.code;
		} else {
            factors.ZONE = null;
		}
        if (factors.SOCIAL_ZONE != null && factors.SOCIAL_ZONE.code != null) {
            factors.SOCIAL_ZONE = factors.SOCIAL_ZONE.code;
        } else {
            factors.SOCIAL_ZONE = null;
        }
		if(factors.OCCUPATION_C != null) {
            factors.OCCUPATION_L = factors.OCCUPATION_C.level != null ? factors.OCCUPATION_C.level : null;
            factors.OCCUPATION_C = factors.OCCUPATION_C.code != null ? factors.OCCUPATION_C.code : null;
		}else{
        	factors.OCCUPATION_L = null;
		}
		env.factors = factors;
		common.req("sale/perform.json", {platformId:2, opt:"try", content:factors}, r => {
			var form = r.form == null ? this.state.form : r.form;
			if (r.factors != null) form.map(function(e) {
				var res = r.factors[e.name];
				if (res != null)
					e.value = res;
			});
			this.setState({premium:r.total, rules:r.rules, alert:r.alert, vals:r, form:form});
		});
	},
    onSummary(code) {
    	this.setState({summary: this.state.detail.summary[code]});
    },
	openPoster() {
		document.location.href = "iyunbao://poster?code=" + (common.param("qrCode") || env.pack.wareCode);
	},
    openKF() {
		document.location.href = env.kefuUrl;
	},
    /* 销售资质问卷答案 */
    /*onConfirmQualifQests(){
        // QualificationTest
        // this.refs.qualifQuests.confirmAnswer();
    },*/
   	render() {
		if (this.state.quest && !!env.docs && !!env.docs.quests && env.docs.quests.length > 0) {
			var exempt = env.factors.EXEMPT;	// 轻症豁免
			var appExempt = env.factors.A_EXEMPT;	// 投保人豁免
			var questTitle = "被保险人";
			var quests = env.docs.quests;
			var questsPlus = null;
			if (appExempt == "Y" || exempt == "Y") {
				if (env.docs.allQuests) {
                    questTitle = "投保人及被保险人";
                    quests = env.docs.allQuests;
                } else if (env.docs.applicantQuests) {
                    questsPlus = [
						<div className="title">健康及财务告知（投保人）</div>,
						<div className="text" style={{overflow:"auto"}}>
							<Summary content={env.docs.applicantQuests}/>
						</div>
                    ];
                } else {
                    questTitle = "投保人及被保险人";
				}
			}
			return (
				<div className="common" style={{maxWidth: "750px", minWidth: "320px", marginLeft: "auto", marginRight: "auto"}}>
					<div style={{display: "none"}}></div>
					<div className="title">健康及财务告知（{questTitle}）</div>
					<div className="text" style={{overflow:"auto"}}>
						<Summary content={quests}/>
					</div>
					{questsPlus}
					<div className="console">
						<div className="tab">
							<div className="row">
								<div className="col left" onClick={this.alertQuest}>是</div>
								<div className="col right" onClick={this.apply}>全否，继续投保</div>
							</div>
						</div>
					</div>
					{ !this.state.alertQuest ? null :
						<div className="notice">
							<div className="content">
								<br/>
								很抱歉，{ appExempt == "Y" || exempt == "Y" ? "投保人或被保险人" :"被保险人"}的健康状况不满足该保险的投保规定<br/>
								如有疑问，请联系{this.state.vendor.name}客服<br/>
								<br/>
								<a href={"tel:"+env.docs.telephone}>{env.docs.telephone}</a>
								<div className="console">
									<div className="tab">
										<div className="row">
											<div className="col left" onClick={this.quest}>选错啦</div>
											<div className="col right" onClick={this.back}>返回试算页</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					}
				</div>
			);
		}

		let v = this.props.detail;
        let r1 = this.state.rules == null ? null : this.state.rules.map((r,i) => (<div className="error" key={i}>错误：{r}</div>));
        let r2 = this.state.alert == null ? null : this.state.alert.map((r,i) => (<div className="alert" key={i}>备注：{r}</div>));

		return (
			<div className="common" style={{maxWidth: "750px", minWidth: "320px", marginLeft: "auto", marginRight: "auto"}}>
				<div className="top-activity-banner" ref="top_activity_banner" style={{display: this.state.isShowActBanner ? "block" : "none"}}></div>
				<div ref="top_banner" style={{marginTop: this.state.bannerTop || 0}}>
					<div style={{position: "relative"}}>
						<img src={env.banner} style={{width:"100%", height:"auto"}}/>
						<div style={{width: "100%", position:"absolute", bottom: "0", paddingTop:"5px", paddingBottom:"5px", zIndex:"1", textAlign:"center", color:"#FFF", backgroundColor:"rgba(66,66,66,0.7)"}}>
							<div className="font-wl">{v.name}</div>
							<div className="font-wm">{v.remark}</div>
						</div>
					</div>
                    { this.state.packs == null ? null :
						<Tabs onChange={this.changePlan} options={this.state.packs}/>
                    }
					{ this.state.form == null ? null :
						<div className="form">
							<PlanForm ref="plan" parent={this} fields={this.state.form} company={this.state.company} onRefresh={this.refreshPremium}/>
							<div style={{paddingTop:"10px"}}>{r1}{r2}</div>
						</div>
					}
				</div>
				<div className="font-bm" style={{height:"40px", lineHeight:"40px", textAlign:"center", borderBottom: "1px solid #CCC"}}>
					{ this.state.vendor.logo == null ? null : <img src={this.state.vendor.logo} style={{height:"20px", verticalAlign:"middle", paddingBottom:"5px"}}/> }
					&nbsp;&nbsp;由{this.state.vendor.name}承保并负责理赔
				</div>
				{ this.state.exps == null ? null :
					<Tabs ref="detailTabs" onChange={this.onSummary} options={this.state.exps}/>
				}
				{ this.state.summary == null ? null :
					<Summary content={this.state.summary} vals={this.state.vals == null ? null : this.state.vals.summary}/>
				}
				<div className="console">
					<div className="tab">
						<div className="row">
                            {env.frame == "iyb" ? <div className="col rect" onClick={this.openPoster}>海报</div> : null}
                            {env.frame == "iyb" && !!env.kefuUrl ? <div className="col rect" onClick={this.openKF}>客服</div> : null}
							<div className="col left">{env.pack != null && env.pack.applyMode == 1 ? "首期" : ""}保费：{!this.state.premium || this.state.premium <= 0 ? "无法计算" : this.state.premium}</div>
							<div className="col right" onClick={this.next.bind(this)}>去投保</div>
						</div>
					</div>
				</div>
                {env.pack != null && env.pack.extra != null ? <QualificationTest ref="qualifQuests" quests={env.pack.extra.qualifQuests}></QualificationTest> : null}
			</div>
		);
	}
});

env.getShareObj = function(shareObj){
	if(env.shareObj == null) {
        env.shareObj = {
            title: env.ware.name,
            desc: env.ware.remark,
            thumb: env.ware.logo,
            imgUrl: env.ware.logo,
            link: window.location.href
        };
	}
	if(shareObj != null) {
		env.shareObj = shareObj;
	}

	var cluburl = getUrl('club');
	if(!!cluburl && !env.newShareUrl){
        $.ajax({url:cluburl + "open/v2/reception/link/newShareLink", type:"POST", data:JSON.stringify({originalLink: env.shareObj.link, pageCode: 'cp', pageId: env.ware.code, pageTitle: env.shareObj.title, accountId: common.param("accountId"), pageIcon: env.shareObj.imgUrl}), async: false, xhrFields: { withCredentials: true }, contentType:'application/json;charset=UTF-8',
			success: (r)=> {
            if (r.isSuccess+"" == "true") {
                if(!!r.result.shareLink){
                	env.newShareUrl = r.result.shareLink;
                    env.shareObj.link = env.newShareUrl;
				}
            } else {
                ToastIt(r.errorMsg);
            }
        }, fail: (r) => {
        	ToastIt("访问服务器失败");
        }, dataType:"json"});
	}else{
        env.shareObj.link = env.newShareUrl;
	}
    env.shareObj.productId = env.ware.code;
    return env.shareObj;
};


var getUrl = function(key){
    if(!env.url){
        common.reqSync("util/env_conf.json", {}, r => {
            if(!!r && !!r.url) {
                env.url = r.url;
            }
        }, r => {});
    }
    if(key == null || key == ""){
        return env.url;
    }else{
        return env.url[key];
    }
};

$(document).ready( function() {
	if(common.param("wareId") == 8) {
		var urlp = document.location.href.split("wareId")[0];
		var urln = document.location.href.split("wareId")[1];
		window.location.href = urlp+"wareId=21" + (urln.substr(urln.indexOf("&")));
	}

	common.req("sale/view.json", {wareId:common.param("wareId"), packIds: common.param("packIds")}, function (r) {
		env.ware = r;
        try{
            document.title = r.name;
        }catch(e){}

		ReactDOM.render(
			<Ware detail={r}/>, document.getElementById("content")
		);

        // this.readyShare();
	});

});