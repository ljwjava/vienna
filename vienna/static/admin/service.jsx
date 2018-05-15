"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var ENV = {};

var Main = React.createClass({
	getInitialState() {
		return {srvList:[]};
	},
	componentDidMount() {
		common.req("develop/list_service.json", {}, r => {
			var srvList = r.map(x => {
				ENV.gatewayMap[x.id + ""] = x;
				return <option value={x.id}>{x.uri}</option>;
			});
			this.setState({srvList:gatewayList});
		});
	},
	create() {
	},
    delete() {
    },
	save() {
	},
	render() {
		return (
			<div className="container-fluid mt-3 mb-3 form-horizontal">
				<div className="form-row mb-3">
					<div className="col-sm-3">
						<select className="form-control" ref="srvList" onChange={this.setSrv}>
							<option value="">请选择</option>
							{this.state.srvList}
						</select>
					</div>
					<div className="col-sm-3">
						<select className="form-control" ref="envList" onChange={this.setEnv}>
							<option value="test">TEST</option>
							<option value="uat">UAT</option>
							<option value="prd">PRODUCT</option>
						</select>
					</div>
					<div className="col-sm-6">
						<input className="form-control" placeholder="URL">http://www.lerrain.com:7011</input>
					</div>
				</div>
				<div className="form-row">
					<div className="col-sm-3">
						fee/test.json
					</div>
					<div className="col-sm-6">
					</div>
					<div className="col-sm-3">
						<input type="button" className="btn btn-primary btn-lg mr-3" value="测试1" onClick={this.test}/>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="测试2" onClick={this.test}/>
						<input type="button" className="btn btn-primary btn-lg mr-3" value="测试3" onClick={this.test}/>
					</div>
				</div>
				<div className="form-row">
					<div className="col-sm-12">
						<textarea id="param" ref="param" style={{height:"600px"}}></textarea>
					</div>
				</div>
				<div className="form-row">
					<pre></pre>
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