"use strict";

import React from 'react';

var DateEditor = React.createClass({
	getInitialState() {
		return {alert: null, text: null};
    },
    val() {
		return this.refs.self.value;
	},
	verify() {
		let alert = null;
		let val = this.val();
		if (val == null || val == "") {
			if (this.props.valReq == "yes")
				alert = "此项为必填项";
		}
		return alert;
	},
	setTime(time) {
		this.refs.self.value = time;
		this.setState({text:time});
	},
	onChange() {
		if (this.props.onChange)
			this.props.onChange(this);
        this.setState({text:this.refs.self.value});
	},
	change(val) {
        this.setTime(val);
	},
	render() {
		let val = this.props.value;
		if (val == null || val == "" || val == "now" || val == "today") val = new Date().format("yyyy-MM-dd");
		return (<input ref="self" type="date" placeholder={this.props.placeholder} onChange={this.onChange} onBlur={this.onChange} defaultValue={val}/>);
	}
});

module.exports = DateEditor;

