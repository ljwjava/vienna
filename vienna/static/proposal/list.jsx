"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
	search: null,
	from: 0,
	number: 10
}

class ProposalList extends List {
	create() {
		document.location.href = "standard.web";
	}
	copy(id) {
		common.req("proposal/copy.json", {proposalId:id}, r => {
			document.location.href = "standard.web?proposalId=" + r.proposalId;
		});
	}
	open(id) {
		document.location.href = "standard.web?proposalId=" + id;
	}
	refresh() {
		common.req("proposal/list.json", this.props.env, r => {
			this.setState({content:r});
		});
	}
	delete(id) {
		common.req("proposal/delete.json", {proposalId:id}, r => {
			this.refresh();
		});
	}
	buildConsole() {
		return (
			<div className="container-fluid">
				<div className="collapse navbar-collapse">
					<div className="nav navbar-nav navbar-right">
						<a onClick={this.create}>＋ 新建建议书</a>
					</div>
				</div>
			</div>
		);
	}
	buildTableTitle() {
		return (
			<tr>
				<th width="5%"></th>
				<th width="40%">建议书名称</th>
				<th width="20%">修改时间</th>
				<th width="15%">保费</th>
				<th width="20%"></th>
			</tr>
		);
	}
	buildTableLine(v) {
		let date = new Date(v.updateTime);
		return (
			<tr key={v.id}>
				<td>{v.tag}</td>
				<td>{v.name}</td>
				<td>{date.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>{v.premium}</td>
				<td>
					<a onClick={this.copy.bind(this, v.id)} style={{marginRight:"10px"}}>复制</a>
					<a onClick={this.open.bind(this, v.id)} style={{marginRight:"10px"}}>编辑</a>
					<a onClick={this.delete.bind(this, v.id)}>删除</a>
				</td>
			</tr>
		);
	}
}

$(document).ready( function() {
	ReactDOM.render(
		<ProposalList env={env}/>, document.getElementById("content")
	);
});