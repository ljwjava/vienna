"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 10
}

class MainList extends List {
    refresh() {
        common.req("util/routing.json", {
            service: 'cloudCapital',
            srvPath: 'v1/capital/withdraw/flowOut/list',
            params: {
                accountId: '1',
                startDate: this.buildDate(this.state.startDate),
                endDate: this.buildDate(this.state.endDate),
                currentPage: this.props.env.from,
                pageSize: this.props.env.number
            }
        }, sr => {
            if (sr.code == 0) {
                this.setState({content: {list: sr.result.resultList, total: sr.result.totalItem}});
            } else {
                console.error(sr.message);
            }
        }, er => {
            console.error('err', er);
        });
    }

    buildTableTitle() {
        return (
            <tr>
                <th>序号</th>
                <th>操作时间</th>
                <th>提现金额(元)</th>
                <th>提现手续费(元)</th>
                <th>状态</th>
            </tr>
        );
    }

    buildTableLine(v) {
        return (
            <tr key={v.id}>
                <td>{v.id}</td>
                <td>{v.gmtCreated}</td>
                <td>{v.outflowAmount}</td>
                <td>{this.buildFee(v.feeAmount)}</td>
                <td>{this.buildStatus(v.settleStatus)}</td>
            </tr>
        );
    }

    buildDate(d){
        if(!d || d.indexOf("N") != -1){
            return '';
        }
        return d;
    }
    buildFee(f) {
        if (f == 0) {
            return "免费";
        } else {
            return f;
        }
    }

    buildStatus(s) {
        //结算状态1处理中 2成功 3失败 4已申请 5-已拒绝
        if (s == 1) {
            return "处理中";
        } else if (s == 2) {
            return "提现成功";
        } else if (s == 3) {
            return "提现失败";
        } else if (s == 4) {
            return "已申请";
        } else if (s == 5) {
            return "已拒绝";
        } else {
            return "未知状态";
        }
    }
}

var Main = React.createClass({
    list() {
        this.refs.ml.setState({
            startDate: common.timeStr(this.refs.startDate.value),
            endDate: common.timeStr(this.refs.endDate.value)
        }, () => {
            this.refs.ml.refresh();
        });
    },

    render() {
        return (
            <div>
                <nav className="navbar navbar-light bg-white justify-content-between">
                    <div className="form-inline">
                        开始时间:<input className="form-control mr-sm-2" type="date" ref="startDate"/>
                        结束时间:<input className="form-control mr-sm-2" type="date" ref="endDate"/>
                        <button className="btn btn-outline-success my-2 my-sm-0" type="button"
                                onClick={this.list.bind(this)}>搜索
                        </button>
                    </div>
                    <button className="btn btn-primary">导出EXCEL</button>
                </nav>
                <MainList ref="ml" env={env}/>
            </div>
        );
    }
});

$(document).ready(function () {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});