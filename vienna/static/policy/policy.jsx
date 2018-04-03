"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var env = {
    company: {},
    dict: {
        feeType: {
            "1": "推广费",
            "2": "关联奖励",
            "3": "活动奖励"
        },
        feeStatus: {
            "0": "未结算",
            "1": "已发放",
            "9": "发放失败"
        },
        gender: {
            "M": "男",
            "F": "女"
        },
        certType: [
        ],
        platform: {
            "2": "VIENNA",
            "3": "IYBAPP",
            "4": "云中介",
            "5": "Q云保",
            "6": "保通线下"
        },
        relation: {
            "self": "本人（iyb）",
            "coupon": "配偶（iyb）",
            "lineal": "父母子女（iyb）"
        }
    }
}

env.strOf = function(s1, s2) {
    if (s1 == null || s1 == "")
        return s2;
    return s1;
}

env.policyOf = function(v) {
    var app = v.target && v.target.applicant ? v.target.applicant : {};
    var ins = v.type == 1 && v.target && v.target.insurant ? v.target.insurant : null;
    var vec = v.type == 2 && v.target && v.target.vehicle ? v.target.vehicle : null;

    return <div>
		<div className="card card-body border-success">
			<div className="form-row">
				<div className="col-md-4">
					<label>保单号</label>
					<input type="text" className="form-control" defaultValue={v.policyNo}/>
				</div>
				<div className="col-md-4">
					<label>投保单号</label>
					<input type="text" className="form-control" defaultValue={v.applyNo}/>
				</div>
				<div className="col-md-4">
					<label>保险公司</label>
					<select className="form-control" defaultValue={v.vendorId}>>
						{Object.keys(env.company).map(v => <option value={v}>{env.company[v].name}</option>)}
					</select>
				</div>
			</div>
			<div className="form-row mt-3">
				<div className="col-md-4">
					<label>出单人</label>
					<input type="text" className="form-control" defaultValue={v.ownerName}/>
				</div>
				<div className="col-md-4">
					<label>出单人所在机构</label>
					<input type="text" className="form-control" defaultValue={v.ownerCompanyName}/>
				</div>
				<div className="col-md-4">
					<label>出单人所在组织</label>
					<input type="text" className="form-control" defaultValue={v.ownerOrgName}/>
				</div>
			</div>
			<div className="form-row mt-3">
				<div className="col-md-4">
					<label>代理机构</label>
					<input type="text" className="form-control" defaultValue={v.agencyName}/>
				</div>
			</div>
		</div>
		<div className="card card-body border-success mt-3">
			<div className="form-row mb-3">
				<div className="col-md-4">
					<label>投保人</label>
					<input type="text" className="form-control" defaultValue={app.name}/>
				</div>
				<div className="col-md-4">
					<label>投保人性别</label>
					<select className="form-control" defaultValue={app.gender}>>
						{Object.keys(env.dict.gender).map(v => <option value={v}>{env.dict.gender[v]}</option>)}
					</select>
				</div>
				<div className="col-md-4">
					<label>投保人生日</label>
					<input type="text" className="form-control" defaultValue={app.birthday}/>
				</div>
			</div>
			<div className="form-row">
				<div className="col-md-4">
					<label>投保人证件</label>
					<div className="input-group">
						<input type="text" className="form-control" defaultValue={app.certNo}/>
						<div className="input-group-append">
							<button type="button" className="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">{env.strOf(env.dict.certType[app.certType], "？")}</button>
							<ul className="dropdown-menu">
								{ Object.keys(env.dict.certType).map(v => <li><a href="#">{env.dict.certType[v]}</a></li>) }
							</ul>
						</div>
					</div>
				</div>
				<div className="col-md-4">
					<label>投保人手机</label>
					<input type="text" className="form-control" defaultValue={app.mobile}/>
				</div>
				<div className="col-md-4">
					<label>投保人Email</label>
					<input type="text" className="form-control" defaultValue={app.email}/>
				</div>
			</div>
		</div>
		{ !ins ? null :
			<div className="card card-body border-success mt-3">
				<div className="form-row">
					<div className="col-md-4">
						<label>被保险人</label>
						<input type="text" className="form-control" defaultValue={ins.name}/>
					</div>
					<div className="col-md-4">
						<label>被保险人性别</label>
						<select className="form-control" defaultValue={ins.gender}>
							{Object.keys(env.dict.gender).map(v => <option value={v}>{env.dict.gender[v]}</option>)}
						</select>
					</div>
					<div className="col-md-4">
						<label>被保险人生日</label>
						<input type="text" className="form-control" defaultValue={ins.birthday}/>
					</div>
				</div>
				<div className="form-row mt-3">
					<div className="col-md-4">
						<label>被保险人证件</label>
						<div className="input-group">
							<input type="text" className="form-control" defaultValue={ins.certNo}/>
							<div className="input-group-append">
								<button type="button" className="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">{env.strOf(env.dict.certType[ins.certType], "？")}</button>
								<ul className="dropdown-menu">
									{ Object.keys(env.dict.certType).map(v => <li><a href="#">{env.dict.certType[v]}</a></li>) }
								</ul>
							</div>
						</div>
					</div>
					<div className="col-md-4">
						<label>与投保人关系</label>
						<select className="form-control" defaultValue={ins.relation}>
							{ Object.keys(env.dict.relation).map(v => <option value={v}>{env.dict.relation[v]}</option>) }
						</select>
					</div>
				</div>
			</div>
		}
		{ !vec ? null :
			<div className="card card-body border-success mt-3">
				<div className="form-row">
					<div className="col-md-4">
						<label>车牌号</label>
						<input type="text" className="form-control" defaultValue={vec.plateNo}/>
					</div>
					<div className="col-md-4">
						<label>车架号</label>
						<input type="text" className="form-control" defaultValue={vec.frameNo}/>
					</div>
				</div>
			</div>
		}
	</div>;
};

