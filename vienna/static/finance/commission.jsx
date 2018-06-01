"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

env = {
    search: null,
    from: 0,
    number: 10,
    settleStatus: [
        {
            code: "N",
            name: "未结算"
        },
        {
            code: "Y",
            name: "已结算"
        },
        {
            code: "F",
            name: "冻结"
        },
        {
            code: "D",
            name: "撤回"
        },
    ],
};

class CommissionList extends List {

    open(v) {
        this.props.parent.setState({clause: v});
    }

    refresh() {
        common.req("util/routing.json", {
                service: 'cloudCapital',
                srvPath: 'v1/capital/commission/list',
                params: {
                    accountId: 123,
                    //start: this.refs.start.value,
                    //end: this.buildDate(this.state.endDate),
                    page: {
                        currentPage: this.props.env.from,
                        pageSize: this.props.env.number
                    }
                }
            },
            r => {
                if (r.code === "0") {
                    this.setState(
                        {
                            content: {
                                list: r.result.resultList,
                                total: r.result.totalItem
                            }
                        });
                } else {
                    console.error(r.message);
                }
            }, er => {
                console.error('err', er);
            });
    };

    buildTableTitle() {
        return (
            <tr>
                <th>出单时间</th>
                <th>订单号</th>
                <th>产品</th>
                <th>来源</th>
                <th>保费(元)</th>
                <th>手续费(元)</th>
                <th>平台服务费(元)</th>
                <th>结算时间</th>
                <th>状态</th>
            </tr>
        );
    };

    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return (
            <tr key={v.id}>
                <td> {v.gmtCreated}</td>
                <td>{v.policyNo}</td>
                {/*<td>{env.company[v.companyId]}</td>*/}
                <td>{v.productId}</td>
                <td>{v.source}</td>
                <td>{v.amount}</td>
                <td>{v.fee}</td>
                <td>{v.platformFee}</td>
                <td>{v.settleTime}</td>
                <td>123</td>
            </tr>
        );
    };
}


