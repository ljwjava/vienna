"use strict";

import React from 'react';
import DateEditor from './widget.date.jsx';

var CertValidEditor = React.createClass({
	getInitialState() {
		return {long:true, isInit: true};
    },
	val() {
		return {
			certExpire: this.state.long?null:this.refs.certExpire.val(),
			certLong: this.state.long
		};
	},
	verify() {
		let alert = null;
		let expire = this.state.long?null:this.refs.certExpire.val();
		if (expire != null && expire != "") {
			let y2 = Number(expire.substr(0, 4));
			let m2 = Number(expire.substr(5, 2));
			let d2 = Number(expire.substr(8, 2));
			let d = new Date();
			let y1 = d.getFullYear();
			let m1 = d.getMonth() + 1;
			let d1 = d.getDate();
			let pass = false;
			if (y2 > y1)
				pass = true;
			else if (y2 == y1 && m2 > m1)
				pass = true;
			else if (y2 == y1 && m2 == m1 && d2 > d1)
				pass = true;
			if (!pass)
				alert = "证件有效期需要在当前时间之后";
		}
		return alert;
	},
	onChange() {
		if (this.props.onChange)
			this.props.onChange(this); 
	},
	setExpire() {
		let val = this.state.long?null:this.refs.certExpire.val();
		this.setState({long:val == null || val == "", isInit: false});
	},
	setLong() {
		this.setState({long:!this.state.long, isInit: false});
	},
	render() {
		let val = this.props.value == null ? {} : this.props.value;
		if (this.state.isInit && val != null){
			this.state.long = val.certLong==null ? this.state.long : val.certLong;
			this.state.isInit = false;
		}

		return (
			<div>
				<a className="inputPad" onClick={!this.props.readOnly && this.setLong}><span style={{color:"#888"}}>{this.state.long?"◤ ":"      ◥"}</span>{this.state.long?"长期有效":""}</a>
				{this.state.long?null:<DateEditor ref="certExpire" valReq="yes" defaultValue="" onChange={this.setExpire} value={val.certExpire} readOnly={!!this.props.readOnly}/>}
			</div>
		);
	}
});

module.exports = CertValidEditor;