env.clauseOf = function(v) {
    var cl = !v.clauses ? [] : v.clauses.map(v =>
		<tr key={v.id}>
			<td>{v.clauseName}</td>
			<td>{v.insure}</td>
			<td>{v.purchase}</td>
			<td>{v.pay}</td>
			<td>{v.premium}</td>
		</tr>
    );

    return cl.length == 0 ? null:
		<table className="table table-bordered mt-3">
			<thead className="thead-light">
			<tr>
				<th><div>条款</div></th>
				<th><div>保险期间</div></th>
				<th><div>保额/档次/份数</div></th>
				<th><div>缴费期间</div></th>
				<th>首年保费</th>
			</tr>
			</thead>
			<tbody>{cl}</tbody>
		</table>
}

var ChannelFee = React.createClass({
    getInitialState() {
        return {list: []};
    },
    componentDidMount() {
        let req = {bizType: 2, bizId: this.props.policy.id}
        if (this.props.req != null) common.req("policy/channel_fee.json", req, r => {
            this.setState({list: r});
        });
    },
    channelOf(v) {
        let estimateDate = v.estimate == null ? null : new Date(v.estimate).format("yyyy-MM-dd");
        let payTime = v.payTime == null ? null : new Date(v.payTime).format("yyyy-MM-dd hh:mm:ss");
        let payer = env.company[v.payer] ? env.company[v.payer].name : v.payer;
        let drawer = env.company[v.drawer] ? env.company[v.drawer].name : v.drawer;
        return (
			<tr key={v.id}>
				<td>{v.type == 1 ? "渠道代理费" : v.type == 2 ? "技术服务费" : null}</td>
				<td>{v.productName}</td>
				<td>{v.amount}</td>
				<td>{estimateDate}</td>
				<td>{env.dict.feeStatus[v.status]}</td>
				<td>{payTime}</td>
				<td>{payer} &rarr; {drawer}</td>
			</tr>
        );
    },
    render() {
        return (
			<div>
				<table className="table table-bordered mt-3">
					<thead className="thead-light">
					<tr>
						<th>费用类型</th>
						<th>条款</th>
						<th>金额（元）</th>
						<th>预计结算时间</th>
						<th>发放状态</th>
						<th>实际结算时间</th>
						<th>费用流向</th>
					</tr>
					</thead>
					<tbody>{ this.state.list.map(v => this.channelOf(v)) }</tbody>
				</table>
			</div>
        );
    }
});

var AgentFee = React.createClass({
    getInitialState() {
        return {list: []};
    },
    componentDidMount() {
        if (this.props.req != null) common.req("policy/agent_fee.json", this.props.req, r => {
            this.setState({list: r});
        });
    },
    pay(feeId) {
        common.req("policy/fee/pay_agent.json", {feeList: [feeId]}, r => {
            alert(r + "笔费用已发放");
        });
    },
    agentOf(v) {
        let estimateDate = v.estimate == null ? null : new Date(v.estimate).format("yyyy-MM-dd");
        let payTime = v.payTime == null ? null : new Date(v.payTime).format("yyyy-MM-dd hh:mm:ss");
        return (
			<tr key={v.id}>
				<td>{env.dict.feeType[v.type]}</td>
				<td>{v.amount}</td>
				<td>{common.round(v.amount * 100 / this.props.policy.premium, 0)}%</td>
				<td>{estimateDate}</td>
				<td>{v.auto ? "是" : "否"}</td>
				<td>{env.dict.feeStatus[v.status]}</td>
				<td>{payTime}</td>
				<td>{v.freeze}</td>
				<td>平台 &rarr; 代理人{v.drawer}</td>
				<td style={{padding:"6px"}}>
                    {v.payTime == null ? <button className="btn btn-outline-danger mr-1" onClick={this.pay.bind(this, v.id)}>发放</button> : null}
				</td>
			</tr>
        );
    },
    render() {
        return (
			<table className="table table-bordered mt-3 text-center">
				<thead className="thead-light">
				<tr>
					<th>费用类型</th>
					<th>金额（元）</th>
					<th>比例（％）</th>
					<th>预计发放时间</th>
					<th>是否自动发放</th>
					<th>发放状态</th>
					<th>实际发放时间</th>
					<th>冻结天数</th>
					<th>费用流向</th>
					<th>操作</th>
				</tr>
				</thead>
				<tbody>{ this.state.list.map(v => this.agentOf(v)) }</tbody>
			</table>
        );
    }
});

