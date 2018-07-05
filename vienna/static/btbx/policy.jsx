"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

env.strOf = function(s1, s2) {
    if (s1 == null || s1 == "")
        return s2;
    return s1;
}

env.policyOf = function(v) {
    return <div>
		<div className="card card-body border-success">
			<div className="form-row">
				<div className="col-md-4">
					<label>业务类型</label>
					<input type="text" className="form-control" value={v.policyType}/>
				</div>
				<div className="col-md-4">
					<label>保单号</label>
					<input type="text" className="form-control" value={v.policyNo}/>
				</div>
				<div className="col-md-4">
					<label>保险公司</label>
					<input type="text" className="form-control" value={v.insCompany}/>
				</div>
			</div>
			<div className="form-row mt-3">
				<div className="col-md-4">
					<label>代理人</label>
					<input type="text" className="form-control" value={v.agent}/>
				</div>
				<div className="col-md-4">
					<label>所在机构</label>
					<input type="text" className="form-control" value={"保通保险代理湖南分公司"}/>
				</div>
				<div className="col-md-4">
					<label>业务来源</label>
					<input type="text" className="form-control" value={v.bizSource}/>
				</div>
			</div>
			<div className="form-row mt-3">
				<div className="col-md-4">
					<label>代理机构</label>
					<input type="text" className="form-control" value={v.agencyName}/>
				</div>
				<div className="col-md-4">
					<label>投保时间</label>
					<input type="text" className="form-control" value={v.applyTime}/>
				</div>
				<div className="col-md-4">
					<label>保单状态</label>
					<input type="text" className="form-control" value={"正常"}/>
				</div>
			</div>
			<div className="form-row mb-3">
				<div className="col-md-4">
					<label>客户</label>
					<input type="text" className="form-control" value={v.customer}/>
				</div>
				<div className="col-md-4">
					<label>车牌号</label>
					<input type="text" className="form-control" value={v.plateNo}/>
				</div>
			</div>
			<div className="form-row">
				<div className="col-md-4">
					<label>条款</label>
					<input type="text" className="form-control" value={v.clause}/>
				</div>
			</div>
		</div>
	</div>;
};

var Main = React.createClass({
    getInitialState() {
        return {};
    },
    back() {
        document.location.href = "policy_list.web";
    },
    componentDidMount() {
        let policyId = common.param("policyId");
        common.req("btbx/policy/view.json", {policyId: policyId}, r => {
            this.setState({policy: r});
        });
    },
    render() {
    	if (this.state.policy == null)
    		return null;

        return (
			<div>
				<nav className="navbar nav-pills flex-column flex-sm-row bg-light border-bottom">
					<div className="form-inline">
						<h5 className="text-primary font-weight-bold mt-sm-1" onClick={this.back}>【保单】</h5>
						<h5 className="mt-sm-1">详情</h5>
					</div>
				</nav>
				<div className="container-fluid mt-3 mb-3">
					{ env.policyOf(this.state.policy) }
				</div>
				<div className="text-center">
					<button className="ml-auto mr-auto btn btn-outline-primary" onClick={this.back}>&nbsp;&nbsp;&nbsp;&nbsp;返回&nbsp;&nbsp;&nbsp;&nbsp;</button>
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