var BalanceInfo = React.createClass({

    withdraw() {
        document.location.href = "withdraw.web"
    },

    withdrawRecord() {
        document.location.href = "withdraw_list.web"
    },

    resetQuery() {
        console.log("reset query cond");
    },

    getInitialState() {
        return {
            start: "2018-05-12",
            balance: {
                totalIncome: 20000.00,
                ydayAvailable: 0.00,
                available: 0.00,
                ydayReceivable: 0.00,
                unSettled: 0.00
            },
            productList: [
                {
                    code: "prd1",
                    name: "尊享e生"
                }
            ]
        };
    },

    componentWillMount() {
        common.req("capital/balance.json", null, r => {
            this.setState({balance: r.result});
        });
    },

    handleChange: function (event) {
        this.setState({start: event.target.value});
    },
    render: function () {
        return (
            <div>
                <nav className="breadcrumb">
                    <a className="breadcrumb-item active">财务结算</a>
                </nav>
                <div className='col-md-12 form-inline'>
                    <div className='col-md-9 form-horizontal'>
                        {/*style={{backgroundColor: "#ed486d"}}*/}
                        <div className='form-group'>
                            <p style={{float: "left", paddingTop: '20px'}}>可提现总额(元):</p>
                        </div>
                        {/*<br/>*/}
                        <div className='form-group'>
                            <p style={{float: 'left', color: '#FFAA00', fontSize: '36px'}}>{common.round(this.state.balance.ydayReceivable, 2)}</p>
                            {/*<label style={{fontSize: "medium", float: 'left', textc}}>100.00</label>*/}
                            <button className='btn btn-primary' style={{marginLeft: '20px'}} onClick={this.withdraw.bind(this, null)}>提现</button>
                            <button className='btn btn-default' style={{marginLeft: '20px'}} onClick={this.withdrawRecord.bind(this, null)}>提现记录</button>
                        </div>
                        <hr style={{border: '1px solid', color: '#ececec'}}/>
                        <p style={{color: '#80808080'}}>
                            昨日收入(元):
                            {/*<span style={{color: 'black'}}>{ydayAvailable}</span>*/}
                            <span style={{color: 'black'}}>{common.round(this.state.balance.ydayReceivable, 2)}</span>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            待结算(元):
                            <span style={{color: 'black'}}>{common.round(this.state.balance.ydayReceivable, 2)}</span>
                        </p>
                    </div>
                    <div className='col-md-3' style={{backgroundColor: "#fffae7"}}>
                        <p>累计收入(元)：</p>
                        <p>{common.round(this.state.balance.totalIncome, 2)}</p>
                        <a>收入分析</a>
                    </div>
                </div>
                <nav className="navbar navbar-expand-lg navbar-light bg-light">
                    <div className="collapse navbar-collapse">
                        <div className="dropdown mr-4 form-inline">
                            {/*<button className="btn btn-primary dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">添加产品</button>*/}
                            <div className='dropdown mr-4 form-inline'>
                                <select className='form-control'>
                                    <option>出单时间</option>
                                    <option>结算时间</option>
                                </select>
                            </div>
                            <label style={{marginLeft: '20px'}}>开始</label>
                            <input style={{marginLeft: '20px'}} className="form-control mr-sm-2" type="date" ref="start"
                                   defaultValue={new Date().format("yyyy-MM-dd")}
                                //value={this.state.start}
                            />
                            <label style={{marginLeft: '20px'}}>结束</label>
                            <input style={{marginLeft: '20px'}} className="form-control mr-sm-2" type="date" ref="date"
                                   defaultValue={new Date().format("yyyy-MM-dd")}/>
                            {/*<input className="form-control mr-sm-2" type="date" ref="until" value={common.dateStr(new Date())}/>*/}
                            {/*<table className="dropdown-menu" style={{width:"440px"}}>
                                <tbody>
                                <tr>
                                    <td style={{width:"80px", verticalAlign:"top", borderRight:"1px solid #EEE"}}>{vr}</td>
                                    <td style={{verticalAlign:"top"}}>{clauses}</td>
                                </tr>
                                </tbody>
                            </table>*/}
                        </div>
                        <div className='dropdown mr-4 form-inline'>
                            <label>产品</label>
                            <select className='form-control'>
                                {this.state.productList.map(t => <option key={t.code} value={t.code}>{t.name}</option>)}
                            </select>
                        </div>
                        <div className='dropdown mr-4 form-inline'>
                            <label>状态</label>
                            <select className='form-control'>
                                {env.settleStatus.map(t => <option key={t.code} value={t.code}>{t.name}</option>)}
                            </select>
                        </div>
                        {/*<div className="collapse navbar-collapse mr-auto">
                            { this.props.plan.applicant == null ? null : <Customer tag="applicant" show="投保人" plan={this.props.plan} val={this.props.plan.applicant} refresh={this.props.refresh}/> }
                            { this.props.plan.insurant == null ? null : <Customer tag="insurant" show="被保险人" plan={this.props.plan} val={this.props.plan.insurant} refresh={this.props.refresh}/> }
                        </div>
                        <button type="button" className="btn btn-success mr-1" onClick={this.props.parent.save}>保存</button>
                        <button type="button" className="btn btn-success" onClick={this.props.parent.apply}>投保</button>*/}
                    </div>
                </nav>
                <nav className="navbar navbar-expand-lg navbar-light">
                    <div className="collapse navbar-collapse mr-auto">
                        <button type="button" className="btn btn-primary mr-1">查询</button>
                        <button type="button" className="btn btn-default mr-l">重置</button>
                    </div>
                    <button type="button" className="btn btn-primary mr-l">导出表格</button>
                </nav>
                <CommissionList env={env}/>
            </div>
        );
    }
});

$(document).ready(function () {
    ReactDOM.render(
        <BalanceInfo/>,
        document.getElementById("content")
    );
});