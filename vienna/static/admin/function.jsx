"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var ENV = {
	current: {},
	test: null,
    t: t => {
        return t == null ? "" : t;
    }
};

var Main = React.createClass({
	getInitialState() {
		return {envList:[], functionList:[]};
	},
	componentDidMount() {
		common.req("develop/query_env.json", {}, r => {
			var envList = [];
			for (var envId in r) {
				var x = r[envId];
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
        let s = this.refs.script.value.replace(/[\r]/g, "");
        if (ENV.current.script != null && s == ENV.current.script.replace(/[\r]/g, ""))
            s = null;
        common.req("develop/save_cache.json", {
            key: "function/" + ENV.current.id,
            value: {
                envId: this.refs.envList.value,
                name: this.refs.funcName.value,
                script: s,
                params: this.refs.funcParams.value,
                reqParams: this.refs.reqParams.value
            }
        }, r => {
            alert("success");
        });
    },
	apply() {
		common.req("develop/apply_function.json", {
            envId: ENV.envId,
            functionId: ENV.funcId,
            name: this.refs.funcName.value,
            params: this.refs.funcParams.value,
            script: this.refs.script.value
        }, r => {
			alert("success");
		});
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
	setEnv() {
		let envId = this.refs.envList.value;
		if (envId) common.req("develop/list_function.json", {envId: envId}, r => {
			var functionList = r.map(x => <option value={x.id}>{"函数：" + x.name + (x.aka == null ? "" : "，别名：" + x.aka)}</option>);
			this.setState({functionList:functionList});
		});
	},
	setFunction() {
		let funcId = this.refs.funcList.value;
		if (funcId) common.req("develop/function.json", {functionId: funcId}, r => {
			ENV.current = r;
            common.req("develop/load_cache.json", {key: "function/" + funcId}, r => {
                ENV.test = r;
                this.refresh();
            });
		});
	},
	reset() {
        ENV.test = null;
        this.refresh();
	},
	refresh() {
        let vals = ENV.test ? ENV.test : ENV.current;
        this.refs.funcName.value = ENV.t(vals.name);
        this.refs.funcParams.value = ENV.t(vals.params);
        this.refs.reqParams.value = ENV.t(vals.reqParams);
        this.refs.script.value = vals.script == null ? ENV.t(ENV.current.script) : ENV.t(vals.script);

        this.setState({modify: ENV.test != null});
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
						<input type="button" className="btn btn-primary btn-lg mr-3" value="测试 >>>>" onClick={this.test}/>
					</div>
					<div className="col-sm-4" style={{textAlign:"right"}}>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="刷新" onClick={this.refresh}/>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="重置" onClick={this.reset}/>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="暂存" onClick={this.save}/>
						<input type="button" className="btn btn-danger btn-lg" value="生效" onClick={this.apply}/>
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