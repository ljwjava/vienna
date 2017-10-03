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
		for (var i=0;i<env.total/10;i++) {
			page.push(<button type="button" className="btn btn-primary" onClick={this.page.bind(this, i)}>{i+1}</button>);
		}
		return (
			<div className="bottom">
				<button type="button" className="btn btn-primary" onClick={this.page.bind(this, env.from / env.number - 1)}>&lt;&lt;</button>
				{page}
				<button type="button" className="btn btn-primary" onClick={this.page.bind(this, env.from / env.number + 1)}>&gt;&gt;</button>
			</div>
		);
	},
	render() {
		return (
			<div className="list">
				<br/>
				<div className="container-fluid">
					{ this.buildConsole() }
				</div>
				<br/>
				<table className="bordered">
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

