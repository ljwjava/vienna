"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    company: {},
    bizType: {},
    insType: {},
    type: 1,
    search: null,
    from: 0,
    number: 10
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
                <th>主要产品</th>
                <th>保单号</th>
                <th>投保人</th>
                <th>被保险人</th>
                <th>投保时间</th>
                <th>保费</th>
                <th>业务员</th>
                <th>操作</th>
            </tr>
        );
    }
    buildTableLine(v) {
        return (
            <tr key={v.id}>
                <td>{env.company[v.vendorId].name}</td>
                <td>{v.productName}</td>
                <td>{v.policyNo}</td>
                <td>{v.applicantName}</td>
                <td>{v.insurantName}</td>
                <td>{common.dateStr(v.insureTime)}</td>
                <td style={{textAlign:"right"}}>{v.premium}</td>
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
    create() {

    },
    render() {
        return (
            <div>
                <nav className="navbar navbar-light justify-content-between">
                    <div className="form-inline">
                        <h5 className="text-primary font-weight-bold mt-sm-1">【保单】</h5>
                        <h5 className="mt-sm-1">人身险</h5>
                    </div>
                    <div className="form-inline">
                        <button className="btn btn-outline-primary mr-2" id="upload">拖拽至此处上传</button>
                        <button className="btn btn-primary mr-2" onClick={this.create}>录入保单</button>
                    </div>
                </nav>
                <div  className="container-fluid">
                    <PolicyList env={env}/>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));

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

        var fd = new FormData();
        fd.append("index", env.index);
        fd.append("path", env.dir);
        for (var i=0;i<fileList.length;i++)
            fd.append("file", fileList[i]);
        $.ajax({url:common.url("btbx/policy/upload.file"), type:"POST", data:fd, xhrFields:{ withCredentials: true }, processData:false, contentType:false, success:function(r) {}, fail: function(r) {}, dataType:"json"});
    }, false);
});