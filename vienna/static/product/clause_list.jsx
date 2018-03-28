"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    company: {},
    category: {},
    search: null,
    from: 0,
    number: 12,
};

class ProductList extends List {
    open(v) {
        this.props.parent.setState({clause: v});
    }
    refresh() {
        common.req("product/list.json", {typeId: 1, companyId: null, search: env.search, from: env.from, number: env.number}, r => {
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
                <th>类别</th>
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
                <td>{env.category[v.categoryId]}</td>
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
        return {clause: {}};
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
        common.req("dict/view.json", {name: "clauseCategory"}, r => {
            let c = r.clauseCategory.map(v => {
                env.category[v.code] = v.name;
                return <option value={v.code}>{v.name}</option>;
            });
            this.setState({category: c});
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
            categoryId: this.refs.clauseCategory.value,
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
                <ProductList ref="list" env={env} parent={this}/>

                <div className="modal fade" id="editor" tabIndex="-1" role="dialog" aria-hidden="true">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">条款详情</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body" key={this.state.clause.id}>
                                <div className="form-row">
                                    <div className="col-md-6 mb-3">
                                        <label>条款ID</label>
                                        <input type="text" ref="clauseId" className="form-control" value={this.state.clause.id == null ? "自动生成" : this.state.clause.id} readOnly/>
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label>名称</label>
                                        <input type="text" ref="clauseName" className="form-control" defaultValue={this.state.clause.name}/>
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="col-md-6 mb-3">
                                        <label>条款CODE</label>
                                        <input type="text" ref="clauseCode" className="form-control" defaultValue={this.state.clause.code}/>
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label>保险公司</label>
                                        <select ref="clauseCompany" className="form-control" defaultValue={this.state.clause.companyId}>
                                            {this.state.company}
                                        </select>
                                    </div>
                                </div>
                                <div className="form-row">
                                    <div className="col-md-6 mb-3">
                                        <label>类别</label>
                                        <select ref="clauseCategory" className="form-control" defaultValue={this.state.clause.categoryId}>
                                            <option value="90">请选择</option>
                                            {this.state.category}
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">关闭</button>
                                <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.saveClause}>保存修改</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});