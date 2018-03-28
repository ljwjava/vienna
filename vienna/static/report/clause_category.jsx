"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var Main = React.createClass({
    getInitialState() {
        return {content: {}};
    },
    componentDidMount() {
        this.refresh();
    },
    refresh() {
        var b = this.refs.year1.value + "-" + this.refs.month1.value + "-01";
        var e = this.refs.year2.value + "-" + this.refs.month2.value + "-01";
        common.req("report/clause_category.json", {begin: b, end: e}, r => {
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
                            <option value="01">1月</option>
                            <option value="02">2月</option>
                            <option value="03">3月</option>
                            <option value="04">4月</option>
                            <option value="05">5月</option>
                            <option value="06">6月</option>
                            <option value="07">7月</option>
                            <option value="08">8月</option>
                            <option value="09">9月</option>
                            <option value="10">10月</option>
                            <option value="11">11月</option>
                            <option value="12">12月</option>
                        </select>
                        <span className="mr-sm-2">至</span>
                        <select className="form-control mr-sm-2" ref="year2">
                            <option value="2018">2018年</option>
                            <option value="2017">2017年</option>
                        </select>
                        <select className="form-control mr-sm-2" ref="month2">
                            <option value="02">1月</option>
                            <option value="03">2月</option>
                            <option value="04">3月</option>
                            <option value="05">4月</option>
                            <option value="06">5月</option>
                            <option value="07">6月</option>
                            <option value="08">7月</option>
                            <option value="09">8月</option>
                            <option value="10">9月</option>
                            <option value="11">10月</option>
                            <option value="12">11月</option>
                            <option value="13">12月</option>
                        </select>
                        <button className="btn btn-info" onClick={this.refresh}>查询</button>
                    </div>
                </nav>
                <table className="table table-bordered">
                    <thead className="thead-light">
                    <tr className="text-center">
                        <th>类别</th>
                        <th>条款数量</th>
                        <th>保费</th>
                        <th>应收</th>
                        <th>应付</th>
                    </tr>
                    </thead>
                    <tbody>
                        { c.list == null ? null : c.list.map(v =>
                            <tr className="text-center">
                                <td>{v.categoryName}</td>
                                <td>{v.num}</td>
                                <td>{common.round(v.premium, 2)}</td>
                                <td>{common.round(v.income, 2)}</td>
                                <td>{common.round(v.outcome + v.cms, 2)}</td>
                            </tr>
                        )}
                        <tr className="text-center">
                            <td>合计</td>
                            <td>{c.num}</td>
                            <td>{common.round(c.premium, 2)}</td>
                            <td>{common.round(c.income, 2)}</td>
                            <td>{common.round(c.outcome + c.cms, 2)}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});