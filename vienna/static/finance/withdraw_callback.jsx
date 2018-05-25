"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

env = {
    withdraw: {
        bankName: "中国工商银行",
        lastNo: "8989"
    }
};

var WithdrawSucc = React.createClass({
    render() {
        return (
            <div>
                <h5>提现成功</h5>
                <p style={{color: "#80808080"}}>您的{env.withdraw.bankName}(尾号{env.withdraw.lastNo})的账户将会收到款项，请注意查收</p>
            </div>
        )
    }
});

var ApplySucc = React.createClass({
    render() {
        return (
            <div>
                <h5>申请成功</h5>
                <p style={{color: "#80808080"}}>工作人员将会在收到并核对发票后，根据发票金额对您的账户进行汇款。</p>
            </div>
        )
    }
});


let Main = React.createClass({

    goWithdraw() {
        document.location.href = "withdraw.web"
    },

    render() {
        let infoDiv;
        if (false) {
            infoDiv = <WithdrawSucc/>
        } else {
            infoDiv = <ApplySucc/>
        }
        return (
            <div>
                <nav className="breadcrumb">
                    <a className="breadcrumb-item" href="commission.web">财务结算</a>
                    <a className="breadcrumb-item active">提现</a>
                </nav>

                <div className="container">
                    <div className="row">
                        <div className="col-md-12" style={{textAlign: "center"}}>
                            <span className="glyphicon glyphicon-ok-sign" style={{color: "green", fontSize: "90px"}}/>
                            <br/>
                            {infoDiv}
                            <button className="btn btn-primary" onClick={this.goWithdraw.bind(this, null)}>继续提现</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

$(document).ready(function () {
    ReactDOM.render(
        <Main/>,
        document.getElementById("content")
    );
});