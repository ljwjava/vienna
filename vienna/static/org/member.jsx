"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
	orgId: 10000,
    search: null,
    from: 0,
    number: 20
}

var OrgTree = React.createClass({
    getInitialState() {
        return {orgs: []};
    },
	componentDidMount() {
        common.req("btbx/org/view_org.json", {orgId: this.props.orgId}, r => {
            this.setState({orgs: [r]});
            this.findChildren(r);
        });
	},
    findChildren(org) {
    	if (org.children != null) {
            org.children = null;
            this.forceUpdate();
        } else common.req("btbx/org/list_org.json", {orgId: org.id}, r => {
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
    open(id) {
        document.location.href = "member.web?memberId=" + id;
    }
    refresh() {
        common.req("btbx/org/list_member.json", {orgId: this.props.orgId, from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th><div>组员</div></th>
				<th><div>手机</div></th>
                <th><div>Email</div></th>
                <th><div>入职时间</div></th>
                <th><div>状态</div></th>
				<th></th>
			</tr>
        );
    }
    buildTableLine(v) {
        let joinTime = v.joinTime == null ? null : new Date(v.joinTime).format("yyyy-MM-dd");
        return (
			<tr key={v.id}>
				<td><img src="../images/user.png" style={{width:"24px", height:"24px"}}/> {v.name}</td>
				<td>{v.mobile}</td>
                <td>{v.email}</td>
                <td>{joinTime}</td>
                <td>{v.status}</td>
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
				<div className="col-sm-2">
					<br/>
					<OrgTree orgId={env.orgId} parent={this}/>
				</div>
				<div className="col-sm-10">
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