"use strict";

import React from 'react';

var Selecter = React.createClass({
	val() {
		return this.refs.self.value;
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
	select(code) {
		this.refs.self.value = code;
		onChange();
	},
	onChange() {
		if (this.props.onChange)
			this.props.onChange(this, this.val());
	},
	change(code) {
		this.refs.self.value = code;
	},
	render() {
		if (this.props.options == null || this.props.options.length == 0)
			return (<span ref="self"></span>);
		let btns = this.props.options.map(v => (<option key={v[0]} value={v[0]}>{v[1]}</option>));
		return (<select ref="self" onChange={this.onChange} defaultValue={this.props.value}>{btns}</select>);
	}
});

module.exports = Selecter;

