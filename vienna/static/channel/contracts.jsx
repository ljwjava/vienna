"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var strOf = function(s1, s2) {
	if (s1 == null || s1 == "")
		return s2;
	return s1;
}

env.test = [{
	name: "新合约",
	begin: "2018-01-01",
	end: "2019-01-01",
	clauses: [{
		id: 10001,
		name: "和谐附加豁免保费轻症疾病保险",
		list: [{
            pay: "5",
			value: [80, 20, 5]
		}, {
            pay: "10",
            value: [100, 25, 5, 5, 5]
        }, {
            pay: "20",
            value: [110, 30, 5, 5, 5]
        }, {
            pay: "30",
            value: [120, 30, 5]
        }]
	}]
}]

env.timeOf = function(v, str) {
	return v == null ? (str ? str : "无限制") : v;
}

env.companyStr = function(v) {
    if (env.company == null)
        return null;
    var r = env.company[v];
    return r == null ? null : r.name;
}

env.termList = [null, 1, 2, 3, 5, 10, 15, 20, 25, 30, 50, 55, 60, 65, 70, 75, 80, 88, 100, 999];

var Contract = React.createClass({
    getInitialState() {
        return {show: false};
    },
    componentDidMount() {
    	this.reload();
    },
    reload() {
    	let partyA = this.refs.partyA ? this.refs.partyA.value : null;
    	if (!env.company[partyA] || env.company[partyA].type != 1)
    		partyA = null;
        common.req("btbx/product/query.json", {typeId: 1, companyId: partyA}, r => {
            this.setState({clauses: r});
        });
	},
	render() {
    	let v = this.props.value;
    	let clauses = this.state.clauses ? this.state.clauses : null;
        let list = !this.state.show || !clauses ||  v.feeDefine == null ? null : v.feeDefine.map(x => {
            return (
				<tr>
					<td>
						<div className="input-group" role="group">
							<select className="form-control" defaultValue={x.productId}>
								<option>全部</option>
								{ clauses.map(t => <option value={t.id}>{t.name}</option>) }
							</select>
							<div className="input-group-append" onClick={this.reload}>
								<span className="input-group-text">刷新</span>
							</div>
						</div>
					</td>
					<td>
						<select className="form-control" defaultValue={x.pay}>
							{ env.termList.map(t => <option value={t}>{strOf(t, "全部")}</option>) }
						</select>
					</td>
					<td>
						<select className="form-control" defaultValue={x.insure}>
                            { env.termList.map(t => <option value={t}>{strOf(t, "全部")}</option>) }
						</select>
					</td>
					<td width="50%">
						<div className="form-inline">
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f1}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f2}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f3}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f4}/>
							<input type="text" className="form-control col-2" defaultValue={x.f5}/>
						</div>
					</td>
					<td>
						<select className="form-control" defaultValue={x.unit}>
							<option value="3">保费百分比</option>
							<option value="4">收入百分比</option>
							<option value="1">保费比例</option>
							<option value="2">定额</option>
						</select>
					</td>
					<td><span className="glyphicon glyphicon-remove"></span></td>
				</tr>
            );
        });
        let fee = !this.state.show ? null : (
			<table className="table table-bordered">
				<thead className="thead-light">
					<tr>
						<th>条款</th>
						<th>交费</th>
						<th>保障</th>
						<th>收入</th>
						<th>单位</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>{list}</tbody>
			</table>
        );
        let base = !this.state.show ? null : (
			<div className="card-body text-secondary">
				<div>
					<div className="form-row">
						<div className="col-md-4 mb-3">
							<label>名称</label>
							<input type="text" className="form-control" defaultValue={v.name}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>甲方（付款方）</label>
							<select ref="partyA" className="form-control" defaultValue={v.partyA}>
                                {env.companySelect}
							</select>
						</div>
						<div className="col-md-4 mb-3">
							<label>乙方（收款方）</label>
							<select className="form-control" defaultValue={v.partyB}>
                                {env.companySelect}
							</select>
						</div>
					</div>
					<div className="form-row">
						<div className="col-md-4 mb-3">
							<label>起始时间</label>
							<input type="text" className="form-control is-valid" defaultValue={v.begin}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>结束时间</label>
							<input type="text" className="form-control is-valid" defaultValue={v.end}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>状态</label>
							<select className="form-control" defaultValue={v.status}>
								<option value="1">激活</option>
								<option value="2">失效</option>
							</select>
						</div>
					</div>
				</div>
                {fee}
				<div className="form-inline">
					<div className="mr-auto"></div>
					<button className="btn btn-secondary mr-2">取消</button>
					<button className="btn btn-danger">保存修改</button>
				</div>
			</div>
        );
        return (
			<div className="card border-info mb-3">
				<nav className="navbar navbar-light text-white bg-info justify-content-between" onClick={this.setState.bind(this, {show: !this.state.show}, null)}>
					合约（{v.name}）{this.state.show ? null : "：" + env.companyStr(v.partyA) + " → " + env.companyStr(v.partyB) + "（" + env.timeOf(v.begin) + " → " + env.timeOf(v.end, "长期有效") + "）"}
				</nav>
                {base}
			</div>
        )
    }
});

var Main = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
        let companyId = common.param("companyId");
        common.req("btbx/channel/query_contract.json", {companyId: companyId}, r => {
            this.setState({contract: r});
        });
        common.req("btbx/channel/company.json", {}, r => {
            if (r != null) {
            	env.company = r;
            	env.companySelect = [];
            	for (let c in r) {
                    env.companySelect.push(<option value={c}>{r[c].name}</option>);
				}
            }
            this.forceUpdate();
        });
    },
	back() {
		document.location.href = "list.web";
	},
	render() {
        let list = this.state.contract == null ? null : this.state.contract.map(v => <Contract value={v}/>);
		return (
			<div className="mt-3">
				{list}
				<button className="btn btn-outline-info btn-block">新的合约</button>
			</div>
		);
	}
});

$(document).ready( function() {
    ReactDOM.render(
		<Main/>, document.getElementById("content")
    );
});