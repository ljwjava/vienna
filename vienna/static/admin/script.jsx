"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Main = React.createClass({
	getInitialState() {
		return {envList:[]};
	},
	componentWillMount() {
		common.req("develop/list_env.json", {}, r => {
			var envList = [];
			for (var envId in r) {
				var x = r[envId];
				envList.push(<option value={envId}>{"环境：" + x}</option>);
			}
			this.setState({envList:envList});
		});
	},
	test() {
		var envId = this.refs.envList.value;
		var jsonStr = this.refs.params.value;
		var req = {
			envId: envId,
			script: this.refs.script.value,
			params: jsonStr == null || jsonStr == "" ? {} : JSON.parse(jsonStr)
        };
		common.req("develop/run.json", req, r => {
			this.setState({
				result: r.result,
				exception: r.exception,
				console: r.console
			});
		});
	},
	render() {
		return (
			<div className="container-fluid mt-3 mb-3 form-horizontal">
				<div className="form-row mb-3">
					<div className="col-sm-3">
						<select className="form-control" ref="envList" onChange={this.setEnv}>{this.state.envList}</select>
					</div>
				</div>
				<div className="form-row">
					<div className="col-sm-8">
						<textarea ref="script" className="form-control" style={{height:"600px"}}></textarea>
					</div>
					<div className="col-sm-4">
						<textarea ref="params" className="form-control" style={{height:"600px"}}></textarea>
					</div>
				</div>
				<div className="form-row mt-3">
					<div className="col-sm-8">
						<input type="button" className="btn btn-primary btn-lg" value="请求 >>>>" onClick={this.test}/>
					</div>
				</div>
				<div className="form-row">
					<div className="col-sm-12"><pre id="result">{this.state.exception != null ? this.state.exception : JSON.stringify(this.state.result)}</pre></div>
				</div>
				<div className="form-row">
					<div className="col-sm-12"><pre id="console">{this.state.console}</pre></div>
				</div>
			</div>
		);
	}
});

$(document).ready( function() {
	ReactDOM.render(
		<Main/>, document.getElementById("content")
	);
});