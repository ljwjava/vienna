"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import DateEditor from '../common/widget.date.jsx';
import Inputer from '../common/widget.inputer.jsx';
import MultiSwt from '../common/widget.multiswt.jsx';
import Form from '../common/component.form.jsx';

var strOf = function(s1, s2) {
    if (s1 == null || s1 == "")
        return s2;
    return s1;
}

var env = {};

env.test = {
    name: "产品1",
    fee: [{
        val: [35, 5, 5, 3]
    }]
}

env.baseOf = function(v) {
    return v == null ? null : (
        <div className="card border-info mb-3">
            <div className="card-header text-white bg-info">基本信息</div>
            <div className="card-body text-secondary">
                <div>
                    <div className="form-row">
                        <div className="col-md-4 mb-3">
                            <label>名称</label>
                            <input type="text" className="form-control" defaultValue={v.name}/>
                        </div>
                        <div className="col-md-4 mb-3">
                            <label>所属公司</label>
                            <select className="form-control" defaultValue={v.companyId}>
                                {null}
                            </select>
                        </div>
                        <div className="col-md-4 mb-3">
                            <label>类别</label>
                            <select className="form-control" defaultValue={v.type}>
                                {null}
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

env.feeOf = function(prd) {
    if (prd == null)
        return null;
    let list = prd.fee.map(x => {
        return <tr>
            <td>{prd.name}</td>
            <td>{strOf(x.pay, "*")}</td>
            <td>{strOf(x.insure, "*")}</td>
            <td width="50%">
                <div className="form-inline">
                    <input type="text" className="form-control col-2 mr-2" defaultValue={x.val[0]}/>
                    <input type="text" className="form-control col-2 mr-2" defaultValue={x.val[1]}/>
                    <input type="text" className="form-control col-2 mr-2" defaultValue={x.val[2]}/>
                    <input type="text" className="form-control col-2 mr-2" defaultValue={x.val[3]}/>
                    <input type="text" className="form-control col-2" defaultValue={x.val[4]}/>
                </div>
            </td>
            <td><span className="glyphicon glyphicon-remove"></span></td>
        </tr>;
    });
    return (
        <table className="table table-bordered">
            <thead className="thead-light">
            <tr>
                <th><div>条款</div></th>
                <th><div>交费</div></th>
                <th><div>保障</div></th>
                <th><div>代理人收入（%）</div></th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            {list}
            <tr>
                <td colSpan="4"></td>
                <td><span className="glyphicon glyphicon-plus"></span></td>
            </tr>
            </tbody>
        </table>
    )
}

var Main = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
        let productId = common.param("productId");
        this.setState({product: env.test});
    },
    back() {
        document.location.href = "list.web";
    },
    render() {
        return (
            <div className="mt-3">
                { env.baseOf(this.state.product) }
                { env.feeOf(this.state.product) }
                <div className="form-inline">
                    <button className="btn btn-success mr-2">全部保存</button>
                    <button className="btn btn-danger">取消</button>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(
        <Main/>, document.getElementById("content")
    );
    $("#brand").html("&lArr;&nbsp;&nbsp;返回");
    $("#brand").click(() => document.location.href = "list.web");
});