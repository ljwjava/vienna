"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

env.status = {
    "0": "未处理"
}

var Main = React.createClass({
    getInitialState() {
        return {content: []};
    },
    componentDidMount() {
        common.req("channel/company.json", {}, r => {
            if (r != null) {
                let company = [];
                for (let c in r) {
                    company.push(<option value={c}>{r[c].name}</option>);
                }
                this.setState({company: company});
            }
        });
    },
    more() {
        if (env.param != null) {
            env.param.from += 20;
            this.refresh();
        }
    },
    list() {
        env.param = {companyId: this.refs.company.value, end: this.refs.until.value, from: 0, num: 20};
        this.refresh();
    },
    refresh() {
        common.req("finance/bill.json", env.param, r => {
            let list = env.param.from == 0 ? r : this.state.content.concat(r);
            this.setState({content: list});
        });
    },
    render() {
        return (
            <div>
                <nav className="navbar navbar-light justify-content-between">
                    <div className="form-inline">
                        <select className="form-control mr-sm-2" ref="company">
                            { this.state.company }
                        </select>
                        <input className="form-control mr-sm-2" type="date" ref="until" value={common.dateStr(new Date())}/>
                        <button className="btn btn-primary" onClick={this.list}>未结算账单</button>
                    </div>
                    <button className="btn btn-primary">导出EXCEL</button>
                </nav>
                <table className="table table-bordered">
                    <thead className="thead-light">
                    <tr className="text-center">
                        <th>条款</th>
                        <th>相关保单号</th>
                        <th>应收</th>
                        <th>出单时间</th>
                        <th>账单时间</th>
                        <th>账单状态</th>
                        <th>结算时间</th>
                    </tr>
                    </thead>
                    <tbody>
                    { this.state.content.map(v =>
                        <tr key={v.id} className="text-center">
                            <td>{v.clauseName}</td>
                            <td>{v.policyNo}</td>
                            <td>{common.round(v.income, 2)}</td>
                            <td>{common.timeStr(v.insureTime)}</td>
                            <td>{common.timeStr(v.estimateTime)}</td>
                            <td>{env.status[v.status]}</td>
                            <td>{common.timeStr(v.payTime)}</td>
                        </tr>
                    )}
                    </tbody>
                </table>
                { env.param == null ? null :
                    <button className="btn btn-outline-info btn-block" onClick={this.more}>继续加载...</button>
                }
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});