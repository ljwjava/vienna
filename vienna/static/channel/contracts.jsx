"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var strOf = function(s1, s2) {
	if (s1 == null || s1 == "")
		return s2;
	return s1;
}

env.timeOf = function(v, str) {
	return v == null ? (str ? str : "无限制") : v;
}

env.companyStr = function(v) {
    if (env.company == null)
        return null;
    var r = env.company[v];
    return r == null ? null : r.name;
}

env.termList = ["ALL", 1, 2, 3, 5, 10, 15, 20, 25, 30, 50, 55, 60, 65, 70, 75, 80, 88, 100, 999];
env.clauses = {};

var Contract = React.createClass({
    getInitialState() {
        return {show: false, clauses: env.clauses};
    },
	detail() {
    	let show = !this.state.show;
    	if (show)
    		this.refresh();
		else
            this.setState({show: false, detail: null});
	},
	refresh() {
        common.req("channel/contract/view.json", {contractId: this.props.value.id}, r => {
            this.setState({show: true, detail: r});
        });
	},
	save() {
    	let d = null;
    	let p = {
			contractId: this.props.value.id,
			platformId: this.refs.platform.value,
            name: this.refs.name.value,
			partyA: this.refs.partyA.value,
            partyB: this.refs.partyB.value,
            begin: this.refs.begin.value,
            end: this.refs.end.value,
			detail: d
        };
        common.req("channel/contract/save.json", p, r => {
        	this.detail(r);
        });
	},
	addItem() {
        let partyA = this.refs.partyA ? this.refs.partyA.value : null;
        if (!env.company[partyA] || env.company[partyA].type != 1)
            partyA = null;
        common.req("product/query.json", {typeId: 1, companyId: partyA}, r => {
            env.self.setState({contractId: this.props.value.id, clauses: r});
        });
	},
    setStatus(status) {
		if (!confirm("确认将合约状态改为【" + (status == 2 ? "生效" : "失效") + "】？"))
			return;
        common.req("channel/contract/status.json", {contractId: this.props.value.id, status: status}, r => {
            this.refresh();
        });
	},
	delete() {
        if (!confirm("确认删除改合约？"))
            return;
        common.req("channel/contract/delete.json", {contractId: this.props.value.id}, r => {
            this.props.parent.refresh();
        });
	},
	render() {
    	let v = this.state.detail ? this.state.detail : this.props.value;
    	let clauses = this.state.clauses ? this.state.clauses : null;
        let list = !this.state.show || !clauses ||  v.feeDefine == null ? null : v.feeDefine.map(x => {
            return (
				<tr className="text-center">
					<td>{x.productId == null ? "全部" : strOf(env.clauses[x.productId], x.productId)}</td>
					<td>{strOf(x.pay, "全部")}</td>
					<td>{strOf(x.insure, "全部")}</td>
					<td style={{width:"30%", padding:"6px"}}>
						<div className="form-inline">
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f1}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f2}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f3}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={x.f4}/>
							<input type="text" className="form-control col-2" defaultValue={x.f5}/>
						</div>
					</td>
					<td style={{padding:"6px"}}>
						<select className="form-control" defaultValue={x.unit}>
							<option value="3">保费百分比</option>
							<option value="4">收入百分比</option>
							<option value="2">定额</option>
						</select>
					</td>
					<td style={{padding:"6px"}}>
						<button className="btn btn-primary">删除</button>
					</td>
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
							<input ref="name" type="text" className="form-control" defaultValue={v.name}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>甲方（付款方）</label>
							<select ref="partyA" className="form-control" defaultValue={v.partyA}>
								{env.companySelect}
							</select>
						</div>
						<div className="col-md-4 mb-3">
							<label>乙方（收款方）</label>
							<select ref="partyB" className="form-control" defaultValue={v.partyB}>
                                {env.companySelect}
							</select>
						</div>
					</div>
					<div className="form-row">
						<div className="col-md-4 mb-3">
							<label>交易方式</label>
							<select ref="platform" className="form-control" defaultValue={v.platformId}>
								<option value="2">IYB/VIENNA</option>
								<option value="3">IYB/WEB</option>
								<option value="6">线下</option>
							</select>
						</div>
						<div className="col-md-4 mb-3">
							<label>起始时间</label>
							<input ref="begin" type="text" className="form-control is-valid" defaultValue={v.begin}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>结束时间</label>
							<input ref="end" type="text" className="form-control is-valid" defaultValue={v.end}/>
						</div>
					</div>
				</div>
                {fee}
				<div className="form-inline">
					<div className="mr-auto">
						<button className="btn btn-success mr-2" data-toggle="modal" data-target="#editor" onClick={this.addItem}>增加条目</button>
					</div>
					<button className="btn btn-danger mr-2" onClick={this.delete}>删除</button>
					<div className="btn-group mr-2" role="group">
						<button type="button" className={"btn btn" + (v.status != 2 ? "-outline" : "") + "-success"} onClick={this.setStatus.bind(this, 2)}>有效</button>
						<button type="button" className={"btn btn" + (v.status == 2 ? "-outline" : "") + "-success"} onClick={this.setStatus.bind(this, 3)}>失效</button>
					</div>
					<button className="btn btn-success" onClick={this.save}>保存修改</button>
				</div>
			</div>
        );
        return (
			<div className="card border-info mb-3">
				<nav className="navbar navbar-light text-white bg-info justify-content-between" onClick={this.detail}>
					合约（{v.name}）{this.state.show ? null : "：" + env.companyStr(v.partyA) + " → " + env.companyStr(v.partyB) + "（" + env.timeOf(v.begin) + " → " + env.timeOf(v.end, "长期有效") + "）"}
				</nav>
                {base}
			</div>
        )
    }
});

