"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var ENV = {};

var Main = React.createClass({
	getInitialState() {
		return {env:[], functions:[]};
	},
	componentWillMount() {
		common.post(common.server() + "/develop/list_env.json", {}, r => {
			this.setState({env:r});
		});
	},
	save() {
		if (ENV.funcId != null) {
			common.post(common.server() + "/develop/save.json", {
				functionId: ENV.funcId,
				url: this.refs.reqUrl.value,
				postJson: this.refs.reqParams.value,
				name: this.refs.funcName.value,
				params: this.refs.funcParams.value,
				script: this.refs.script.value
			}, r => {
				alert("success");
			});
		}
	},
	test() {
		common.post(common.server() + "/test/" + this.refs.reqUrl.value, JSON.parse(this.refs.reqParams.value), r => {
			this.setState({
				result: r.result,
				exception: r.exception,
				console: r.console
			});
		});
	},
	replace() {
		if (ENV.funcId != null) {
			common.post(common.server() + "/develop/replace.json", {
				envId: ENV.envId,
				name: this.refs.funcName.value,
				params: this.refs.funcParams.value,
				script: this.refs.script.value
			}, r => {
				test();
			});
		}
	},
	refreshEnv() {
		common.post(common.server() + "/develop/list_function.json", {envId: this.refs.envList.value}, r => {
			this.setState({functions:r});
		});
	},
	refreshFunction() {
		if (this.refs.funcList.value == "") {
			this.refreshContent({});
		} else common.post(common.server() + "/develop/function.json", {functionId: this.refs.funcList.value}, r => {
			if (r.develop == null || r.develop == "")
				r.develop = r.script;
			this.refreshContent(r);
		});
	},
	reloadFunction() {
		if (ENV.funcId != null) {
			common.post(common.server() + "/develop/function.json", {functionId: ENV.funcId}, r => {
				r.develop = r.script;
				this.refreshContent(r);
			});
		}
	},
	refreshContent(r) {
		ENV.funcId = r.id;
		ENV.envId = r.envId;

		this.refs.funcName.value = r.name;
		this.refs.funcParams.value = r.params;
		this.refs.reqParams.value = r.postJson;
		this.refs.reqUrl.value = r.url;
		this.refs.script.value = r.develop;
	},
	render() {
		var envList = [];
		for (var envId in this.state.env) {
			envList.push(<option value={envId}>{"环境：" + this.state.env[envId]}</option>);
		}
		var functionList = this.state.functions.map(func => {
			return (<option value={func.id}>{"函数：" + func.name + (func.aka == null ? "" : "，别名：" + func.aka)}</option>);
		});
		return (
			<div className="form-horizontal">
				<br/>
				<div className="form-group">
					<div className="col-sm-3">
						<select className="form-control" ref="envList" onChange={this.refreshEnv}><option value="">请选择</option>{envList}</select>
					</div>
					<div className="col-sm-3">
						<select className="form-control" ref="funcList" onChange={this.refreshFunction}><option value="">请选择</option>{functionList}</select>
					</div>
					<div className="col-sm-3">
						<input className="form-control" ref="funcName" placeholder="函数名"></input>
					</div>
					<div className="col-sm-3">
						<input className="form-control" ref="funcParams" placeholder="参数（多个用逗号隔开）"></input>
					</div>
				</div>
				<div className="form-group">
					<div className="col-sm-12">
						<input className="form-control" ref="reqUrl" placeholder="接口URL"></input>
					</div>
				</div>
				<div className="form-group">
					<div className="col-sm-8">
						<textarea ref="script" className="form-control" style={{height:"800px"}}></textarea>
					</div>
					<div className="col-sm-4">
						<textarea ref="reqParams" className="form-control" style={{height:"800px"}}></textarea>
					</div>
				</div>
				<div className="form-group">
					<div className="col-sm-8">
						<input type="button" className="btn btn-primary btn-lg" value="请求 >>>>" onClick={this.test}/>
						&nbsp;&nbsp;
						<input type="button" className="btn btn-primary btn-lg" value="测试 >>>>" onClick={this.replace}/>
						&nbsp;&nbsp;
						<input type="button" className="btn btn-primary btn-lg" value="重置 >>>>" onClick={this.reloadFunction}/>
					</div>
					<div className="col-sm-4" style={{textAlign:"right"}}>
						<input type="button" className="btn btn-primary btn-lg" value="暂存 >>>>" onClick={this.save}/>
						&nbsp;&nbsp;
						<input type="button" className="btn btn-primary btn-lg" value="保存 >>>>" onClick={this.apply}/>
					</div>
				</div>
				<div className="form-group">
					<div className="col-sm-12"><pre id="result">{this.state.exception != null ? this.state.exception : this.state.result}</pre></div>
				</div>
				<div className="form-group">
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