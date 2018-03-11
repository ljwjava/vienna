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
                <th><div>保险公司</div></th>
                <th><div>业务</div></th>
				<th><div>产品类别</div></th>
                <th><div>产品名称</div></th>
                <th><div>保单号</div></th>
				<th><div>投保人</div></th>
                <th><div>车牌号</div></th>
				<th><div>投保时间</div></th>
				<th><div>保费</div></th>
                <th><div>费用</div></th>
                <th><div>佣金</div></th>
                <th><div>业务员</div></th>
				<th>操作</th>
			</tr>
        );
    }
    buildTableLine(v) {
        let date = new Date(v.insureTime);
        if (!v.fee) v.fee = {};
        return (
			<tr key={v.id}>
                <td>{env.company[v.companyId]}</td>
                <td>{env.bizType[Math.round(v.type/1000)]}</td>
				<td>{env.insType[v.type]}</td>
                <td>{v.productName}</td>
                <td>{v.policyNo}</td>
				<td>{v.applicantName}</td>
                <td>{v.vehiclePlateNo}</td>
				<td>{date.format("yyyy-MM-dd")}</td>
				<td style={{textAlign:"right"}}>{v.premium}</td>
                <td style={{textAlign:"right"}}>{v.fee.fee}</td>
				<td style={{textAlign:"right"}}>{v.fee.cms}</td>
                <td>{v.owner}</td>
				<td>
                    <div className="btn-group" role="group">
                        <button type="button" className="btn btn-default" onClick={this.open.bind(this, v.id)}>编辑</button>
                        <button type="button" className="btn btn-danger">删除</button>
                    </div>
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