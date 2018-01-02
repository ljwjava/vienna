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

class UserList extends List {
    open(id) {
        document.location.href = "user.web?userId=" + id;
    }
    refresh() {
        common.req("user/list.json", {from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th><div>用户</div></th>
                <th><div>帐号</div></th>
				<th><div>手机</div></th>
                <th><div>EMAIL</div></th>
                <th><div>状态</div></th>
                <th><div>关联ID</div></th>
                <th><div>关联来源</div></th>
				<th style={{width:"200px"}}>{this.buildPageComponent()}</th>
			</tr>
        );
    }
    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return (
			<tr key={v.id}>
				<td><img src="../images/user.png" style={{width:"24px", height:"24px"}}/> {v.name}</td>
                <td>{v.loginName}</td>
                <td>{v.extra.mobile}</td>
                <td>{v.extra.email}</td>
                <td>{env.status[v.status]}</td>
                <td>{v.extra.referSrc}</td>
                <td>{v.extra.referId}</td>
				<td>
					<a onClick={this.open.bind(this, v.id)}>编辑</a>
				</td>
			</tr>
        );
    }
}

var Main = React.createClass({
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-12">
					<br/>
					<UserList env={env}/>
				</div>
			</div>
            <div className="modal fade" id="editor" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 className="modal-title" id="myModalLabel">
                                详情
                            </h4>
                        </div>
                        <div className="modal-body">
                            <div className="form">

                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.save}>确定</button>
                            <button type="button" className="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </div>
            </div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});