"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
	orgId: 10000,
    status: {
	    "1": "正常",
        "2": "离职",
        "3": "挂起"
    }
}

var OrgTree = React.createClass({
    getInitialState() {
        return {orgs: []};
    },
	componentDidMount() {
        common.req("org/view_org.json", {orgId: this.props.orgId}, r => {
            this.setState({orgs: [r]});
            this.findChildren(r);
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
    open(id) {
        document.location.href = "member.web?memberId=" + id;
    }
    refresh() {
        common.req("org/list_member.json", {orgId: this.props.orgId, from: this.props.env.from, number: this.props.env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th>组员</th>
                <th>证件</th>
				<th>手机</th>
                <th>Email</th>
                <th>入职时间</th>
                <th>状态</th>
				<th>操作</th>
			</tr>
        );
    }
    buildTableLine(v) {
        let joinTime = v.joinTime == null ? null : new Date(v.joinTime).format("yyyy-MM-dd");
        return (
			<tr key={v.id}>
				<td><img src="../images/user.png" style={{width:"24px", height:"24px"}}/> {v.name}</td>
                <td>{v.certNo}</td>
				<td>{v.mobile}</td>
                <td>{v.email}</td>
                <td>{joinTime}</td>
                <td>{env.status[v.status]}</td>
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
        return (
            <div>
                <div className="form-row">
                    <div className="col-sm-2">
                        <br/>
                        <OrgTree orgId={env.orgId} parent={this}/>
                    </div>
                    <div className="col-sm-10">
                        <nav className="navbar navbar-light justify-content-between">
                            <div>
                                <button className="btn btn-primary mr-2">新增人员</button>
                            </div>
                            <div className="form-inline">
                                <input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
                                <button className="btn btn-outline-success my-2 my-sm-0" type="submit">搜索</button>
                            </div>
                        </nav>
                        <MemberList ref="list" env={{from: 0, number: 12}} orgId={this.state.orgId}/>
                    </div>
                </div>
            </div>
        )
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});