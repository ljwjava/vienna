"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 12,
	companyType: {
    	"1": "保险公司",
        "2": "中介",
        "3": "平台"
	}
}

class MainList extends List {
    open(id) {
        document.location.href = "contracts.web?companyId=" + id;
    }
    componentDidMount() {
    	super.componentDidMount();
	}
    refresh() {
        common.req("channel/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th>名称</th>
				<th>类型</th>
				<th>执照号码</th>
				<th>所在地区</th>
				<th>联系人</th>
				<th>电话</th>
				<th>电子邮件</th>
				<th>操作</th>
			</tr>
        );
    }
    buildTableLine(v) {
        return (
			<tr key={v.id}>
				<td>{v.name}</td>
				<td>{env.companyType[v.type]}</td>
				<td>{v.bizLicense}</td>
				<td>{v.city}</td>
				<td>{v.contact}</td>
				<td>{v.telephone}</td>
				<td>{v.email}</td>
				<td style={{padding:"6px"}}>
					<button className="btn btn-outline-success mr-1">编辑</button>
					<button className="btn btn-outline-success mr-1" onClick={this.open.bind(this, v.id)}>合约</button>
					<button className="btn btn-outline-danger mr-1">删除</button>
				</td>
			</tr>
        );
    }
}

var Main = React.createClass({
    render() {
        return (
        	<div>
				<nav className="navbar navbar-light bg-white justify-content-between">
					<button className="btn btn-outline-primary">新增渠道</button>
					<div className="form-inline">
						<input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
						<button className="btn btn-outline-success my-2 my-sm-0" type="submit">搜索</button>
					</div>
				</nav>
				<MainList env={env}/>
			</div>
		);
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});