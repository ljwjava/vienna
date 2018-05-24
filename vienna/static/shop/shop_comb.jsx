"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import CheckboxList from "../common/widget.checkboxList.jsx";
import Pagination from "../common/component.page.jsx";
import Selecter from '../common/widget.selecter.jsx';
import Inputer from '../common/widget.inputer.jsx';

var env = {
    company: {},
    category: {},
    search: null,
    from: 0,
    number: 12,
};

var Main = React.createClass({
    getInitialState() {
        return {
            clause: {},
            content:{list:[], total:0},
        };
    },
    refresh() {
        common.req("product/list.json", {typeId: 1, companyId: null, search: env.search, from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
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
    buildTableTitle() {
        return([
            {"key":"checkbox","name":"复选框","hidden":true},
            {"key":"id","name":"产品ID"},
            {"key":"code","name":"产品CODE"},
            {"key":"companyId","name":"所属公司"},
            {"key":"name","name":"产品名称"},
            {"key":"categoryId","name":"类别"},
            {"key":"operate","name":"操作"}
        ]);
    },
    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return([
            {"key":"checkbox","value":"复选框","hidden":true},
            {"key":"id","value":v.id},
            {"key":"code","value":v.code},
            {"key":"companyId","value":env.company[v.companyId]},
            {"key":"name","value":v.name},
            {"key":"categoryId","value":env.category[v.categoryId]},
            {"key":"operate","value":[{"key":"edit","value":"编辑"},{"key":"config","value":"配置"},{"key":"deleted","value":"删除"}]}
        ]);
    },
    render() {
        return (
            <div className="page-product-list">
                <h1>请选择产品</h1>
                <nav className="navbar navbar-light justify-content-between">
                    <div className="form-inline">
                        <input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
                        <button className="btn btn-outline-success my-2 my-sm-0" type="submit">搜索</button>
                    </div>
                </nav>
                <CheckboxList ref="list" env={env} parent={this} tableTitle={this.buildTableTitle} tableLine={this.buildTableLine}/>

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