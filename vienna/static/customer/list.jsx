"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
	search: null,
	from: 0,
	number: 16,
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
				<th><div>姓名</div></th>
				<th><div>性别</div></th>
				<th><div>生日</div></th>
				<th><div>证件</div></th>
				<th><div>手机</div></th>
				<th><div>电子邮件</div></th>
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
					<div className="btn-group" role="group">
						<button type="button" className="btn btn-default" onClick={this.open.bind(this, v.id)}>编辑</button>
						<button type="button" className="btn btn-danger">删除</button>
					</div>
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
							<li>
								<form className="navbar-form navbar-left" role="search">
									<div className="form-group">
										<input type="text" className="form-control" placeholder=""/>
									</div>
									<button type="submit" className="btn btn-success">搜索</button>
								</form>
							</li>
						</ul>
						<ul className="nav navbar-nav navbar-right">
							<li>
								<div className="navbar-nav-console">
									<button className="btn btn-default">新增客户</button>
								</div>
							</li>
						</ul>
					</div>
					<CustomerList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
	ReactDOM.render(
		<Main/>, document.getElementById("content")
	);
});