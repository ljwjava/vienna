"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
	search: null,
	from: 0,
	number: 12,
	dict: {
		gender: {
			"M": "男",
			"F": "女"
		}
	},
	create: function() {
        document.location.href = "edit.web";
	}
}

class CustomerList extends List {
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
	buildTableTitle() {
		return (
			<tr>
				<th>姓名</th>
				<th>性别</th>
				<th>生日</th>
				<th>证件</th>
				<th>手机</th>
				<th>电子邮件</th>
				<th>操作</th>
			</tr>
		);
	}
	buildTableLine(v) {
		return (
			<tr key={v.id}>
				<td>{v.name}</td>
				<td>{env.dict.gender[v.gender]}</td>
				<td>{v.birthday}</td>
				<td>{v.certNo}</td>
				<td>{v.mobile}</td>
				<td>{v.email}</td>
				<td>
					<a className="mr-2" onClick={this.open.bind(this, v.id)}>编辑</a>
					<a>删除</a>
				</td>
			</tr>
		);
	}
}

var Main = React.createClass({
    render() {
        return (
			<div>
				<nav className="navbar navbar-light justify-content-between">
					<button className="btn btn-primary" data-toggle="modal" data-target="#editor" onClick={this.newCustomer}>新增客户</button>
					<div className="form-inline">
						<input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
						<button className="btn btn-success my-2 my-sm-0" type="submit">搜索</button>
					</div>
				</nav>
				<CustomerList env={env}/>
			</div>
		);
    }
});

$(document).ready( function() {
	ReactDOM.render(
		<Main/>, document.getElementById("content")
	);
});