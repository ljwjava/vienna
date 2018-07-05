"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 10
}

class PolicyList extends List {
    open(v) {
        document.location.href = "policy.web?policyId=" + v.id;
    }
    refresh() {
        common.req("btbx/policy/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
            <tr>
                <th>产品类型</th>
                <th>业务类型</th>
                <th>保险公司</th>
                <th>主要产品</th>
                <th>保单号</th>
                <th>投保人</th>
                <th>投保时间</th>
                <th>保费</th>
                <th>业务员</th>
                <th>操作</th>
            </tr>
        );
    }
    buildTableLine(v) {
        return (
            <tr key={v.id}>
                <td>{v.bizType}</td>
                <td>{v.policyType}</td>
                <td>{v.insCompany}</td>
                <td>{v.clause}</td>
                <td>{v.policyNo}</td>
                <td>{v.customer}</td>
                <td>{common.dateStr(v.applyTime)}</td>
                <td style={{textAlign:"right"}}>{v.premium}</td>
                <td>{v.agent}</td>
                <td style={{padding:"6px"}}>
                    <button className="btn btn-outline-success mr-1" onClick={this.open.bind(this, v)}>明细</button>
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
                    <div className="form-inline">
                        <h5 className="text-primary font-weight-bold mt-sm-1">【保单】</h5>
                        <h5 className="mt-sm-1">人身险</h5>
                    </div>
                </nav>
                <div  className="container-fluid">
                    <PolicyList env={env}/>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});