"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
	env: "tst",
	search: null,
	from: 0,
	number: 10
}

class ServiceList extends List {
	restart(serviceId) {
		common.req("boot/restart.json", {serviceId: serviceId}, r => {
			alert(r);
		});
	}
	stop(serviceId) {
		common.req("boot/stop.json", {serviceId: serviceId}, r => {
			alert(r);
		});
	}
	viewLog(serviceId) {
		common.req("boot/log.json", {serviceId: serviceId}, r => {
			alert(r);
		});
	}
	refresh() {
		common.req("boot/list.json", this.props.env, r => {
			this.setState({content:r});
		});
	}
	buildConsole() {
		return (
			<div className="collapse navbar-collapse">
				<div className="nav navbar-nav">
					<input type="text" className="form-control"/>
				</div>
				<div className="nav navbar-nav">
					<button type="button" className="btn btn-primary" onClick={this.create}>查询</button>
				</div>
			</div>
		);
	}
	buildTableTitle() {
		return (
			<tr>
				<th>名称</th>
				<th>JAR</th>
				<th>CLASS</th>
				<th>PORT</th>
				<th>状态</th>
				<th>最近重启时间</th>
				<th></th>
			</tr>
		);
	}
	buildTableLine(v) {
		return (
			<tr key={v.id}>
				<td>{v.name}</td>
				<td>{v.jar}</td>
				<td>{v.class}</td>
				<td>{v.port}</td>
				<td>{v.state}</td>
				<td>{v.restartTime == null ? null : v.updateTime.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>
					<button type="button" className="btn btn-primary" onClick={this.restart.bind(this, v.id)}>重启</button>
					<button type="button" className="btn btn-primary" onClick={this.stop.bind(this, v.id)}>关闭</button>
					<button type="button" className="btn btn-primary" onClick={this.viewLog.bind(this, v.id)}>日志</button>
				</td>
			</tr>
		);
	}
}

$(document).ready( function() {
	ReactDOM.render(
		<ServiceList env={env}/>, document.getElementById("content")
	);
});