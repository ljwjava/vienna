"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 16,
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
        common.req("btbx/channel/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th><div>名称</div></th>
				<th><div>类型</div></th>
				<th><div>执照号码</div></th>
				<th><div>所在地区</div></th>
				<th><div>联系人</div></th>
				<th><div>电话</div></th>
				<th><div>电子邮件</div></th>
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
				<td>
					<div className="btn-group" role="group">
						<button type="button" className="btn btn-default" onClick={this.open.bind(this, v.id)}>编辑</button>
					</div>
				</td>
			</tr>
        );
    }
}

var Main = React.createClass({
	switchList(mode) {
	},
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-12">
					<div className="container-fluid">
						<ul className="nav navbar-nav">
							<li>
								<form className="navbar-form" role="search">
									<div className="form-group">
										<input type="text" className="form-control" placeholder=""/>
									</div>
									<button type="submit" className="btn btn-success">搜索</button>
								</form>
							</li>
						</ul>
						<ul className="nav navbar-nav navbar-right">
							<li>
								<form className="navbar-form" role="search">
									<button className="btn btn-default">新增渠道</button>
								</form>
							</li>
						</ul>
					</div>
					<MainList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});