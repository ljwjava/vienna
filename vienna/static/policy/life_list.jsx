"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    company: {},
    bizType: {},
    insType: {},
    search: null,
    from: 0,
    number: 20
}

class PolicyList extends List {
    open(id) {
        document.location.href = "policy.web?policyId=" + id;
    }
    componentDidMount() {
        super.componentDidMount();
        common.req("btbx/channel/company.json", {}, r => {
            if (r != null) env.company = r;
            this.setState({});
        });
    }
    refresh() {
        common.req("btbx/policy/list.json", env, r => {
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
                <td>{env.company[v.companyId].name}</td>
                <td>{v.productName}</td>
                <td>{v.policyNo}</td>
                <td>{v.applicantName}</td>
                <td>{v.insurantName}</td>
                <td>{common.dateStr(v.insureTime)}</td>
                <td style={{textAlign:"right"}}>{v.premium}</td>
                <td>{v.owner}</td>
                <td>
                    <a className="ml-2" onClick={this.open.bind(this, v.id)}>编辑</a>
                    <a className="ml-2">删除</a>
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
        xhr.open("post", common.url("btbx/policy/upload.file"), true);
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