"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Ground = React.createClass({
    viewProduct(ware) {
    	let plus = window.location.search;
    	if (plus != null && plus != "")
    		plus = "&" + plus.substring(1);
    	if (ware.type == 1)
    		document.location.href = "life_ware.html?wareId=" + ware.id + plus;
    },
	render() {
		let list = this.props.list.map(v => (
			<tr className="list" onClick={this.viewProduct.bind(this, v)}>
				<th><img src={v.logo}/></th>
				<td>
					<div className="listTR">{v.name}</div>
					<div className="listTC">{v.remark}</div>
					<div className="listTB">{v.price}</div>
				</td>
			</tr>
		));
		return (
			<table>
				<tbody>{list}</tbody>
			</table>
		);
	}
});

$(document).ready( function() {
	common.post("https://lifeins.iyunbao.com/ware/list.json", {channel:"idb"}, function (r) {
		ReactDOM.render(
			<Ground list={r}/>, document.getElementById("content")
		);
	});
});