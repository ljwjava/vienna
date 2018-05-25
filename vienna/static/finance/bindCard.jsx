"use strict";

import React from 'react';
import ReactDOM from 'react-dom';


env.cityList = ["上海", "北京", "广州", "深圳"];
env.bankList = ["招行", "工行", "中行", "农行", "建行"];


var MyCard = React.createClass({
    getInitialState() {
        return {};
    },
    saveCard(i) {
        console.log(i);
        /*common.req("capital/save.json", p, r => {
            this.setState({ctx:r})
        });*/
    },
    cancel() {
        document.location.href = "commission.web"
    },
    render() {
        return (
            <div>
                <nav className="breadcrumb">
                    <a className="breadcrumb-item" href="commission.web">财务结算</a>
                    <a className="breadcrumb-item active">财务信息</a>
                </nav>
                <div className="col-md-12 form-inline">
                    <div className="col-md-12 form-inline" style={{paddingTop: "10px"}}>
                        <div className="col-md-2">
                            <label>财务信息</label>
                        </div>
                        <div className="col-md-1">
                            <button className="btn btn-outline-danger" onClick={this.saveCard.bind(this, null)}>保存</button>
                        </div>
                        <div className="col-md-1">
                            <button className="btn btn-outline-danger" onClick={this.cancel.bind(this, null)}>取消</button>
                        </div>
                    </div>
                    <hr style={{border: "10px, solid", color: "red"}}/>

                    <div className="col-md-12 form-inline" style={{paddingTop: "10px"}}>
                        <div className="col-md-2">
                            <label>账户类型</label>
                        </div>
                        <div className="col-md-3">
                            <select className="form-control" defaultValue="1" onChange={v => console.log(v)}>
                                <option value="1">企业账户</option>
                                <option value="2">个人账户</option>
                            </select>
                        </div>
                    </div>
                    <div className="col-md-12 form-inline" style={{paddingTop: "10px"}}>
                        <div className="col-md-2">
                            <label>户名：</label>
                        </div>
                        <div className="col-md-4">
                            <input className="form-control"/>
                        </div>
                    </div>

                    <div className="col-md-12 form-inline" style={{paddingTop: "10px"}}>
                        <div className="col-md-2">
                            <label>开户地址：</label>
                        </div>
                        <div className="col-md-3">
                            <select className="form-control" defaultValue={env.cityList[1].id} onChange={v => console.log(v)}>
                                {env.cityList.map(t => <option key={t} value={t}>{t}</option>)}
                            </select>
                        </div>
                    </div>

                    <div className="col-md-12 form-inline" style={{paddingTop: "10px"}}>
                        <div className="col-md-2">
                            <label>开户银行：</label>
                        </div>
                        <div className="col-md-3">
                            <select className="form-control" defaultValue={env.bankList[0]} onChange={v => console.log(v)}>
                                {env.bankList.map(t => <option key={t} value={t}>t</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="col-md-12 form-inline" style={{paddingTop: "10px"}}>
                        <div className="col-md-2">
                            <label>银行卡号：</label>
                        </div>
                        <div className="col-md-4">
                            <input className="form-control" placeholder="卡号"/>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
});

$(document).ready(function () {
    ReactDOM.render(
        <MyCard/>,
        document.getElementById("content")
    );
});