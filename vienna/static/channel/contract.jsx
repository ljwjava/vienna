"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var strOf = function(s1, s2) {
	if (s1 == null || s1 == "")
		return s2;
	return s1;
}

env.test = {
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
}

env.contractOf = function(v) {
	return (
        <div className="card border-info mb-3">
			<div className="card-header text-white bg-info">合约</div>
			<div className="card-body text-secondary">
				<div>
					<div className="form-row">
						<div className="col-md-4 mb-3">
							<label>名称</label>
							<input type="text" className="form-control" defaultValue={v.name}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>甲方（付款方）</label>
							<input type="text" className="form-control" defaultValue={v.partyA}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>乙方（收款方）</label>
							<input type="text" className="form-control" defaultValue={v.partyB}/>
						</div>
					</div>
					<div className="form-row">
						<div className="col-md-4 mb-3">
							<label>名称</label>
							<input type="text" className="form-control" defaultValue={v.name}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>起始时间</label>
							<input type="text" className="form-control is-valid" defaultValue={v.begin}/>
						</div>
						<div className="col-md-4 mb-3">
							<label>结束时间</label>
							<input type="text" className="form-control is-valid" defaultValue={v.end}/>
						</div>
					</div>
				</div>
				<div className="panel panel-primary">
				</div>
			</div>
		</div>
    );
};

var Main = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
        let companyId = common.param("companyId");
        this.setState({contract: env.test});
    },
	back() {
		document.location.href = "list.web";
	},
	render() {
    	if (this.state.contract == null)
    		return null;
        let fee = this.state.contract.clauses.map(k => {
            let list = k.list.map(x => {
                return <tr>
					<td>{k.name}</td>
					<td>{x.pay}</td>
					<td>{strOf(x.insure, "*")}</td>
					<td width="50%">
						<div className="form-inline">
							<input type="text" className="form-control col-2 mr-2" defaultValue={0}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={0}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={0}/>
							<input type="text" className="form-control col-2 mr-2" defaultValue={0}/>
							<input type="text" className="form-control col-2" defaultValue={0}/>
						</div>
					</td>
					<td><span className="glyphicon glyphicon-remove"></span></td>
				</tr>;
            });
            return (
            	<table className="table table-bordered">
					<thead className="thead-light">
					<tr>
						<th><div>条款</div></th>
						<th><div>交费</div></th>
						<th><div>保障</div></th>
						<th><div>收入（%）</div></th>
						<th>操作</th>
					</tr>
					</thead>
					<tbody>{list}</tbody>
				</table>
			);
        });
        return (
			<div className="mt-3">
				{ env.contractOf(this.state.contract) }
				{ fee }
			</div>
		);
	}
});

$(document).ready( function() {
    ReactDOM.render(
		<Main/>, document.getElementById("content")
    );
});