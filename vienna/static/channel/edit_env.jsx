"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Main = React.createClass({
	run() {
	},
	save() {
		let req = {platformId:env.platformId};
		req.env = this.refs.text.value;
		common.req("platform/save_script.json", req, function(r) {
			alert(r);
		});
	},
	change() {
		this.refs.text.value = this.props.content[this.refs.script.value];
	},
	render() {
		return (
			<table>
				<tbody>
					<tr>
						<td style={{width:"80%", padding:"20px 0 20px 10px"}}>
							<textarea ref="text" style={{width:"100%", height:"800px"}} defaultValue={this.props.content.env} />
						</td>
						<td style={{verticalAlign:"top", padding:"20px 10px 20px 20px"}}>
							<button type="button" className="btn btn-primary" onClick={this.save}>保存脚本</button>
						</td>
					</tr>
				</tbody>
			</table>
		);
	}
});

$(document).ready( function() {
	env.platformId = common.param("platformId");
	common.req("platform/view_script.json", {platformId:env.platformId}, function(r) {
		ReactDOM.render(
			<Main content={r}/>, document.getElementById("content")
		);
	});
});