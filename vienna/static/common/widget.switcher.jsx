"use strict";

import React from 'react';

var Switcher = React.createClass({
	getInitialState() {
		return {value: this.props.value != null ? this.props.value : (this.props.options == null || this.props.options.length == 0 ? null : this.props.options[0][0])};
    },
	val() {
		return this.state.value;
	},
	text() {
		let code = this.val();
		for (var v in this.props.options)
			if (this.props.options[v][0] == code)
				return this.props.options[v][1];
		return null;
	},
	verify() {
		return null;
	},
	change(code) {
		this.state.value = code; //setState是异步方法
		if (this.props.onChange)
			this.props.onChange(this, code);
		this.setState({value: code});
	},
	render() {
		let btns;
		if (this.props.options != null)
			btns = this.props.options.map(v => (<span key={v[0]} className={this.state.value==v[0]?"blockSel":"block"} onClick={this.change.bind(this, v[0])}>{v[1]}</span>));
		return (<span>{btns}</span>);
	}
});

module.exports = Switcher;

