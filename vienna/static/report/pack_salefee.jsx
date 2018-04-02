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
        common.req("report/pack_salefee.json", {begin: b, end: e}, r => {
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
                        <h5 className="mt-sm-1">网销产品费用情况</h5>
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
                        <th>保单数量</th>
                        <th>总保费 ↓</th>
                        <th>佣金费用</th>
                        <th>关联费用</th>
                        <th>营销费用</th>
                    </tr>
                    </thead>
                    <tbody>
                        { c.map(v =>
                            <tr className="text-center">
                                <td>{v.productName}</td>
                                <td>{v.policyNum}</td>
                                <td>{v.sumPremium}</td>
                                <td>{v.commission}</td>
                                <td>{v.relaBonus}</td>
                                <td>{v.bonus}</td>
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