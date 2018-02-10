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
		let p1 = 4, p2 = 4;
        let page = [];
        page.push(<button key="back" type="button" className="btn btn-default" onClick={this.page.bind(this, cur - 1)}>&larr;</button>);
		for (var i = cur - p1; i <= cur + p2; i++) {
			if (i >= 0 && i < env.total / env.number)
				page.push(<button key={i} type="button" className={"btn btn-" + (i == cur ? "primary" : "default")} onClick={this.page.bind(this, i)}>{i + 1}</button>);
		}
        page.push(<button key="next" type="button" className="btn btn-default" onClick={this.page.bind(this, cur + 1)}>&rarr;</button>);
        return (
			<div style={{textAlign:"center"}}>
				<div className="btn-group" role="group" style={{marginTop:"16px"}}>
					{page}
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

