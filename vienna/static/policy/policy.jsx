"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var env = {
    company: {
    },
	dict: {
		feeType: {
			"1": "推广费",
			"101": "续期推广费",
            "2": "上线奖励",
			"3": "代理费",
			"4": "活动奖励"
		},
		feeStatus: {
			"0": "未处理",
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

env.policyOf = function(v, m) {
	if (v == null)
		return null;

	var app = v.target && v.target.applicant ? v.target.applicant : {};
	var ins = v.target && v.target.insurant ? v.target.insurant : {};

	var clauses = !v.detail || !v.detail.clauses ? null:
		<div className="col-sm-12 listC">
			<br/>
			<table>
				<thead>
				<tr>
					<th><div>条款</div></th>
					<th><div>保险期间</div></th>
					<th><div>保额/档次/份数</div></th>
					<th><div>缴费期间</div></th>
					<th>首年保费</th>
				</tr>
				</thead>
				<tbody>
				{
					v.detail.clauses.map(v => {
						return <tr key={v.id}>
							<td>{v.name}</td>
							<td>{v.insure}</td>
							<td>{v.purchase}</td>
							<td>{v.pay}</td>
							<td>{v.premium}</td>
						</tr>;
					})
				}
				</tbody>
			</table>
		</div>

	return [
		<div className="panel panel-primary">
			<div className="panel-heading">
				<h3 className="panel-title">{ m == 1 ? "批改保全（"+v.endorseTime+"）" : "保单信息" }</h3>
			</div>
			<div className="panel-body">
				<div className="col-sm-12">
					<div className="form-group has-success has-feedback col-sm-12">
						<div className="col-sm-4">
							<label className="control-label col-sm-3">出单平台</label>
							<div className="col-sm-9">
								<select className="form-control" defaultValue={ v.platformId}>>
									{Object.keys(env.dict.platform).map(v => <option value={v}>{env.dict.platform[v]}</option>)}
								</select>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">{ m == 1 ? "批单号" : "投保单号" }</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={ m == 1 ? v.endorseNo : v.applyNo }/>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">保单号</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={v.policyNo}/>
							</div>
						</div>
					</div>
					<div className="form-group has-success has-feedback col-sm-12">
						<div className="col-sm-4">
							<label className="control-label col-sm-3">投保人</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={app.name}/>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">投保人性别</label>
							<div className="col-sm-9">
								<select className="form-control" defaultValue={app.gender}>>
									{Object.keys(env.dict.gender).map(v => <option value={v}>{env.dict.gender[v]}</option>)}
								</select>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">投保人生日</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={app.birthday}/>
							</div>
						</div>
					</div>
					<div className="form-group has-success has-feedback col-sm-12">
						<div className="col-sm-4">
							<label className="control-label col-sm-3">投保人证件</label>
							<div className="col-sm-9">
								<div className="input-group">
									<input type="text" className="form-control" defaultValue={app.certNo}/>
									<div className="input-group-btn">
										<button type="button" className="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">{env.strOf(env.dict.certType[app.certType], "？")}</button>
										<ul className="dropdown-menu">
											{ Object.keys(env.dict.certType).map(v => <li><a href="#">{env.dict.certType[v]}</a></li>) }
										</ul>
									</div>
								</div>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">投保人手机</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={app.mobile}/>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">投保人</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={app.email}/>
							</div>
						</div>
					</div>
					<div className="form-group has-success has-feedback col-sm-12">
						<div className="col-sm-4">
							<label className="control-label col-sm-3">被保险人</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={ins.name}/>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">被保险人性别</label>
							<div className="col-sm-9">
								<select className="form-control" defaultValue={ins.gender}>
									{Object.keys(env.dict.gender).map(v => <option value={v}>{env.dict.gender[v]}</option>)}
								</select>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">被保险人生日</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={ins.birthday}/>
							</div>
						</div>
					</div>
					<div className="form-group has-success has-feedback col-sm-12">
						<div className="col-sm-4">
							<label className="control-label col-sm-3">被保险人证件</label>
							<div className="col-sm-9">
								<div className="input-group">
									<input type="text" className="form-control" defaultValue={ins.certNo}/>
									<div className="input-group-btn">
										<button type="button" className="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">{env.strOf(env.dict.certType[ins.certType], "？")}</button>
										<ul className="dropdown-menu">
											{ Object.keys(env.dict.certType).map(v => <li><a href="#">{env.dict.certType[v]}</a></li>) }
										</ul>
									</div>
								</div>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3 col-sm-12">与投保人关系</label>
							<div className="col-sm-9">
								<select className="form-control" defaultValue={ins.relation}>
									{ Object.keys(env.dict.relation).map(v => <option value={v}>{env.dict.relation[v]}</option>) }
								</select>
							</div>
						</div>
					</div>
				</div>
				{ clauses }
			</div>
		</div>
	];
};

env.endorseOf = function(v) {
	return v == null ? null : (
		<div className="panel panel-primary">
			<div className="panel-heading">
				<h3 className="panel-title">批改保全（{v.endorseTime}）</h3>
			</div>
			<div className="panel-body">
				<form className="form-horizontal">
					<div className="col-sm-11">
						<div className="form-group has-success has-feedback">
							<div className="col-sm-4">
								<label className="control-label col-sm-3">批单号</label>
								<div className="col-sm-9">
									<input type="text" className="form-control" defaultValue={v.endorseNo}/>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	);
};

var FeeList = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
    	if (this.props.req != null) common.req("btbx/policy/fee.json", this.props.req, r => {
            this.setState({list:r});
        });
    },
    buildTableLine(v) {
        let estimateDate = v.estimate == null ? null : new Date(v.estimate).format("yyyy-MM-dd");
        let payTime = v.payTime == null ? null : new Date(v.payTime).format("yyyy-MM-dd hh:mm:ss");
        let drawee = v.draweeType != 1 && env.company[v.drawee] ? env.company[v.drawee].name : v.drawee;
        let payee = v.payeeType != 1 && env.company[v.payee] ? env.company[v.payee].name : v.payee;
        return (
			<tr key={v.id}>
				<td>{env.dict.feeType[v.type]}</td>
				<td>{v.amount}</td>
				<td>{estimateDate}</td>
				<td>{v.auto ? "是" : "否"}</td>
				<td>{env.dict.feeStatus[v.status]}</td>
				<td>{payTime}</td>
				<td>{v.freeze}</td>
				<td>{drawee} &rarr; {payee}</td>
			</tr>
        );
    },
    render() {
        return (
			<div className="listC">
				<table>
					<thead>
						<tr>
							<th><div>费用类型</div></th>
							<th><div>金额（元）</div></th>
							<th><div>预计发放时间</div></th>
							<th><div>是否自动发放</div></th>
							<th><div>发放状态</div></th>
							<th><div>实际发放时间</div></th>
							<th><div>冻结天数</div></th>
							<th>费用流向</th>
						</tr>
					</thead>
					<tbody>{ this.state.list == null ? null : this.state.list.map(v => this.buildTableLine(v)) }</tbody>
				</table>
			</div>
        );
    }
});

var Main = React.createClass({
    getInitialState() {
        return {};
    },
	back() {
        document.location.href = "list.web";
    },
    componentDidMount() {
        let policyId = common.param("policyId");
        common.req("btbx/policy/view.json", {policyId:policyId}, r => {
            this.setState({policy: r, endorse: r.endorse});
            common.req("dict/view.json", {company: "btbx", name: "company"}, q => {
                q.company.map(v => { env.company[v.id] = v })
                common.req("dict/view.json", {company: env.company[r.companyId].code, name: "relation,cert"}, s => {
                    s.relation.map(v => { env.dict.relation[v.code] = v.text });
                    s.cert.map(v => { env.dict.certType[v[0]] = v[1] });
                    this.forceUpdate();
                });
            });
        });
    },
	render() {
        let e = this.state.endorse;
        let c = e == null ? null : e.map(v => env.policyOf(v, 1));
        let fee = this.state.policy == null ? null : {productId: this.state.policy.productId, bizNo: this.state.policy.policyNo, platformId: this.state.policy.platformId};
		return (
			<div>
				<br/>
				{ env.policyOf(this.state.policy) }
				{ c }
				<div className="col-sm-12 listC">
                    {fee == null ? null :  <FeeList req={fee}/>}
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