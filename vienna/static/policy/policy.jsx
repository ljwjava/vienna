"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var PolicyForm = React.createClass({
	render() {
        let v = this.props.content;
		return v == null ? null : (
			<form className="form-horizontal">
				<div className="col-sm-11">
					<div className="form-group has-success has-feedback">
						<div className="col-sm-4">
							<label className="control-label col-sm-3">平台</label>
							<div className="col-sm-9">
								<select className="form-control">
									<option>2 - Vienna</option>
									<option>3 - IybWeb</option>
									<option>4 - 云中介</option>
									<option>5 - Q云保</option>
									<option>6 - 保通线下</option>
								</select>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">投保单号</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={v.applyNo}/>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3">保单号</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" defaultValue={v.policyNo}/>
							</div>
						</div>
					</div>
				</div>
			</form>
		);
	}
});

var EndorseForm = React.createClass({
    render() {
        let v = this.props.content;
        return v == null ? null : (
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
        );
    }
});

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
        let payDate = v.pay == null ? null : new Date(v.pay).format("yyyy-MM-dd");
        return (
			<tr key={v.id}>
				<td>{v.type}</td>
				<td>{v.amount}</td>
				<td>{estimateDate}</td>
				<td>{v.auto ? "是" : "否"}</td>
				<td>{v.status}</td>
				<td>{payDate}</td>
				<td>{v.freeze}</td>
				<td>{v.drawee} &rarr; {v.payee}</td>
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
        });
    },
	render() {
        let e = this.state.endorse;
        let c = e == null ? null : e.map(v => {
			return (
				<div className="panel panel-primary">
					<div className="panel-heading">
						<h3 className="panel-title">批改保全（{v.endorseTime}）</h3>
					</div>
					<div className="panel-body">
						<EndorseForm content={v}/>
					</div>
				</div>
			);
		});
        let fee;
        if (this.state.policy != null) {
            let feeReq = this.state.policy == null ? null : {productId: this.state.policy.productId, bizNo: this.state.policy.policyNo, platformId: this.state.policy.platformId};
            fee = <FeeList req={feeReq}/>;
        }
		return (
			<div>
				<br/>
				<div className="panel panel-primary">
					<div className="panel-heading">
						<h3 className="panel-title">保单信息</h3>
					</div>
					<div className="panel-body">
						<PolicyForm content={this.state.policy}/>
						{fee}
					</div>
				</div>
				{c}
				<div className="col-sm-12">
					<div className="container-fluid">
						<div className="collapse navbar-collapse">
							<div className="nav navbar-nav navbar-center">
								<button type="button" className="btn btn-success" onClick={this.save}>&nbsp;&nbsp;保存&nbsp;&nbsp;</button>
								&nbsp;&nbsp;
								<button type="button" className="btn btn-default" onClick={this.cancel}>&nbsp;&nbsp;取消&nbsp;&nbsp;</button>
							</div>
						</div>
					</div>
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