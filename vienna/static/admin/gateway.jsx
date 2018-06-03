"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var ENV = {
    current: {},
	test: {},
	t: t => {
    	return t == null ? "" : t;
    }
};

var Main = React.createClass({
	getInitialState() {
		return {envList:[], modify:false};
	},
	componentDidMount() {
		common.req("develop/view_gateway.json", {gatewayId: common.param("gatewayId")}, r => {
			ENV.current = r;
            common.req("develop/load_cache.json", {key: "gateway/" + common.param("gatewayId")}, r => {
                ENV.test = r;
                this.refresh();
            });
		});
		common.req("develop/query_env.json", {}, r => {
			var envList = [];
			for (var envId in r)
				envList.push(<option value={envId}>{"环境：" + r[envId]}</option>);
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
    delete() {

    },
	save() {
		let s = this.refs.script.value.replace(/[\r]/g, "");
		if (ENV.current.script != null && s == ENV.current.script.replace(/[\r]/g, ""))
			s = null;
		common.req("develop/save_cache.json", {
			key: "gateway/" + ENV.current.id,
			value: {
                gatewayId: ENV.current.id,
                envId: this.refs.envList.value,
                type: this.refs.rtnType.value,
                forward: this.refs.forward.value,
                with: this.refs.with.value,
                remark: this.refs.remark.value,
                sequence: this.refs.sequence.value,
				script: s,
				uri: this.refs.gatewayUri.value,
				param: this.refs.reqParams.value
			}
		}, r => {
			alert("success");
		});
	},
	apply() {
		var req = null;
		if (ENV.current != null) {
			req = {
				gatewayId: ENV.current.id,
				envId: this.refs.envList.value,
				type: this.refs.rtnType.value,
				forward: this.refs.forward.value,
                with: this.refs.with.value,
                remark: this.refs.remark.value,
                login: this.refs.login.value,
                uri: this.refs.gatewayUri.value,
                sequence: this.refs.sequence.value,
				script: this.refs.script.value
			};
			ENV.current.script = req.script;
		}
		if (req != null) {
			common.req("develop/apply.json", req, r => {
                this.save();
            });
        }
	},
	test() {
		var rpm = this.refs.reqParams.value;
		var req = {
			envId: this.refs.envList.value,
			script: this.refs.script.value,
			param: rpm == null || rpm == "" ? null : JSON.parse(rpm)
		};
		common.req("develop/test.json", req, r => {
            this.setState({
                result: r.result,
                exception: r.exception,
                console: r.console
            });
        });
	},
	reset() {
		ENV.test = null;
		this.refresh();
	},
	refresh() {
		let vals = ENV.test ? ENV.test : ENV.current;

        this.refs.envList.value = ENV.t(vals.envId);
        this.refs.remark.value = ENV.t(vals.remark);
        this.refs.sequence.value = ENV.t(vals.sequence);
        this.refs.login.value = ENV.t(vals.login);
        this.refs.reqParams.value = ENV.t(vals.param);

        this.refs.gatewayUri.value = ENV.t(vals.uri);
        this.refs.script.value = vals.script == null ? ENV.t(ENV.current.script) : ENV.t(vals.script);

		this.setState({modify: ENV.test != null});
	},
	render() {
		return (
			<div className="container-fluid mt-3 mb-3 form-horizontal">
				<div className="form-row mb-3">
					<div className="col-sm-3">
						<input className="form-control" ref="gatewayUri"/>
					</div>
					<div className="col-sm-3">
						<input className="form-control" ref="with" placeholder="参数补充"/>
					</div>
					<div className="col-sm-3">
						<select className="form-control" ref="envList"><option value=""></option>{this.state.envList}</select>
					</div>
					<div className="col-sm-3">
						<select className="form-control" ref="rtnType">
							<option value="1">返回类型：文本</option>
							<option value="2">返回类型：HTML</option>
							<option value="3">返回类型：Action</option>
						</select>
					</div>
				</div>
				<div className="form-row mb-3">
					<div className="col-sm-3">
						<input className="form-control" ref="remark"/>
					</div>
					<div className="col-sm-3">
						<input className="form-control" ref="forward" placeholder="转发至"/>
					</div>
					<div className="col-sm-3">
						<select className="form-control" ref="login">
							<option value="N">登录：不需要</option>
							<option value="Y">登录：需要</option>
						</select>
					</div>
					<div className="col-sm-3">
						<select className="form-control" ref="sequence">
							<option value="">优先级：最低</option>
							<option value="1000">优先级：低</option>
							<option value="5000">优先级：中</option>
							<option value="10000">优先级：高</option>
							<option value="20000">优先级：最高</option>
						</select>
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
						<input type="button" className="btn btn-primary btn-lg mr-3" value="测试 >>>>" onClick={this.test}/>
					</div>
					<div className="col-sm-4" style={{textAlign:"right"}}>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="刷新" onClick={this.refresh}/>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="重置" onClick={this.reset}/>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="暂存" onClick={this.save}/>
						<input type="button" className="btn btn-danger btn-lg" value="生效" onClick={this.apply}/>
					</div>
				</div>
				<div className="form-row mt-3">
					<div className="col-sm-12">
						<textarea id="result" style={{width:"100%", height:"300px"}} value={this.state.exception != null ? this.state.exception : JSON.stringify(this.state.result)}></textarea>
					</div>
				</div>
				<div className="form-row mt-3">
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