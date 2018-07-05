"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 20,
    type: {
        0: "未激活",
        1: "正常",
        9: "锁定",
    }
}

class WareList extends List {
    open(id) {
        document.location.href = "ware.web?userId=" + id;
    }
    refresh() {
        common.req("sale/product/list.json", {from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th><div>ID</div></th>
                <th><div>CODE</div></th>
				<th><div>名称</div></th>
                <th><div>名称缩写</div></th>
                <th><div>供应商</div></th>
                <th><div>类型</div></th>
				<th></th>
			</tr>
        );
    }
    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return (
			<tr key={v.id}>
                <td>{v.code}</td>
                <td>{v.name}</td>
                <td>{v.abbrName}</td>
                <td>{v.vendorName}</td>
                <td>{env.type[v.type]}</td>
				<td>
					<a onClick={this.open.bind(this, v.id)}>编辑</a>
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
					<br/>
					<WareList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});