"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
	search: null,
	from: 0,
	number: 10
}

class ChannelList extends List {
	open(id) {
		document.location.href = "edit_script.web?platformId=" + id;
	}
	refresh() {
		common.req("channel/list.json", this.props.env, r => {
			this.setState({content:r});
		});
	}
	delete(id) {
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
			</div>
		);
	}
	buildTableTitle() {
		return (
			<tr>
				<th>ID</th>
				<th>CODE</th>
				<th>名称</th>
				<th>创建时间</th>
				<th>修改时间</th>
				<th></th>
			</tr>
		);
	}
	buildTableLine(v) {
		let date = new Date(v.updateTime);
		return (
			<tr key={v.id}>
				<td>{v.id}</td>
				<td>{v.code}</td>
				<td>{v.name}</td>
				<td>{v.createTime == null ? null : v.createTime.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>{v.updateTime == null ? null : v.updateTime.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>
					<button type="button" className="btn btn-primary" onClick={this.open.bind(this, v.id)}>编辑</button>
				</td>
			</tr>
		);
	}
}

$(document).ready( function() {
	ReactDOM.render(
		<ChannelList env={env}/>, document.getElementById("content")
	);
});