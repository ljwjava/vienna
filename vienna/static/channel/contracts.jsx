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

env.contractOf = function(l) {
	return l == null ? null : l.map(v => {
        let fee = v.clauses.map(k => {
        	let list = k.list.map(x => {
				return <tr>
					<td>{k.name}</td>
					<td>{x.pay}</td>
					<td>{strOf(x.insure, "*")}</td>
					<td width="50%">
						<div className="has-success has-feedback">
							<div className="has-success has-feedback col-sm-2">
								<input type="text" className="form-control" defaultValue={0}/>
							</div>
							<div className="has-success has-feedback col-sm-2">
								<input type="text" className="form-control" defaultValue={0}/>
							</div>
							<div className="has-success has-feedback col-sm-2">
								<input type="text" className="form-control" defaultValue={0}/>
							</div>
							<div className="has-success has-feedback col-sm-2">
								<input type="text" className="form-control" defaultValue={0}/>
							</div>
							<div className="has-success has-feedback col-sm-2">
								<input type="text" className="form-control" defaultValue={0}/>
							</div>
						</div>
					</td>
					<td><span className="glyphicon glyphicon-remove"></span></td>
				</tr>;
			});
			return <table>
				<thead>
					<tr>
						<th><div>条款</div></th>
						<th><div>交费</div></th>
						<th><div>保障</div></th>
						<th><div>收入（%）</div></th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					{list}
					<tr>
						<td colSpan="4"></td>
						<td><span className="glyphicon glyphicon-plus"></span></td>
					</tr>
				</tbody>
			</table>;
        });
        return <div className="panel panel-primary">
			<div className="panel-heading">
				<h3 className="panel-title">合约（{v.begin} &rarr; {v.end}）</h3>
			</div>
			<div className="panel-body">
				<div className="form-group has-success has-feedback">
					<div className="col-sm-4">
						<label className="control-label col-sm-3">名称</label>
						<div className="col-sm-9">
							<input type="text" className="form-control" defaultValue={v.name}/>
						</div>
					</div>
					<div className="col-sm-4">
						<label className="control-label col-sm-3">起始时间</label>
						<div className="col-sm-9">
							<input type="text" className="form-control" defaultValue={v.begin}/>
						</div>
					</div>
					<div className="col-sm-4">
						<label className="control-label col-sm-3">结束时间</label>
						<div className="col-sm-9">
							<input type="text" className="form-control" defaultValue={v.end}/>
						</div>
					</div>
				</div>
				<br/><br/>
				<div className="panel panel-primary">
					<div className="panel-heading">
						<h3 className="panel-title">文档附件（拖拽至此上传）</h3>
					</div>
					<div className="panel-body">
					</div>
				</div>
				<div className="listC">
					{fee}
				</div>
			</div>
		</div>
    });
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
		return (
			<div>
				<div className="col-sm-12">
					<ul className="nav navbar-nav">
						<li><a onClick={this.back}><span className="glyphicon glyphicon-menu-left"></span>&nbsp;&nbsp;返回列表</a></li>
					</ul>
					<ul className="container-fluid nav navbar-nav navbar-right">
					</ul>
				</div>
				<div className="col-sm-12">
					{ env.contractOf(this.state.contract) }
				</div>
				<div className="col-sm-12">
					<div className="nav navbar-nav">
						<button className="btn btn-success btn-lg"><span className="glyphicon glyphicon-ok"></span>&nbsp;&nbsp;确定</button>
						&nbsp;&nbsp;&nbsp;
						<button className="btn btn-danger btn-lg"><span className="glyphicon glyphicon-remove"></span>&nbsp;&nbsp;取消</button>
					</div>
					<div className="container-fluid navbar-right">
						<button className="btn btn-default btn-lg"><span className="glyphicon glyphicon-plus"></span>&nbsp;&nbsp;新的合约</button>
					</div>
				</div>
			</div>
		);
	}
});

$(document).ready( function() {
    ReactDOM.render(
		<Main/>, document.getElementById("content")
    );
});