"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var listEnv = {
    search: null,
    from: 0,
    number: 10,
}

var env = {
	companyType: {
    	"1": "保险公司",
        "2": "中介",
        "3": "平台"
	}
}

class MainList extends List {
    open(id) {
        document.location.href = "contract.web?contractId=" + id;
    }
    componentDidMount() {
    	super.componentDidMount();
	}
    refresh() {
        common.req("btbx/channel/query_contract.json", listEnv, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th>甲方</th>
				<th>乙方</th>
				<th>合约名称</th>
				<th>开始</th>
				<th>结束</th>
				<th>状态</th>
				<th>操作</th>
			</tr>
        );
    }
    buildTableLine(v) {
        return (
			<tr key={v.id}>
				<td>{v.partyA}</td>
				<td>{v.partyB}</td>
				<td>{v.name}</td>
				<td>{v.begin}</td>
				<td>{v.end}</td>
				<td>{v.status}</td>
				<td>
					<a href="#" onClick={this.open.bind(this, v.id)}>编辑</a>
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
					<button className="btn btn-primary">新增合约</button>
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
    listEnv.companyId = common.param("companyId");
    ReactDOM.render(<Main/>, document.getElementById("content"));
});