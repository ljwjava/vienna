"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
	search: null,
	from: 0,
	number: 16
}

class CustomerList extends List {
	create() {
		document.location.href = "edit.web";
	}
	open(id) {
		document.location.href = "edit.web?customerId=" + id;
	}
	refresh() {
		common.req("customer/list.json", this.props.env, r => {
			this.setState({content:r});
		});
	}
	delete(id) {
		common.req("customer/delete.json", {customerId:id}, r => {
			this.refresh();
		});
	}
	buildConsole() {
		return (
			<div className="collapse navbar-collapse">
				<div className="nav navbar-nav">
					<input type="text" className="form-control"/>
				</div>
				<div className="nav navbar-nav">
					<button type="button" className="btn btn-primary" onClick={this.create}>查询</button>
				</div>
				<div className="nav navbar-nav navbar-right">
					<a onClick={this.create}>＋ 新增客户</a>
				</div>
			</div>
		);
	}
	buildTableTitle() {
		return (
			<tr>
				<th width="5%"></th>
				<th width="20%">姓名</th>
				<th width="20%">生日</th>
				<th width="20%">性别</th>
				<th width="20%">修改时间</th>
				<th width="15%"></th>
			</tr>
		);
	}
	buildTableLine(v) {
		let date = new Date(v.updateTime);
		return (
			<tr key={v.customerId}>
				<td></td>
				<td>{v.name}</td>
				<td>{v.birthday}</td>
				<td>{v.gender}</td>
				<td>{date.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>
					<a onClick={this.open.bind(this, v.customerId)}>编辑</a>
					&nbsp;&nbsp;
					<a onClick={this.delete.bind(this, v.customerId)}>删除</a>
				</td>
			</tr>
		);
	}
}

$(document).ready( function() {
	ReactDOM.render(
		<CustomerList env={env}/>, document.getElementById("content")
	);
});