"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    company: {},
    bizType: {},
    insType: {},
    search: null,
    type: 2,
    from: 0,
    number: 12
}

function companyOf(vendorId) {
    var r = env.company[vendorId];
    return r ? r.name : null;
}

class PolicyList extends List {
    open(v) {
        document.location.href = "policy.web?policyId=" + v.id;
    }
    componentDidMount() {
        super.componentDidMount();
        common.req("channel/company.json", {}, r => {
            if (r != null) env.company = r;
            this.setState({});
        });
    }
    refresh() {
        common.req("policy/list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
                <th>保险公司</th>
                <th>产品</th>
                <th>保单号</th>
				<th>投保人</th>
                <th>车牌号</th>
				<th>投保时间</th>
				<th>保费</th>
                <th>费用</th>
                <th>佣金</th>
                <th>业务员</th>
				<th>操作</th>
			</tr>
        );
    }
    buildTableLine(v) {
        let date = new Date(v.insureTime);
        if (!v.fee) v.fee = {};
        return (
			<tr key={v.id}>
                <td>{companyOf(v.vendorId)}</td>
                <td>{v.productName}</td>
                <td>{v.policyNo}</td>
				<td>{v.applicantName}</td>
                <td>{v.vehiclePlateNo}</td>
				<td>{date.format("yyyy-MM-dd")}</td>
				<td style={{textAlign:"right"}}>{v.premium}</td>
                <td style={{textAlign:"right"}}>{v.fee.income}</td>
				<td style={{textAlign:"right"}}>{v.fee.cms}</td>
                <td>{v.owner}</td>
                <td style={{padding:"6px"}}>
                    <button className="btn btn-outline-success mr-1" onClick={this.open.bind(this, v)}>编辑</button>
                    <button className="btn btn-outline-danger mr-1">删除</button>
                </td>
			</tr>
        );
    }
}

var Main = React.createClass({
    render() {
        return (
            <div>
                <nav className="navbar navbar-light justify-content-between">
                    <div></div>
                    <button className="btn btn-primary" id="upload">拖拽至此处上传</button>
                </nav>
                <PolicyList env={env}/>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));

    $(document).on({
        dragleave: function(e) { e.preventDefault() },
        drop: function(e) { e.preventDefault() },
        dragenter: function(e) { e.preventDefault() },
        dragover: function(e) { e.preventDefault() }
    });

    var box = document.getElementById('upload'); //拖拽区域
    box.addEventListener("drop", function(e){
        e.preventDefault();
        var fileList = e.dataTransfer.files;
        if(fileList.length == 0)
            return false;

        var fd = new FormData();
        fd.append("index", env.index);
        fd.append("path", env.dir);
        for (var i=0;i<fileList.length;i++)
            fd.append("file", fileList[i]);
        $.ajax({url:common.url("policy/upload.file"), type:"POST", data:fd, xhrFields:{ withCredentials: true }, processData:false, contentType:false, success:function(r) {}, fail: function(r) {}, dataType:"json"});
    }, false);
});