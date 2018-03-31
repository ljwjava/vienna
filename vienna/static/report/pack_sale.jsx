"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

function toDateStr(y, m) {
    if (m > 12) {
        m -= 12;
        y ++;
    }
    if (m < 10)
        m = "0" + m;
    return y + "-" + m + "-01";
}

var Main = React.createClass({
    getInitialState() {
        return {content: []};
    },
    componentDidMount() {
        this.count(2);
    },
    count(v) {
        var b, e;
        var now = new Date();
        var y = now.getFullYear();
        var m = now.getMonth();
        if (v == 1) {
            b = new Date(now.getTime() - 3600000 * 24).format("yyyy-MM-dd");
            e = now.format("yyyy-MM-dd");
        } else if (v == 2) {
            b = now.format("yyyy-MM-dd");
            e = new Date(now.getTime() + 3600000 * 24).format("yyyy-MM-dd");
        } else if (v == 3) {
            m--;
            if (m < 0) {
                y--;
                m = 12;
            }
            b = new Date(y, m, 1).format("yyyy-MM-01");
            e = now.format("yyyy-MM-01");
        } else if (v == 4) {
            m++;
            if (m >= 12) {
                y++;
                m = 0;
            }
            b = now.format("yyyy-MM-01");
            e = new Date(y, m, 1).format("yyyy-MM-01");
        } else if (v == 5) {
            b = now.format("yyyy-01-01");
            e = new Date(y + 1, 1, 1).format("yyyy-01-01");
        }
        common.req("report/pack_sale.json", {begin: b, end: e}, r => {
            this.setState({content: r});
        });
    },
    render() {
        let c = this.state.content;
        return (
            <div>
                <nav className="navbar navbar-light">
                    <div className="form-inline">
                        <h5 className="text-primary font-weight-bold mt-sm-1">【报表】</h5>
                        <h5 className="mt-sm-1">网销产品出单情况</h5>
                    </div>
                    <div className="form-inline btn-group">
                        <button className="btn btn-outline-info" onClick={this.count.bind(this, 1)}>昨日</button>
                        <button className="btn btn-outline-info" onClick={this.count.bind(this, 2)}>今日</button>
                        <button className="btn btn-outline-info" onClick={this.count.bind(this, 3)}>上月</button>
                        <button className="btn btn-outline-info" onClick={this.count.bind(this, 4)}>本月</button>
                        <button className="btn btn-outline-info" onClick={this.count.bind(this, 5)}>今年</button>
                    </div>
                </nav>
                <table className="table table-bordered">
                    <thead className="thead-light">
                    <tr className="text-center">
                        <th>产品</th>
                        <th>填单数量</th>
                        <th>成交数量</th>
                        <th>保单成交比例</th>
                        <th>填单保费 ↓</th>
                        <th>成交保费</th>
                        <th>保费成交比例</th>
                    </tr>
                    </thead>
                    <tbody>
                        { c.map(v =>
                            <tr className="text-center">
                                <td>{v.productName}</td>
                                <td>{v.orderNum}</td>
                                <td>{v.dealNum <= 0 ? "-" : v.dealNum}</td>
                                <td>{v.dealNum <= 0 ? "-" : common.round(v.dealNum * 100 / v.orderNum, 2) + "%"}</td>
                                <td>{common.round(v.orderAmt, 2)}</td>
                                <td>{v.dealNum <= 0 ? "-" : common.round(v.dealAmt, 2)}</td>
                                <td>{v.dealNum <= 0 ? "-" : common.round(v.dealAmt * 100 / v.orderAmt, 2) + "%"}</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});