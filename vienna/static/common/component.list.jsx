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
		let p1 = 9, p2 = 9;
        let page = [];
        page.push(<button key="first" type="button" className="mr-1 btn btn-info" onClick={this.page.bind(this, 0)}><span className="glyphicon glyphicon-fast-backward"></span></button>);
		page.push(<button key="back" type="button" className={"mr-1 btn btn-info" + (cur > 0 ? "" : " disabled")} onClick={this.page.bind(this, cur - 1)}><span className="glyphicon glyphicon-chevron-left"></span></button>);
		for (var i = cur - p1; i <= cur + p2; i++) {
			if (i >= 0 && i < env.total / env.number)
				page.push(<button key={i} type="button" className={"mr-1 btn btn-" + (i == cur ? "info" : "outline-info")} onClick={this.page.bind(this, i)}>{i + 1}</button>);
		}
		page.push(<button key="next" type="button" className={"mr-1 btn btn-info" + (cur + 1 < env.total / env.number ? "" : " disabled")} onClick={this.page.bind(this, cur + 1)}><span className="glyphicon glyphicon-chevron-right"></span></button>);
        page.push(<button key="last" type="button" className="btn btn-info" onClick={this.page.bind(this, Math.floor(env.total / env.number))}><span className="glyphicon glyphicon-fast-forward"></span></button>);
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
			<table className="table table-bordered">
				<thead className="thead-light">{ this.buildTableTitle() }</thead>
				<tbody>{ this.state.content.list.map(v => this.buildTableLine(v)) }</tbody>
				<caption>{ this.buildPageAppend() }</caption>
			</table>
		);
	}
});

module.exports = List;

