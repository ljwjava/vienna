"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 20
}

env.upload = function() {
    document.location.href = "upload.web";
}

class PolicyList extends List {
    open(id) {
        document.location.href = "policy.web?policyId=" + id;
    }
    refresh() {
        common.req("policy/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th>保单code</th>
				<th>主要产品</th>
				<th>投保人</th>
				<th>投保时间</th>
				<th>保费</th>
				<th>保单状态</th>
				<th></th>
			</tr>
        );
    }
    buildTableLine(v) {
        let date = new Date(v.updateTime);
        return (
			<tr key={v.id}>
				<td>{v.policyNo}</td>
				<td>{v.productName}</td>
				<td>{v.applicantName}</td>
				<td>{date.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>{v.premium}</td>
				<td>{v.status}</td>
				<td>
					<a onClick={this.open.bind(this, v.id)}>处理</a>
				</td>
			</tr>
        );
    }
}

var Main = React.createClass({
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-12">
					<div className="container-fluid">
						<ul className="nav navbar-nav">
						</ul>
						<ul className="nav navbar-nav navbar-right">
							<li><a onClick={env.create}>上传保单</a></li>
						</ul>
					</div>
					<PolicyList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});