"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

//中介条款按月明细
//中介收入按月明细
//到期未结算收入明细
var Report = React.createClass({
    componentDidMount() {
    },
    refresh() {
        common.req("report/list.json", env, r => {
            this.setState({content:r});
        });
    },
    report() {
        return (
            <table className="table table-bordered">
                <thead>
                    <tr>
                        <th>保险公司</th>
                        <th>条款</th>
                        <th>保单号</th>
                        <th>费用</th>
                        <th>投保日期</th>
                        <th>账单日期</th>
                    </tr>
                </thead>
                <tbody>
                    <tr></tr>
                </tbody>
            </table>
        );
    }
}

var Main = React.createClass({
    render() {
        return (
            <div>
                <nav className="navbar navbar-light justify-content-between">
                    <div className="form-inline">
                        <select className="form-control mr-sm-2">
                            <option value="2018">2018</option>
                        </select>
                        <select className="form-control mr-sm-2">
                            <option value="1">1月</option>
                            <option value="2">2月</option>
                            <option value="3">3月</option>
                            <option value="4">4月</option>
                            <option value="5">5月</option>
                            <option value="6">6月</option>
                            <option value="7">7月</option>
                            <option value="8">8月</option>
                            <option value="9">9月</option>
                            <option value="10">10月</option>
                            <option value="11">11月</option>
                            <option value="12">12月</option>
                        </select>
                        <button className="btn btn-primary">查询</button>
                    </div>
                    <button className="btn btn-primary">导出EXCEL</button>
                </nav>
                <Report/>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});