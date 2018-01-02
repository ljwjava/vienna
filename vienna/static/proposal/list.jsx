"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
	search: null,
	from: 0,
	number: 10
}

env.create = function() {
    document.location.href = "standard.web";
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
	buildTableTitle() {
		return (
			<tr>
				<th style={{width: "40%"}}><div>建议书名称</div></th>
				<th><div>修改时间</div></th>
				<th><div>保费</div></th>
				<th style={{width:"200px"}}>{this.buildPageComponent()}</th>
			</tr>
		);
	}
	buildTableLine(v) {
		let date = new Date(v.updateTime);
		return (
			<tr key={v.id}>
				<td><img src={v.tag == "single" ? "../images/user.png" : "../images/users.png"} style={{width:"24px", height:"24px"}}/> {v.name}</td>
				<td style={{textAlign:"center"}}>{date.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td style={{textAlign:"center"}}>{v.premium}</td>
				<td style={{textAlign:"center"}}>
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
		<div>
			<div className="container-fluid">
				<ul className="nav navbar-nav">
				</ul>
				<ul className="nav navbar-nav navbar-right">
					<li><a onClick={env.create}>新建建议书</a></li>
				</ul>
			</div>
			<ProposalList ref="list" env={env}/>
		</div>, document.getElementById("content")
	);
});