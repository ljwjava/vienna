"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Menu = React.createClass({
	getInitialState() {
		return {menu: []};
	},
	componentDidMount() {
		let self = this;
		common.req("user/info.json", null, function(r) {
			self.setState({menu: r});
		});
	},
	link(url) {
		document.location.href = common.link(url);
	},
	render() {
		let menu = this.state.menu.map(v => (
			<li className="dropdown">
				<a data-toggle="dropdown" href="#">{v.name}</a>
				<ul className="dropdown-menu">
					{v.item.map(i => (<li><a onClick={this.link.bind(this, i.link)}>{i.name}</a></li>))}
				</ul>
			</li>
		))
		return (<ul className="nav navbar-nav">{menu}</ul>);
	}
});

$(document).ready( function() {
	ReactDOM.render(
		<Menu/>, document.getElementById("menu")
	);
});