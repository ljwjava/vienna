"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    company: {},
    search: null,
    from: 0,
    number: 12,
};

//到期未结算收入明细
class MainList extends List {
    refresh() {
        common.req("channel/bill.json", {typeId: 1, companyId: null, search: env.search, from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
                <th>产品ID</th>
				<th>产品CODE</th>
                <th>所属公司</th>
                <th>产品名称</th>
				<th></th>
			</tr>
        );
    }
    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return (
			<tr key={v.id}>
                <td>{v.id}</td>
                <td>{v.code}</td>
                <td>{env.company[v.companyId]}</td>
                <td>{v.name}</td>
				<td style={{padding:"6px"}}>
                    <button className="btn btn-outline-success mr-1" data-toggle="modal" data-target="#editor" onClick={this.open.bind(this, v)}>编辑</button>
                    <button className="btn btn-outline-success mr-1">配置</button>
                    <button className="btn btn-outline-danger mr-1">删除</button>
                </td>
			</tr>
        );
    }
}

var Main = React.createClass({
    getInitialState() {
        return {clause: {}, company: null};
    },
    componentDidMount() {
        common.req("channel/company.json", {}, r => {
            if (r != null) {
                env.company = {};
                let company = [];
                for (let c in r) {
                    env.company[c] = r[c].name;
                    company.push(<option value={c}>{r[c].name}</option>);
                }
                this.setState({company: company});
            }
        });
    },
    newClause() {
        this.setState({clause: {}});
    },
    saveClause() {
        let param = {
            id: this.state.clause.id,
            code: this.refs.clauseCode.value,
            name: this.refs.clauseName.value,
            companyId: this.refs.clauseCompany.value
        }
        common.req("product/save_clause.json", param, r => {
            this.refs.list.refresh();
        });
    },
    render() {
        return (
            <div>
                <nav className="navbar navbar-light justify-content-between">
                    <button className="btn btn-primary" data-toggle="modal" data-target="#editor" onClick={this.newClause}>新增条款</button>
                    <div className="form-inline">
                        <input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
                        <button className="btn btn-outline-success my-2 my-sm-0" type="submit">搜索</button>
                    </div>
                </nav>
                <MainList ref="list" env={env} parent={this}/>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});