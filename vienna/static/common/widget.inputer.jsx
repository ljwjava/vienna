"use strict";

import React from 'react';

var Inputer = React.createClass({
	getInitialState() {
		return {reg:null};
    },
   	componentDidMount() {
		if (this.props.valReg)
			this.state.reg = new RegExp(this.props.valReg);
		else if (this.props.valType == "number")
			this.state.reg = new RegExp("^[0-9]*$");
	},
	val() {
		return $.trim(this.refs.self.value);
	},
	verify() {
		let alert = null;
		let val = this.val();
		//console.log(this.state.reg.text(val) + ", " + val);
		if (val == null || val == "") {
			if (this.props.valReq == "yes")
				alert = "此项为必填项";
			else
				alert = null;
		} else if (this.state.reg == null || this.state.reg.test(val)) {
			alert = null;
		} else {
			alert = this.props.valMistake ? this.props.valMistake : "请检查输入项";
		}
		return alert;
	},
	onChange() {
		if (this.props.onChange)
			this.props.onChange(this); 
	},
	render() {
		return (<input ref="self" type="text" placeholder={this.props.placeholder} onChange={this.onChange} onBlur={this.onChange} defaultValue={this.props.value}/>);
	}
});

module.exports = Inputer;

