"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 20,
}

class ProductList extends List {
    open(id) {
        document.location.href = "product.web?productId=" + id;
    }
    refresh() {
        common.req("product/list.json", {search: env.search, from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
                <th><div>产品ID</div></th>
				<th><div>产品CODE</div></th>
                <th><div>产品名称</div></th>
				<th><div>所属公司</div></th>
                <th><div>状态</div></th>
				<th style={{width:"200px"}}>{this.buildPageComponent()}</th>
			</tr>
        );
    }
    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return (
			<tr key={v.id}>
                <td>{v.id}</td>
                <td>{v.code}</td>
                <td>{v.name}</td>
                <td>{v.companyName}</td>
                <td>{v.status}</td>
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
					<ProductList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});