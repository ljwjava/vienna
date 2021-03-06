"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
	orgId: 1,
    search: null,
    from: 0,
    number: 20
}

var VendorTree = React.createClass({
    getInitialState() {
        return {orgs: []};
    },
	componentDidMount() {
        common.req("org/view_org.json", {orgId: this.props.orgId}, r => {
            this.setState({orgs: [r]});
        });
	},
    findChildren(org) {
    	if (org.children != null) {
            org.children = null;
            this.forceUpdate();
        } else common.req("org/list_org.json", {orgId: org.id}, r => {
            org.children = r;
            this.forceUpdate();
        });
	},
    setMemberList(orgId) {
        this.props.parent.setState({orgId: orgId}, () => {
            this.props.parent.refs.list.refresh();
		});
    },
	buildOrg(orgs) {
        return orgs.map(org => {
            var children = org.children == null ? null : this.buildOrg(org.children);
            var icon = <img src={org.children == null ? "../images/folder.png" : "../images/folder-open.png"} style={{width:"24px", height:"24px"}} onClick={this.findChildren.bind(this, org)}/>;
            return <div key={org.id}>
				<div>{icon} <a onClick={this.setMemberList.bind(this, org.id)}>{org.name}</a></div>
				<div style={{paddingLeft: "12px"}}>{children}</div>
			</div>;
        });
	},
    render() {
    	return <div>{this.buildOrg(this.state.orgs)}</div>;
	}
});

class MemberList extends List {
    upload() {
        document.location.href = "upload.web";
    }
    open(id) {
        document.location.href = "policy.web?policyId=" + id;
    }
    refresh() {
        common.req("org/list_member.json", {orgId: this.props.orgId, from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th><div>组员</div></th>
				<th><div>手机</div></th>
				<th style={{width:"200px"}}>{this.buildPageComponent()}</th>
			</tr>
        );
    }
    buildTableLine(v) {
        return (
			<tr key={v.id}>
				<td><img src="../images/user.png" style={{width:"24px", height:"24px"}}/> {v.name}</td>
				<td>{v.mobile}</td>
				<td>
					<a onClick={this.open.bind(this, v.id)}>编辑</a>
				</td>
			</tr>
        );
    }
}

var Main = React.createClass({
    getInitialState() {
        return {orgId: env.orgId};
    },
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-3">
					<br/>
					<VendorTree parent={this}/>
				</div>
				<div className="col-sm-9">
					<br/>
					<MemberList ref="list" env={env} orgId={this.state.orgId}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});