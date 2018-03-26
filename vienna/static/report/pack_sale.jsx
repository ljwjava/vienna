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
        this.refresh();
    },
    refresh() {
        var b = toDateStr(Number(this.refs.year1.value), Number(this.refs.month1.value));
        var e = toDateStr(Number(this.refs.year1.value), Number(this.refs.month1.value) + 1);
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
                    <div className="form-inline">
                        <select className="form-control mr-sm-2" ref="year1">
                            <option value="2018">2018年</option>
                            <option value="2017">2017年</option>
                        </select>
                        <select className="form-control mr-sm-2" ref="month1">
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
                        <button className="btn btn-info" onClick={this.refresh}>查询</button>
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