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

env.termList = [null, 1, 2, 3, 5, 10, 15, 20, 25, 30, 50, 55, 60, 65, 70, 75, 80, 88, 100, 999];

var strOf = function(s1, s2) {
    if (s1 == null || s1 == "")
        return s2;
    return s1;
}

class ProductList extends List {
    refresh() {
        common.req("btbx/product/list.json", {typeId: 2, companyId: null, search: env.search, from: env.from, number: env.number}, r => {
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
                <th><div>状态</div></th>
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
                <td>{v.status}</td>
				<td>
					<a data-toggle="modal" data-target="#editor" onClick={this.props.parent.showFee.bind(this.props.parent, v)}>推广费</a>
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
        common.req("btbx/channel/company.json", {}, r => {
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
    showFee(v) {
        common.req("btbx/product/query_rate.json", {productId: v.id, platformId: 2}, r => {
            let list = !r ? null : r.map(x => {
                return (
                    <tr>
                        <td width="12%">
                            <input type="text" className="form-control" defaultValue={x.begin}/>
                        </td>
                        <td width="12%">
                            <input type="text" className="form-control" defaultValue={x.end}/>
                        </td>
                        <td>
                            <select className="form-control" defaultValue={x.factors.pay}>
                                { env.termList.map(t => <option value={t}>{strOf(t, "全部")}</option>) }
                            </select>
                        </td>
                        <td>
                            <select className="form-control" defaultValue={x.factors.insure}>
                                { env.termList.map(t => <option value={t}>{strOf(t, "全部")}</option>) }
                            </select>
                        </td>
                        <td width="18%">
                            <input type="text" className="form-control" defaultValue={x.saleFee}/>
                        </td>
                        <td width="10%">
                            <input type="text" className="form-control" defaultValue={x.upperBonus}/>
                        </td>
                        <td width="10%">
                            <input type="text" className="form-control" defaultValue={x.saleBonus}/>
                        </td>
                        <td>
                            <select className="form-control" defaultValue={x.unit}>
                                <option value="3">百分比</option>
                                <option value="1">比例</option>
                                <option value="2">定额</option>
                            </select>
                        </td>
                        <td></td>
                    </tr>
                );
            });
            let fee = (
                <table className="table table-bordered" key={v.id}>
                    <thead className="thead-light">
                        <tr>
                            <th>开始</th>
                            <th>结束</th>
                            <th>交费</th>
                            <th>保障</th>
                            <th>基础</th>
                            <th>上线</th>
                            <th>奖励</th>
                            <th>单位</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>{list}</tbody>
                </table>
            );
            this.setState({feeRate: fee});
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
                                {this.state.feeRate}
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-dismiss="modal">关闭</button>
                                <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.Clause}>保存修改</button>
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