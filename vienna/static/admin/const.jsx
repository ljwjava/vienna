"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var ENV = {
    script: "",
    reqParams: "",
    reqSupply: "",
    gatewayMap: {},
    envMap: {}
};

var Main = React.createClass({
	getInitialState() {
		return {envList:[], gatewayList:[], modify:false};
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
	create() {

	},
    delete() {

    },
	save() {
		let url = this.getUrl();
		if (url != null) {
			let s = this.refs.script.value.replace(/[\r]/g, "");
			if (s == ENV.gatewayMap[ENV.gatewayId].script.replace(/[\r]/g, ""))
				s = null;
			common.req("develop/save.json", {
                key: url,
                value: {
                    script: s,
                    param: this.refs.reqParams.value
                }
            }, r => {
                alert("success");
            });
        }
	},
	apply() {
		var req = null;
		if (ENV.gatewayId != null) {
			req = {
				type: 1,
				gatewayId: ENV.gatewayId,
				script: this.refs.script.value
			};
			ENV.gatewayMap[ENV.gatewayId].script = req.script;
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
	setGateway() {
		ENV.gatewayId = this.refs.gatewayList.value;
		if (ENV.gatewayId == "") ENV.gatewayId = null;

		ENV.reqSupply = "";
		this.refs.reqSupply.value = ENV.reqSupply;
		this.refs.envList.value = ENV.gatewayMap[ENV.gatewayId].envId;

		var url = this.getUrl();
		if (url != null) common.req("develop/req_testing.json", {key: url}, r => {
            ENV.script = r != null ? r.script : null;
			ENV.reqParams = r != null ? r.param : null;
			this.refresh();
		});
	},
	reset() {
		if (ENV.gatewayId != null) {
			var reqUrl = ENV.gatewayMap[ENV.gatewayId].uri;
			var reqSupply = this.refs.reqSupply.value;
			if (reqSupply != null && reqSupply != "")
				reqUrl = reqUrl.replace("[*]", reqSupply);
            ENV.script = null;
            this.refresh();
		}
	},
	refresh() {
		this.refs.reqParams.value = ENV.reqParams;
		this.refs.reqSupply.value = ENV.reqSupply;

		this.refs.script.value = ENV.script != null ? ENV.script : ENV.gatewayMap[ENV.gatewayId].script;
		this.setState({modify: ENV.script != null});
	},
	render() {
		return (
			<div className="container-fluid mt-3 mb-3 form-horizontal">
				<div className="form-row mb-3">
					<div className="col-sm-3">
						<select className="form-control" ref="envList"><option value="">请选择</option>{this.state.envList}</select>
					</div>
				</div>
				<div className="form-row">
					<div className="col-sm-8">
						<textarea id="script" ref="script" className={"form-control" + (this.state.modify ? " border-danger" : "")} style={{height:"600px"}}></textarea>
					</div>
					<div className="col-sm-4">
						<textarea id="reqParams" ref="reqParams" className="form-control" style={{height:"600px"}}></textarea>
					</div>
				</div>
				<div className="form-row mt-3">
					<div className="col-sm-8">
						<input type="button" className="btn btn-primary btn-lg mr-3" value="请求 >>>>" onClick={this.test}/>
						<input type="button" className="btn btn-primary btn-lg" value="重置 >>>>" onClick={this.reset}/>
					</div>
					<div className="col-sm-4" style={{textAlign:"right"}}>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="新建 >>>>" onClick={this.create}/>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="暂存 >>>>" onClick={this.save}/>
						<input type="button" className="btn btn-primary btn-lg" value="生效 >>>>" onClick={this.apply}/>
					</div>
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