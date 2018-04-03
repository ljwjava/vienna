"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var rateOf = function(fz, fm) {
    if (fz == null || fm == null || fz <= 0 || fm <= 0)
            return "-";
    return common.round(fz * 100 / fm, 2) + "%";
}

var Main = React.createClass({
    getInitialState() {
        return {content: []};
    },
    componentDidMount() {
        this.refresh();
    },
    refresh() {
        var date = this.refs.date.value;
        common.req("report/apply_stats.json", {date: date}, r => {
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
                        <h5 className="mt-sm-1">产品出单成功率</h5>
                    </div>
                    <div className="form-inline btn-group">
                        <input className="form-control mr-sm-2" type="date" ref="date" defaultValue={new Date().format("yyyy-MM-dd")}/>
                        <button className="btn btn-info" onClick={this.refresh}>查询</button>
                    </div>
                </nav>
                <table className="table table-bordered">
                    <thead className="thead-light">
                    <tr className="text-center">
                        <th>产品</th>
                        <th>校验提交次数</th>
                        <th>验证码错误率</th>
                        <th>信息查询失败率</th>
                        <th>校验失败率</th>
                        <th>投保提交次数 ↓</th>
                        <th>核保失败率</th>
                        <th>支付失败率</th>
                        <th>出单失败率</th>
                        <th>出单成功率</th>
                        <th>最终成功率</th>
                    </tr>
                    </thead>
                    <tbody>
                        { c.map(v =>
                            <tr className="text-center">
                                <td>{v.productName}</td>
                                <td>{v.check}</td>
                                <td className="text-danger font-weight-bold">{rateOf(v.smsFail, v.check)}</td>
                                <td className="text-danger font-weight-bold">{rateOf(v.otherFail, v.check)}</td>
                                <td className="text-danger font-weight-bold">{rateOf(v.checkFail, v.check)}</td>
                                <td>{v.apply}</td>
                                <td className="text-danger font-weight-bold">{rateOf(v.verifyFail, v.apply)}</td>
                                <td className="text-danger font-weight-bold">{rateOf(v.payFail, v.apply)}</td>
                                <td className="text-danger font-weight-bold">{rateOf(v.insureFail, v.apply)}</td>
                                <td className="text-success font-weight-bold">{rateOf(v.applySuccess, v.apply)}</td>
                                <td className="text-success font-weight-bold">{rateOf(v.insureSuccess, v.apply)}</td>
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