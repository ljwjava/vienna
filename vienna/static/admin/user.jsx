"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 20,
    status: {
        0: "未激活",
        1: "正常",
        9: "锁定",
    }
}

var RoleList = React.createClass({
    getInitialState() {
        return {roles: {}};
    },
    componentDidMount() {
        common.req("user/role.json", {userId: env.userId}, r => {
            var roles = {};
            r.allRoles.map(v => {
                roles[v.code] = v;
            });
            r.roles.map(v => {
                roles[v.code].select = true;
            });
            this.setState({roles: roles});
        });
    },
    setRole(code) {
        var roles = this.state.roles;
        roles[code].select = !roles[code].select;
        this.setState({roles: roles});
    },
    render() {
        var roles = [];
        for (var r in this.state.roles) {
            var v = this.state.roles[r];
            roles.push(<span key={v.code} onClick={this.setRole.bind(this, v.code)} className={v.select?"blockSel":"block"}>{v.name}</span>);
        }
        return <div className="form-horizontal">{roles}</div>;
    }
});

var Main = React.createClass({
    render() {
        return <div>
            <br/>
            角色信息
            <br/>
            <RoleList/>
		</div>;
    }
});

$(document).ready( function() {
    env.userId = common.param("userId");
    ReactDOM.render(<Main/>, document.getElementById("content"));
});