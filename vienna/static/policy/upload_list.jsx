"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    company: {},
    search: null,
    from: 0,
    number: 20
}

class UploadList extends List {
    deal(id) {
    }
    refresh() {
        common.req("btbx/policy/upload_list.json", env, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
                <th><div>保险公司</div></th>
                <th><div>产品类型</div></th>
                <th><div>产品名称</div></th>
                <th><div>操作</div></th>
                <th><div>保单号</div></th>
                <th><div>批单号</div></th>
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
        return (
			<tr key={v.id}>
                <td>{v.companyName}</td>
                <td>{v.insType}</td>
                <td>{v.productName}</td>
                <td>{v.operate}</td>
                <td>{v.policyNo}</td>
				<td>{v.endorseNo}</td>
                <td>{v.applicantName}</td>
                <td>{v.vehiclePlateNo}</td>
				<td>{date.format("yyyy-MM-dd")}</td>
				<td style={{textAlign:"right"}}>{v.premium}</td>
                <td style={{textAlign:"right"}}>{v.feeRate}</td>
				<td style={{textAlign:"right"}}>{v.cmsRate}</td>
                <td>{v.agentName}</td>
				<td>
                    <div className="btn-group" role="group">
                        <button type="button" className="btn btn-default" onClick={this.deal.bind(this, v.id)}>处理</button>
                        <button type="button" className="btn btn-default">删除</button>
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
                    <div>
                        <button className="btn btn-primary mr-2">批次合并</button>
                        <button className="btn btn-primary">全部重试</button>
                    </div>
                    <div className="form-inline">
                        <input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"/>
                        <button className="btn btn-outline-success my-2 my-sm-0" type="submit">搜索</button>
                    </div>
                </nav>
                <UploadList env={env}/>
	    	</div>
        )
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});