var FeeItem = React.createClass({
    getInitialState() {
        return {clauses:[]};
    },
    componentDidMount() {
    	env.self = this;
    },
    saveFee() {
    	let id = this.state.contractId;
        if (id != null) common.req("channel/contract/item.json", {contractId: id, clauseId: this.refs.clause.value, unit: this.refs.unit.value, pay: this.refs.pay.value, insure: this.refs.insure.value}, r => {
            this.setState({contract: r});
        });
    },
    render() {
    	return (
			<div className="modal fade" id="editor" tabIndex="-1" role="dialog" aria-hidden="true">
				<div className="modal-dialog modal-lg" role="document">
					<div className="modal-content">
						<div className="modal-header">
							<h5 className="modal-title" id="exampleModalLabel">新增费用项</h5>
							<button type="button" className="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div className="modal-body">
							<div className="form-row">
								<div className="col-md-6 mb-3">
									<label>条款</label>
									<select className="form-control" ref="clause">
										<option>全部</option>
										{ this.state.clauses.map(t => <option value={t.id}>{t.name}</option>) }
									</select>
								</div>
								<div className="col-md-6 mb-3">
									<label>费用单位</label>
									<select className="form-control" ref="unit">
										<option value="3">保费百分比</option>
										<option value="4">收入百分比</option>
										<option value="2">定额</option>
									</select>
								</div>
							</div>
							<div className="form-row">
								<div className="col-md-6 mb-3">
									<label>交费</label>
									<select className="form-control" ref="pay">
										{ env.termList.map(t => <option value={t}>{t}</option>) }
									</select>
								</div>
								<div className="col-md-6 mb-3">
									<label>保障</label>
									<select className="form-control" ref="insure">
										{ env.termList.map(t => <option value={t}>{t}</option>) }
									</select>
								</div>
							</div>
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-secondary" data-dismiss="modal">关闭</button>
							<button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.saveFee}>保存条目</button>
						</div>
					</div>
				</div>
			</div>
		);
    }
});

var Main = React.createClass({
    getInitialState() {
        return {clauses:[]};
    },
    componentDidMount() {
        common.req("product/query.json", {typeId: 1}, r => {
        	r.map(v => { env.clauses[v.id] = v.name; })
        });
        common.req("channel/company.json", {}, r => {
            if (r != null) {
            	env.company = r;
            	env.companySelect = [];
            	for (let c in r) {
                    env.companySelect.push(<option value={c}>{r[c].name}</option>);
				}
            }
            this.forceUpdate();
        });
        this.refresh();
    },
	refresh() {
        common.req("channel/contract/query.json", {companyId: env.companyId}, r => {
            this.setState({contract: r});
        });
	},
    newContract() {
        let p = {
            name: "新的合约",
            partyA: env.companyId,
			partyB: 34
        };
        common.req("channel/contract/save.json", p, r => {
            this.refresh();
        });
    },
	back() {
		document.location.href = "list.web";
	},
	render() {
        let list = this.state.contract == null ? null : this.state.contract.map(v => <Contract key={v.id} parent={this} value={v}/>);
		return (
			<div className="mt-3">
				{list}
				<button className="btn btn-outline-info btn-block" onClick={this.newContract}>新的合约</button>
				<FeeItem ref="pop"/>
			</div>
		);
	}
});

$(document).ready( function() {
    env.companyId = common.param("companyId");
    ReactDOM.render(
		<Main/>, document.getElementById("content")
    );
});