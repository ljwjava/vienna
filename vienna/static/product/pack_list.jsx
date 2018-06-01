"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    company: {},
    search: null,
    from: 0,
    number: 12,
};

env.termList = ["ALL", 1, 2, 3, 5, 10, 15, 20, 25, 30, 50, 55, 60, 65, 70, 75, 80, 88, 100, 999];

var strOf = function(s1, s2) {
    if (s1 == null || s1 == "")
        return s2;
    return s1;
}

class ProductList extends List {
    refresh() {
        common.req("product/list.json", {typeId: 2, companyId: null, search: env.search, from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
                <th><div>产品ID</div></th>
				<th><div>产品CODE</div></th>
                <th><div>产品名称</div></th>
				<th><div>所属公司</div></th>
				<th></th>
			</tr>
        );
    }
    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return (
			<tr key={v.id}>
                <td>{v.id}</td>
                <td>{v.code}</td>
                <td>{v.name}</td>
                <td>{env.company[v.companyId]}</td>
                <td style={{padding:"6px"}}>
                    <button className="btn btn-outline-success mr-1" data-toggle="modal" data-target="#editor" onClick={this.props.parent.showFee.bind(this.props.parent, v)}>费用</button>
                    <button className="btn btn-outline-success mr-1">编辑</button>
                </td>
			</tr>
        );
    }
}

var Main = React.createClass({
    getInitialState() {
        return {company: null};
    },
    componentDidMount() {
        common.req("channel/company.json", {}, r => {
            if (r != null) {
                env.company = {};
                let company = [];
                for (let c in r) {
                    env.company[c] = r[c].name;
                    company.push(<option value={c}>{r[c].name}</option>);
                }
                this.setState({company: company});
            }
        });
    },
    addItem() {
        this.state.feeRate.push({factors: {pay: "ALL", insure: "ALL"}, productId: this.state.productId, freeze: 19, unit: 3});
        this.forceUpdate();
    },
    removeItem(i) {
        this.state.feeRate[i] = {id: this.state.feeRate[i].id};
        this.forceUpdate();
    },
    showFee(v) {
        common.req("product/query_rate.json", {productId: v.id, platformId: 2, schemeId: 1}, r => {
            r.map(v => {
                if (v.factors.pay == null)
                    v.factors.pay = "ALL";
                if (v.factors.insure == null)
                    v.factors.insure = "ALL";
            });
            this.setState({productId: v.id, feeRate: r});
        });
    },
    saveFeeRate() {
        common.req("product/save_rate.json", {productId: this.state.productId, platformId: 2, schemeId: 1, detail: this.state.feeRate}, r => {
            console.log(r);
        });
    },
    render() {
        return (
            <div>
                <nav className="navbar navbar-light justify-content-between">
                    <div></div>
                    <div className="form-inline">
                        <input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
                        <button className="btn btn-outline-success my-2 my-sm-0" type="submit">搜索</button>
                    </div>
                </nav>
                <ProductList ref="list" env={env} parent={this}/>

                <div className="modal fade" id="editor" tabIndex="-1" role="dialog" aria-hidden="true">
                    <div className="modal-dialog modal-lg" style={{maxWidth:"1200px"}} role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="exampleModalLabel">商品费用</h5>
                                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body">
                                <table className="table table-bordered" key={this.state.productId}>
                                    <thead className="thead-light">
                                    <tr>
                                        <th>开始</th>
                                        <th>结束</th>
                                        <th>交费</th>
                                        <th>保障</th>
                                        <th>基础</th>
                                        <th>上线</th>
                                        <th>奖励</th>
                                        <th>冻结</th>
                                        <th>单位</th>
                                        <th>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    { !this.state.feeRate ? null : this.state.feeRate.map((x, i) => x.factors == null ? null :
                                        <tr>
                                            <td width="12%">
                                                <input type="text" className="form-control" defaultValue={x.begin} onChange={y => {x.begin = y.target.value}}/>
                                            </td>
                                            <td width="12%">
                                                <input type="text" className="form-control" defaultValue={x.end} onChange={y => {x.end = y.target.value}}/>
                                            </td>
                                            <td>
                                                <select className="form-control" defaultValue={x.factors.pay} onChange={y => {x.factors.pay = y.target.value}}>
                                                    { env.termList.map(t => <option value={t}>{strOf(t, "全部")}</option>) }
                                                </select>
                                            </td>
                                            <td>
                                                <select className="form-control" defaultValue={x.factors.insure} onChange={y => {x.factors.insure = y.target.value}}>
                                                    { env.termList.map(t => <option value={t}>{strOf(t, "全部")}</option>) }
                                                </select>
                                            </td>
                                            <td width="12%">
                                                <input type="text" className="form-control" defaultValue={x.saleFee} onChange={y => {x.saleFee = y.target.value}}/>
                                            </td>
                                            <td width="8%">
                                                <input type="text" className="form-control" defaultValue={x.upperBonus} onChange={y => {x.upperBonus = y.target.value}}/>
                                            </td>
                                            <td width="8%">
                                                <input type="text" className="form-control" defaultValue={x.saleBonus} onChange={y => {x.saleBonus = y.target.value}}/>
                                            </td>
                                            <td width="8%">
                                                <input type="text" className="form-control" defaultValue={x.freeze} onChange={y => {x.freeze = y.target.value}}/>
                                            </td>
                                            <td width="12%">
                                                <select className="form-control" defaultValue={x.unit} onChange={y => {x.unit = y.target.value}}>
                                                    <option value="3">百分比</option>
                                                    <option value="1">比例</option>
                                                    <option value="2">定额</option>
                                                </select>
                                            </td>
                                            <td>
                                                <button className="btn btn-outline-danger mr-1" onClick={this.removeItem.bind(this, i)}>删除</button>
                                            </td>
                                        </tr>
                                    )}
                                    </tbody>
                                </table>
                                <div className="mr-auto" style={{color:"#F00"}}>注：1、开始及结束时间指当日0点（空白时为不限制），结束日期配置时请注意延后一天。2、配置错误问题很严重，务必注意！</div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-success mr-auto" onClick={this.addItem}>添加条目</button>
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">关闭</button>
                                <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.saveFeeRate}>保存修改</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});