var ClauseList = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
        if (this.props.req != null) common.req("policy/fee.json", this.props.req, r => {
            this.setState({list1: r.agent, list2: r.channel});
        });
    },
    channelOf(v) {
        let estimateDate = v.estimate == null ? null : new Date(v.estimate).format("yyyy-MM-dd");
        let payTime = v.payTime == null ? null : new Date(v.payTime).format("yyyy-MM-dd hh:mm:ss");
        let payer = env.company[v.payer] ? env.company[v.payer].name : v.payer;
        let drawer = env.company[v.drawer] ? env.company[v.drawer].name : v.drawer;
        return (
			<tr key={v.id}>
				<td>渠道费用</td>
				<td>{v.productName}</td>
				<td>{v.amount}</td>
				<td>{estimateDate}</td>
				<td>{env.dict.feeStatus[v.status]}</td>
				<td>{payTime}</td>
				<td>{payer} &rarr; {drawer}</td>
			</tr>
        );
    },
    render() {
        return (
			<table className="table table-bordered mt-3">
				<thead className="thead-light">
				<tr>
					<th>费用类型</th>
					<th>条款</th>
					<th>金额（元）</th>
					<th>预计结算时间</th>
					<th>发放状态</th>
					<th>实际结算时间</th>
					<th>费用流向</th>
				</tr>
				</thead>
				<tbody>{ this.state.list2 == null ? null : this.state.list2.map(v => this.channelOf(v)) }</tbody>
			</table>
        );
    }
});

var Main = React.createClass({
    getInitialState() {
        return {tab: "base"};
    },
    back() {
        document.location.href = "life_list.web";
    },
	change(tab) {
		this.setState({tab: tab})
	},
	tabClassOf(tab) {
		return "flex-sm-fill text-sm-center nav-link " + (this.state.tab == tab ? "active" : "disabled");
	},
    componentDidMount() {
        let policyId = common.param("policyId");
        common.req("policy/view.json", {policyId: policyId}, r => {
            this.setState({policy: r});
            common.req("channel/company.json", {}, r1 => {
                if (r1 != null) env.company = r1;
                common.req("dict/view.json", {company: env.company[r.vendorId].code, name: "relation,cert"}, s => {
                    s.relation.map(v => { env.dict.relation[v.code] = v.text });
                    s.cert.map(v => { env.dict.certType[v[0]] = v[1] });
                    this.forceUpdate();
                });
            });
        });
    },
    render() {
    	if (this.state.policy == null)
    		return null;

        var tabs = [["base", "基础信息"], ["endorse", "批改保全"], ["channel", "渠道费用"], ["agent", "个人费用"]];
        var body;
        if (this.state.tab == "base") {
            body = [
            	env.policyOf(this.state.policy),
                env.clauseOf(this.state.policy),
				<div className="container-fluid mt-3 text-center">
					<button className="btn btn-danger mr-2">
						<span className="glyphicon glyphicon-yen mr-1"></span> 生成费用
					</button>
					<button className="btn btn-danger mr-5">
						<span className="glyphicon glyphicon-exclamation-sign mr-1"></span> 退保退费
					</button>
					<button className="btn btn-success mr-2">
						<span className="glyphicon glyphicon-ok mr-1"></span> 保存
					</button>
					<button className="btn btn-outline-dark" onClick={this.back}>
						<span className="glyphicon glyphicon-remove mr-1"></span> 取消
					</button>
				</div>
            ];
        } else if (this.state.tab == "channel") {
            let biz = {bizType: 2, bizId: this.state.policy.id};
            body = <ChannelFee req={biz} policy={this.state.policy}/>
        } else if (this.state.tab == "agent") {
            let biz = {bizType: 2, bizId: this.state.policy.id};
            body = <AgentFee req={biz} policy={this.state.policy}/>
        } else {
        	body = null;
		}

        return (
			<div>
				<nav className="navbar nav-pills flex-column flex-sm-row bg-light border-bottom">
					<div className="form-inline">
						<h5 className="text-primary font-weight-bold mt-sm-1">【保单】</h5>
						<h5 className="mt-sm-1">{this.state.policy.applicantName + "（" + this.state.policy.policyNo + "）"}</h5>
					</div>
					<div className="form-inline">
                    { tabs.map(v =>
						<a className={this.tabClassOf(v[0])} href="#" onClick={this.change.bind(this, v[0])}>{v[1]}</a>
                    )}
					</div>
				</nav>
				<div className="container-fluid mt-3">
					{ body }
				</div>
			</div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(
		<Main/>, document.getElementById("content")
    );
});