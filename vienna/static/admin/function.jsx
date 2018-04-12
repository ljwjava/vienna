"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var ENV = {
	target: 1,
	script: "",
	reqParams: "",
	reqSupply: "",
	envMap: {}
};

var Main = React.createClass({
	getInitialState() {
		return {envList:[], functionList:[]};
	},
	componentDidMount() {
		common.req("develop/list_env.json", {}, r => {
			var envList = [];
			for (var envId in r) {
				var x = r[envId];
				ENV.envMap[envId + ""] = x;
				envList.push(<option value={envId}>{"环境：" + x}</option>);
			}
			this.setState({envList:envList});
		});

        var sct = [document.getElementById("script"), document.getElementById("reqParams")];
        for (var x in sct) sct[x].onkeydown = function(e) {
            if (e.keyCode === 9){
                var position = this.selectionStart + 4;
                this.value = this.value.substr(0, this.selectionStart) + '    ' + this.value.substr(this.selectionStart);
                this.selectionStart = position;
                this.selectionEnd = position;
                this.focus();
                e.preventDefault();
            }
        };
    },
	save() {
		var url = this.getUrl();
		if (url != null) common.req("develop/save.json", {
			url: url,
			param: this.refs.reqParams.value
		}, r => {
			alert("success");
		});
	},
	apply() {
		var req = null;
		if (ENV.funcId != null) {
			req = {
				type: 2,
				envId: ENV.envId,
				functionId: ENV.funcId,
				name: this.refs.funcName.value,
				params: this.refs.funcParams.value,
				script: this.refs.script.value
			}
			ENV.funcMap[ENV.funcId].name = req.name;
			ENV.funcMap[ENV.funcId].params = req.params;
			ENV.funcMap[ENV.funcId].script = req.script;
		}
		if (req != null) common.req("develop/apply.json", req, r => {
			alert("success");
		});
	},
	test() {
		var url = this.getUrl();
		if (url != null) {
			var jsonStr = this.refs.reqParams.value;
			var req = jsonStr == null || jsonStr == "" ? {} : JSON.parse(jsonStr);
			common.req("test/" + url, req, r => {
				this.setState({
					result: r.result,
					exception: r.exception,
					console: r.console
				});
			});
		}
	},
	replace() {
		var req = null;
		if (ENV.funcId != null) {
			req = {
				type: 2,
				envId: ENV.envId,
				name: this.refs.funcName.value,
				params: this.refs.funcParams.value,
				script: this.refs.script.value
			}
		}
		if (req != null) common.req("develop/replace.json", req, r => {
			this.test();
		});
	},
	getUrl() {
		if (ENV.gatewayId == null) return null;
		var reqUrl = ENV.gatewayMap[ENV.gatewayId].uri;
		var reqSupply = this.refs.reqSupply.value;
		if (reqSupply != null && reqSupply != "")
			reqUrl = reqUrl.replace("[*]", reqSupply);
		if (reqUrl.indexOf("*") >= 0)
			return null;
		return reqUrl;
	},
	setEnv() {
		ENV.funcId = null;
		ENV.envId = this.refs.envList.value;
		if (ENV.envId == "") ENV.envId = null;
		if (ENV.envId != null) common.req("develop/list_function.json", {envId: ENV.envId}, r => {
			ENV.funcMap = {};
			var functionList = r.map(x => {
				ENV.funcMap[x.id + ""] = x;
				return (<option value={x.id}>{"函数：" + x.name + (x.aka == null ? "" : "，别名：" + x.aka)}</option>);
			});
			this.setState({functionList:functionList});
		});
	},
	setFunction() {
		ENV.funcId = this.refs.funcList.value;
		if (ENV.funcId == "") ENV.funcId = null;
		if (ENV.funcId != null) common.req("develop/function.json", {functionId: ENV.funcId}, r => {
			this.refresh();
		});
	},
	reset() {
		if (ENV.gatewayId != null) {
			var reqUrl = ENV.gatewayMap[ENV.gatewayId].uri;
			var reqSupply = this.refs.reqSupply.value;
			if (reqSupply != null && reqSupply != "")
				reqUrl = reqUrl.replace("[*]", reqSupply);
			common.req("develop/req_testing.json", {url: reqUrl}, r => {
				ENV.reqParams = r.param;
				this.refresh();
			});
		}
	},
	refresh() {
		if (ENV.funcId != null) {
			this.refs.funcName.value = ENV.funcMap[ENV.funcId].name;
			this.refs.funcParams.value = ENV.funcMap[ENV.funcId].params;
		}
		this.refs.reqParams.value = ENV.reqParams;

		ENV.script = "";
		if (ENV.funcId != null)
			ENV.script = ENV.funcMap[ENV.funcId].script;
		this.refs.script.value = ENV.script;
	},
	render() {
		return (
			<div className="container-fluid mt-3 mb-3 form-horizontal">
				<div className="form-row mb-3">
					<div className="col-sm-3">
						<select className="form-control" ref="envList" onChange={this.setEnv}><option value="">请选择</option>{this.state.envList}</select>
					</div>
					<div className="col-sm-3">
						<select className="form-control" ref="funcList" onChange={this.setFunction}><option value="">请选择</option>{this.state.functionList}</select>
					</div>
					<div className="col-sm-3">
						<input className="form-control" ref="funcName" placeholder="函数名"></input>
					</div>
					<div className="col-sm-3">
						<input className="form-control" ref="funcParams" placeholder="参数（多个用逗号隔开）"></input>
					</div>
				</div>
				<div className="form-row">
					<div className="col-sm-8">
						<textarea id="script" ref="script" className="form-control" style={{height:"700px"}}></textarea>
					</div>
					<div className="col-sm-4">
						<textarea id="reqParams" ref="reqParams" className="form-control" style={{height:"700px"}}></textarea>
					</div>
				</div>
				<div className="form-row mt-3">
					<div className="col-sm-8">
						<input type="button" className="btn btn-primary btn-lg" value="测试 >>>>" onClick={this.test}/>
						&nbsp;&nbsp;
						<input type="button" className="btn btn-primary btn-lg" value="重置 >>>>" onClick={this.reset}/>
					</div>
					<div className="col-sm-4" style={{textAlign:"right"}}>
						<input type="button" className="btn btn-primary btn-lg" value="暂存 >>>>" onClick={this.save}/>
						&nbsp;&nbsp;
						<input type="button" className="btn btn-primary btn-lg" value="生效 >>>>" onClick={this.apply}/>
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