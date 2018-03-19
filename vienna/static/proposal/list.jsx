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
				<th>建议书名称</th>
				<th>主要险种</th>
				<th>投保人</th>
				<th>修改时间</th>
				<th>保费</th>
				<th>操作</th>
			</tr>
		);
	}
	buildTableLine(v) {
		let date = new Date(v.updateTime);
		return (
			<tr key={v.id}>
				<td><img src={v.tag == "single" ? "../images/user.png" : "../images/users.png"} style={{width:"24px", height:"24px"}}/> {v.name}</td>
				<td></td>
				<td>{v.applicant}</td>
				<td>{date.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>{Number(v.premium).toFixed(2)}</td>
				<td style={{padding:"6px"}}>
					<button className="btn btn-outline-success mr-1" onClick={this.copy.bind(this, v.id)}>复制</button>
					<button className="btn btn-outline-success mr-1" onClick={this.open.bind(this, v.id)}>编辑</button>
					<button className="btn btn-outline-danger mr-1" onClick={this.delete.bind(this, v.id)}>删除</button>
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
					<button className="btn btn-primary" onClick={env.create}>新建建议书</button>
					<div className="form-inline">
						<input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
						<button className="btn btn-outline-success my-2 my-sm-0" type="submit">搜索</button>
					</div>
				</nav>
				<ProposalList ref="list" env={env}/>
			</div>
		)
    }
});

$(document).ready( function() {
	ReactDOM.render(
		<Main/>, document.getElementById("content")
	);
});