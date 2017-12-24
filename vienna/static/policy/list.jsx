"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 10
}

class StandardList extends List {
    upload() {
        document.location.href = "upload.web";
    }
    open(id) {
        document.location.href = "policy.web?policyId=" + id;
    }
    refresh() {
        common.req("policy/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildConsole() {
        return (
			<nav className="navbar navbar-default">
				<div className="container-fluid">
					<div className="collapse navbar-collapse">
						<ul className="nav navbar-nav">
						</ul>
						<ul className="nav navbar-nav navbar-right">
							<li><a onClick={this.upload}>上传保单</a></li>
						</ul>
					</div>
				</div>
			</nav>
        );
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

$(document).ready( function() {
    ReactDOM.render(
		<StandardList env={env}/>, document.getElementById("content")
    );
});