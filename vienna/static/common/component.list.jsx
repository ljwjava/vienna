"use strict";

import React from 'react';

var List = React.createClass({
	getInitialState() {
		return {content:{list:[], total:0}};
	},
	componentDidMount() {
		this.refresh();
	},
	page(i) {
		let env = this.props.env;
		env.from = i * env.number;
		if (env.from < 0)
			env.from = 0;
		else if (env.from >= env.total)
			env.from = env.from - env.number;
		this.refresh();
	},
	buildPageComponent() {
		let page = [];
		let env = this.props.env;
		env.total = this.state.content.total;
		for (var i=0;i<env.total/env.number;i++) {
			page.push(<a key={i} onClick={this.page.bind(this, i)}>&nbsp;{i+1}&nbsp;</a>);
		}
		return (
			<div className="bottom">
				<a onClick={this.page.bind(this, env.from / env.number - 1)}>上一页&nbsp;&nbsp;</a>
				{page}
				<a onClick={this.page.bind(this, env.from / env.number + 1)}>&nbsp;&nbsp;下一页</a>
			</div>
		);
	},
	render() {
		return (
			<div className="listA">
				<br/>
				{ this.buildConsole() }
				<table>
					<thead>{ this.buildTableTitle() }</thead>
					<tbody>{ this.state.content.list.map(v => this.buildTableLine(v)) }</tbody>
				</table>
				<br/>
				{ this.buildPageComponent() }
			</div>
		);
	}
});

module.exports = List;

