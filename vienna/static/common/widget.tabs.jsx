"use strict";

import React from 'react';

var Tabs = React.createClass({
	getInitialState() {
		return {code:this.props.options[0][0]};
	},
	onChange(code) {
		this.props.onChange(code);
		this.setState({code:code});
	},
	render() {
		let tabs = this.props.options.map(d => (<li key={d[0]} className={(this.state.code == d[0]?" active":"")} onClick={this.onChange.bind(this, d[0])}>{d[1]}</li>));
		return (<ul className="tabcard">{tabs}</ul>);
	}
});

module.exports = Tabs;

