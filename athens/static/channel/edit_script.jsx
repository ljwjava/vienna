"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

//<select ref="script" className="form-control" onChange={this.change}>
//	<option value="env">环境脚本</option>
//	<option value="perform">执行脚本</option>
//</select>
//<br/>

var Main = React.createClass({
	run() {
		let req = eval(this.refs.text.value);
		common.req("ware/perform.json", {platformId: env.platformId, opt: "insure", type: 1, content: req}, r => {
			alert(r);
		});
	},
	save() {
		let req = {platformId:env.platformId};
		req.perform = this.refs.text.value;
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
							<textarea ref="param" style={{width:"100%", height:"300px"}} />
							<br/><br/>
							<textarea ref="text" style={{width:"100%", height:"800px"}} defaultValue={this.props.content.perform} />
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