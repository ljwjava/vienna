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
    buildPageAppend() {
        let env = this.props.env;
        env.total = this.state.content.total;
		let cur = env.from / env.number;
        let page = [];
		for (var i = cur - 4; i <= cur + 4; i++) {
			if (i >= 0 && i < env.total / env.number)
				page.push(<span key={i} onClick={this.page.bind(this, i)}>{i + 1}</span>);
		}
        return (
			<div style={{textAlign:"center"}}>
				<div style={{marginTop:"16px"}}>
					<a onClick={this.page.bind(this, cur - 1)}>上一页</a>
					&nbsp;&nbsp;{page}&nbsp;&nbsp;
					<a onClick={this.page.bind(this, cur + 1)}>下一页</a>
				</div>
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
				{this.buildPageAppend()}
			</div>
		);
	}
});

module.exports = List;

