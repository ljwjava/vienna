"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Main = React.createClass({
	getInitialState() {
		return {city:[],sel:{}};
	},
	sel(code) {
		this.state.sel[code] = !this.state.sel[code];
		this.setState({});
		console.log(this.state.sel);
	},
	componentWillMount() {
		common.req("dict/view.json", {name: "city", version: "new"}, r => {
			this.setState({city:r.city});
		});
	},
	go() {
		let d = [];
        this.state.city.map(v => {
        	if (this.state.sel[v.value]) {
        		d.push(v);
			}
        });
        this.setState({result:JSON.stringify(d)});
	},
	render() {
		let c = this.state.city.map(v => {
			return <div key={v.value} onClick={this.sel.bind(this, v.value)} style={{color:this.state.sel[v.value]?"#F00":"#000"}}>{v.label}</div>
		});
		return <div>
			{c}
			<br/><br/><br/>
			<div onClick={this.go}>GOGOGO</div>
			<textarea value={this.state.result} style={{width:"800px", height:"300px"}}></textarea>
		</div>;
	}
});

$(document).ready( function() {
	ReactDOM.render(
		<Main/>, document.getElementById("content")
	);
});