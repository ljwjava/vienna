"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

env = {
    search: null,
    from: 0,
    number: 10,
    card: {
        bankName: "工商银行",
        lastNo: "8989"
    },
    withdraw: {
        remainTimes: 5,
        feeRate: 1,
        minFee: 1
    }
};

let Main = React.createClass({
    render() {
        return (
            <div>
                <nav className="breadcrumb">
                    <a className="breadcrumb-item" href="commission.web">财务结算</a>
                    <a className="breadcrumb-item active">提现</a>
                </nav>

                <div className="alert alert-info" style={{margin: "0 30 0 30"}} role="alert">
                    <span className="glyphicon glyphicon-info-sign"/>
                    <a className="alert-link">请先将发票寄送至上海市虹口区四川北路859号2605 云宝收</a>
                </div>
                <div className='col-md-12 form-horizontal' style={{margin: "30px"}}>
                    <p>提现账户：{env.card.bankName} (尾号{env.card.lastNo})</p>
                    <div className="form-group form-inline">
                        <label>提现金额</label>&nbsp; : &nbsp;
                        <input className="form-control"/>&nbsp;
                        <label>元</label>
                    </div>
                    <p style={{color: "#80808080"}}>您还可以免费提现{env.withdraw.remainTimes}次，超过部分收取{env.withdraw.feeRate}%服务费，最低{env.withdraw.minFee}元</p>
                </div>

                <button style={{margin: "30px"}} className="btn btn-primary">提现</button>

                <hr style={{border: "1px solid", color: "#80808080", margin: "30px"}}/>

                <div className='col-md-12 form-horizontal' style={{margin: "30px"}}>
                    <h4 style={{fontWeight: "bold"}}>使用遇到问题？</h4>
                    <p>1.提示“账户错误”怎么操作？</p>
                    <p style={{color: "#80808080"}}>答：您可以尝试更换下财务信息所绑定的银行卡，并重新尝试。<a href="home.web">修改财务信息</a></p>
                </div>
            </div>
        );
    }
});

$(document).ready(function () {
    ReactDOM.render(
        <Main name='fefef'/>,
        document.getElementById("content")
    );
});