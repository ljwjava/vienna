"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
	search: null,
	from: 0,
	number: 16,
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

var Main = React.createClass({
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-12">
					<div className="container-fluid">
						<ul className="nav navbar-nav">
							<li style={{verticalAlign:"middle"}}>
								<input type="text" className="form-control"/>
							</li>
						</ul>
						<ul className="nav navbar-nav navbar-right">
							<li><a onClick={env.create}>新增客户</a></li>
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