"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var ENV = {
	runEnv: {},
    rtnType: {
    	"1": "文本",
        "2": "HTML",
        "3": "Action",
	},
    search: null,
    from: 0,
    number: 20
}

class PolicyList extends List {
    open(v) {
        document.location.href = "gateway.web?gatewayId=" + v.id;
    }
    refresh() {
        common.req("develop/list_gateway.json", ENV, r => {
            this.setState({content: r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th>环境</th>
				<th>URL</th>
				<th>类型</th>
				<th>登录</th>
				<th>功能</th>
				<th>操作</th>
			</tr>
        );
    }
    buildTableLine(v) {
        return (
			<tr key={v.id}>
				<td>{ENV.runEnv[v.envId]}</td>
				<td>{v.uri}</td>
				<td>{ENV.rtnType[v.type]}</td>
				<td>{v.login}</td>
				<td>{v.remark}</td>
				<td style={{padding:"6px"}}>
					<button className="btn btn-outline-success mr-1" onClick={this.open.bind(this, v)}>编辑</button>
					<button className="btn btn-outline-danger mr-1">删除</button>
				</td>
			</tr>
        );
    }
}

var Main = React.createClass({
    componentDidMount() {
        common.req("develop/query_env.json", {}, r => {
            ENV.runEnv = r;
            this.forceUpdate();
        });
    },
    create() {
    	var req = {
    		uri: this.refs.gatewayUri.value,
			envId: this.refs.envList.value,
			type: this.refs.rtnType.value,
			remark: this.refs.remark.value,
			login: this.refs.login.value,
			sequence: this.refs.sequence.value
		}

		if (req.envId == null || req.envId == "") {
            alert("请选择执行环境");
        } else if (req.uri == null || req.uri == "") {
			alert("请输入链接地址");
        } else {
            common.req("develop/save_gateway.json", {}, r => {
            	document.location.href = "gateway.web?gatewayId=" + r;
            });
		}
    },
    render() {
        return (
			<div>
				<nav className="navbar navbar-light justify-content-between">
					<div className="form-inline">
						<h5 className="text-primary font-weight-bold mt-sm-1">【列表】</h5>
						<h5 className="mt-sm-1">接口</h5>
					</div>
					<div className="form-inline">
						<button className="btn btn-primary mr-2" data-toggle="modal" data-target="#editor">新的接口</button>
					</div>
				</nav>
				<div  className="container-fluid">
					<PolicyList env={ENV}/>
				</div>

				<div className="modal fade" id="editor" tabIndex="-1" role="dialog" aria-hidden="true">
					<div className="modal-dialog modal-lg" style={{maxWidth:"1200px"}} role="document">
						<div className="modal-content">
							<div className="modal-header">
								<h5 className="modal-title" id="exampleModalLabel">新的接口</h5>
								<button type="button" className="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div className="modal-body">
								<div className="form-row mb-3">
									<div className="col-sm-6">
										<input className="form-control" ref="gatewayUri" placeholder="链接地址，如：policy/view.json"/>
									</div>
									<div className="col-sm-3">
										<select className="form-control" ref="envList">
											<option value="">请选择</option>
											{Object.keys(ENV.runEnv).map(v => <option value={v}>{ENV.runEnv[v]}</option>)}
										</select>
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
									<div className="col-sm-6">
										<input className="form-control" ref="remark" placeholder="接口功能简介"/>
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
							</div>
							<div className="modal-footer">
								<button type="button" className="btn btn-secondary" data-dismiss="modal">关闭</button>
								<button type="button" className="btn btn-primary" data-dismiss="modal" onClick={this.create}>保存</button>
							</div>
						</div>
					</div>
				</div>
			</div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});