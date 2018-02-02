"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 20
}

env.upload = function() {
    document.location.href = "upload.web";
}

class PolicyList extends List {
    open(id) {
        document.location.href = "policy.web?policyId=" + id;
    }
    refresh() {
        common.req("policy/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
				<th><div>保单code</div></th>
				<th><div>主要产品</div></th>
				<th><div>投保人</div></th>
				<th><div>投保时间</div></th>
				<th><div>保费</div></th>
				<th><div>保单状态</div></th>
				<th>{this.buildPageComponent()}</th>
			</tr>
        );
    }
    buildTableLine(v) {
        let date = new Date(v.updateTime);
        return (
			<tr key={v.id}>
				<td>{v.policyNo}</td>
				<td>{v.productName}</td>
				<td>{v.applicantName}</td>
				<td>{date.format("yyyy-MM-dd hh:mm:ss")}</td>
				<td>{v.premium}</td>
				<td>{v.status}</td>
				<td><a onClick={this.open.bind(this, v.id)}>处理</a></td>
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
							<li id="upload"><a onClick={env.create}>上传保单</a></li>
						</ul>
					</div>
					<PolicyList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));

    $(document).on({
        dragleave:function(e){    //拖离
            e.preventDefault();
        },
        drop:function(e){  //拖后放
            e.preventDefault();
        },
        dragenter:function(e){    //拖进
            e.preventDefault();
        },
        dragover:function(e){    //拖来拖去
            e.preventDefault();
        }
    });

    var box = document.getElementById('upload'); //拖拽区域
    box.addEventListener("drop", function(e){
        e.preventDefault();
        var fileList = e.dataTransfer.files;
        if(fileList.length == 0)
            return false;

        var xhr = new XMLHttpRequest();
        xhr.open("post", common.url("policy/upload.file"), true);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhr.onreadystatechange = function() {};

        var fd = new FormData();
        fd.append("index", env.index);
        fd.append("path", env.dir);
        for (var i=0;i<fileList.length;i++)
            fd.append("file", fileList[i]);
        xhr.send(fd);

        console.log(xhr.response);
    }, false);
});