"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/widget.navi.jsx';

var Doc = React.createClass({
	openDoc(link) {
		if ((/iphone|ipod|ipad/gi).test(navigator.platform))
			native.jsCallH5View(link, "");
		else
			document.location.href = link;
	},
   	render() {
   		let list = [];
   		for (let i in this.props.detail) {
			list.push(<a key={i} onClick={this.openDoc.bind(this, common.link(this.props.detail[i]))}>{"《"+i+"》"}</a>);
   		}
   		return (<div className="field">{this.props.name}：{list}</div>);
   	}
});

var Ware = React.createClass({
	left() {
		if (native)
			native.jsPopNativeVC("");
	},
	apply(packId) {
    	let plus = window.location.search;
    	if (plus != null && plus != "")
    		plus = "&" + plus.substring(1);
		common.save("idb/orderId", "");
    	document.location.href = "life_apply.mobile?packId=" + packId + plus;
    },
   	render() {
		let list = [];
		let v = this.props.detail;
		if (v.banner != null && v.banner.length > 0) {
			list.push(<img src={v.banner[0]} style={{width:"100%"}}/>);
		}
		if (v.detail != null && v.detail.length > 0) {
			let d = v.detail[0];
			if (d.summary != null) for (let label in d.summary) {
				d.summary[label].map(function(item) {
					if (item.type == "image") {
						list.push(<img src={item.content} style={{width:"100%"}}/>);
					} else if (item.type == "doc"){
						for (let docName in item.content)
							list.push(<Doc name={docName} detail={item.content[docName]}/>);
					}
				});
				break;
			}
			list.push(<div className="console" onClick={this.apply.bind(this, d.target)}>投保</div>);
		}
		return (
			<div>
				<Navi title="产品详情" left={[this.left, "<返回"]}/>
				{list}
			</div>
		);
	}
});

$(document).ready( function() {
	common.req("ware/view.json", {wareId:common.param("wareId")}, function (r) {
		ReactDOM.render(
			<Ware detail={r}/>, document.getElementById("content")
		);
	});
});