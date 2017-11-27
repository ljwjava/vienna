"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/component.list.jsx';

var env = {
	payStatus: {"1":"未付款", "2":"已付款", "3":"已退款", "4":"部分退款", "5":"支付失败", "9":"支付异常"},
	orderStatus: {"1":"填写中", "2":"已提交", "3":"成功", "4":"失败", "5":"终止", "9":"异常"},
	search: null,
	from: 0,
	number: 10
}

class OrderList extends List {
	open(id) {
		document.location.href = "standard.web?proposalId=" + id;
	}
	refresh() {
		common.req("proposal/order/list.json", this.props.env, r => {
			this.setState({content:r});
		});
	}
	delete(id) {
		common.req("proposal/order/delete.json", {orderId:id}, r => {
			this.refresh();
		});
	}
	buildConsole() {
		return null;
	}
	buildTableTitle() {
		return (
			<tr>
				<th width="30%">订单名称</th>
				<th width="20%">修改时间</th>
				<th width="15%">保费</th>
				<th width="10%">状态（订单）</th>
				<th width="10%">状态（支付）</th>
				<th width="15%"></th>
			</tr>
		);
	}
	buildTableLine(v) {
		return (
			<tr key={v.id}>
				<td>{v.productName}</td>
				<td>{new Date(v.modifyTime).format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>{v.price}</td>
				<td>{env.orderStatus[v.status]}</td>
				<td>{env.payStatus[v.pay]}</td>
				<td>
					<a onClick={this.open.bind(this, v.id)} style={{marginRight:"10px"}}>编辑</a>
					<a onClick={this.delete.bind(this, v.id)}>删除</a>
				</td>
			</tr>
		);
	}
}

$(document).ready( function() {
	ReactDOM.render(
		<OrderList env={env}/>, document.getElementById("content")
	);
});