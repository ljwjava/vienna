"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 20
}

class MainList extends List {
    open(id) {
        document.location.href = "channel.web?policyId=" + id;
    }
    refresh() {
        common.req("channel/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th><div>渠道名称</div></th>
				<th><div>执照号码</div></th>
				<th><div>所在地区</div></th>
				<th><div>email</div></th>
				<th>{this.buildPageComponent()}</th>
			</tr>
        );
    }
    buildTableLine(v) {
        let date = new Date(v.updateTime);
        return (
			<tr key={v.name}>
				<td>{v.certLicenseNo}</td>
				<td>{v.city}</td>
				<td>{v.email}</td>
				<td>{date.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td></td>
			</tr>
        );
    }
}

var Main = React.createClass({
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-12">
					<div className="container-fluid">
						<ul className="nav navbar-nav">
						</ul>
						<ul className="nav navbar-nav navbar-right">
							<li></li>
						</ul>
					</div>
					<MainList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});