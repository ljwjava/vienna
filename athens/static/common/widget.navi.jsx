"use strict";

import React from 'react';

var Navi = React.createClass({
	getInitialState() {
		let top = "0";
		if ((/iphone|ipod|ipad/gi).test(navigator.platform))
			top = "15px";
		return {top: top};
	},
	render() {
		return (
			<table className="navi" style={{paddingTop:this.state.top}}>
				<thead>
					<tr>
						{
							this.props.left == null ?
								<td style={{width:"70px"}}></td> :
								<td style={{width:"70px"}} onClick={this.props.left[0]}>{this.props.left[1]}</td>
						}
						<td>{this.props.title}</td>
						<td style={{width:"70px"}}></td>
					</tr>
				</thead>
			</table>
		);
	}
});

module.exports = Inputer;

