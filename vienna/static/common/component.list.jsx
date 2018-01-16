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
			page.push(<li key={i} onClick={this.page.bind(this, i)} style={{textAlign:"center", color:"#000"}}>第 {i + 1} 页</li>);
		}
		return (
			<div className="dropdown">
				<a data-toggle="dropdown" href="#">{env.from / env.number + 1} ▼</a>
				<ul className="dropdown-menu">{page}</ul>
			</div>
		);
	},
	render() {
		return (
			<div className="listC">
				<table>
					<thead>{ this.buildTableTitle() }</thead>
					<tbody>{ this.state.content.list.map(v => this.buildTableLine(v)) }</tbody>
				</table>
			</div>
		);
	}
});

module.exports = List;

