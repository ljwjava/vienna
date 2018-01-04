"use strict";

import React from 'react';

var MultiSwt = React.createClass({
	getInitialState() {
		return {value: this.props.value != null ? this.props.value : {}};
    },
	val() {
		return this.state.value;
	},
	text() {
		let val = this.val();
		let r = {};
		for (var v in this.props.options)
			if (val[this.props.options[v][0]])
				r[this.props.options[v][0]] = this.props.options[v][1];
		return r;
	},
	verify() {
		return null;
	},
	change(code) {
		var val = this.state.value;
		val[code] = !val[code];

		if (this.props.onChange)
			this.props.onChange(this, val);
		this.setState({value: val});
	},
	render() {
		let btns;
		if (this.props.options != null)
			btns = this.props.options.map(v => (<span key={v[0]} className={this.state.value[v[0]]?"blockSel":"block"} onClick={this.change.bind(this, v[0])}>{v[1]}</span>));
		return (<span>{btns}</span>);
	}
});

module.exports = Switcher;

