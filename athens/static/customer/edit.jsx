"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Console = React.createClass({
	save() {
	},
	cancel() {
		document.location.href = "list.web";
	},
	render() {
		return (
			<div className="container-fluid">
				<div className="collapse navbar-collapse">
					<div className="nav navbar-nav navbar-center">
						<button type="button" className="btn btn-primary" onClick={this.save}>&nbsp;&nbsp;保存&nbsp;&nbsp;</button>
						&nbsp;&nbsp;
						<button type="button" className="btn btn-primary" onClick={this.cancel}>&nbsp;&nbsp;取消&nbsp;&nbsp;</button>
					</div>
				</div>
			</div>
		);
	}
});

var CustomerForm = React.createClass({
	getInitialState() {
		return null;
	},
	componentDidMount() {
	},
	render() {
		let v = this.props.content;
		return (
			<table className="bordered">
				<tbody>
					<tr>
						<th>姓名</th>
						<td>{v.name}</td>
						<th>生日</th>
						<td>{v.birthday}</td>
					</tr>
					<tr>
						<th>性别</th>
						<td>{v.gender}</td>
						<th>国籍</th>
						<td>{v.nation}</td>
					</tr>
					<tr>
						<th>地址</th>
						<td colSpan="3">{v.address}</td>
					</tr>
				</tbody>
			</table>
		);
	}
});

var Main = React.createClass({
	render() {
		return (
			<div>
				<br/>
				<CustomerForm content={this.props.content}/>
				<br/>
				<Console/>
			</div>
		);
	}
});

function refresh(r) {
	ReactDOM.render(
		<Main content={r}/>, document.getElementById("content")
	);
}

$(document).ready( function() {
	let customerId = common.param("customerId");
	if (customerId == null || customerId == "") {
		refresh({});
	} else common.req("customer/view.json", {customerId:customerId}, function(r) {
		refresh(r);
	});
});