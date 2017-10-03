"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/widget.navi.jsx';
import Inputer from '../common/widget.inputer.jsx';
import Selecter from '../common/widget.selecter.jsx';
import Switcher from '../common/widget.switcher.jsx';

var apply = {};

var Ground = React.createClass({
	getInitialState() {
		return {};
    },
    componentDidMount() {
		common.req("ware/perform.json", {platformId:3, opt:"getBanks"}, r => {
			this.setState({banks:r});
		});
	},
	submit() {
		if (!this.onCardChange()) {
			alert("请确认银行卡号输入正确");
			return;
		}
		apply.order.detail.pay.bankCode = this.refs.bank.val();
		apply.order.detail.pay.bankCard = this.refs.bankCard.val();
		apply.order.detail.pay.repay = 1;
		common.req("order/save.json", apply.order, function(r) {
			document.location.href = "life_wait.mobile?wareId=" + common.param("wareId") + "&orderId=" + apply.orderId;
		});
    },
	onCardChange() {
		let s1 = this.refs.bankCard.verify();
		let s2 = this.refs.bankCard.val() != this.refs.bankCard2.val() ? "请确认银行卡号输入正确" : null;
		this.setState({bankCard: s1, bankCard2: s2});
		return s1 == null && s2 == null;
	},
	render() {
		var pay = apply.order.detail.pay;
		var v = [
			['银行', (<Selecter ref="bank" options={this.state.banks} value={pay.bankCode}/>)],
			['卡号', (<Inputer ref="bankCard" onChange={this.onCardChange} valReq="yes" value={pay.bankCard} placeholder="请输入银行卡号"/>)],
			['确认卡号', (<Inputer ref="bankCard2" onChange={this.onCardChange} valReq="yes" value={pay.bankCard} placeholder="请再次输入银行卡号"/>)],
			['户名', (<span ref="1">{apply.order.detail.applicant.name}</span>)],
			['付款金额', (<span ref="2">{apply.order.price}</span>)]
		];
		let r = v.map(v => (
			<tr key={v[1].ref}>
				<td className="left">{v[0]}</td>
				<td className="right">
					<div>{v[1]}</div>
					<div className="alert">{this.state[v[1].ref]}</div>
				</td>
			</tr>
		));
		return (
			<div>
				<Navi title="重填支付信息"/>
				<table>
					<tbody>{r}</tbody>
				</table>
				<div className="console" onClick={this.submit}>{this.state.asking>0?this.state.asking:"提交"}</div>
			</div>
		);
	}
});

$(document).ready( function() {
	apply.orderId = common.param("orderId");
	common.req("order/view.json", {orderId: apply.orderId}, function (r) {
		apply.order = r;
		apply.packId = r.productId;
		apply.brokerId = r.owner;
		common.save("idb/orderId", apply.orderId);

		ReactDOM.render(
			<Ground/>, document.getElementById("content")
		);
	});
});