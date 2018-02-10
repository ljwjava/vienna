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
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-12">
					<div className="container-fluid">
                        <ul className="nav navbar-nav">
                            <li>
                                <form className="navbar-form" role="search">
                                    <div className="form-group">
                                        <input type="text" className="form-control" placeholder=""/>
                                    </div>
                                    <button type="submit" className="btn btn-success">搜索</button>
                                </form>
                            </li>
                        </ul>
                        <ul className="nav navbar-nav navbar-right">
                            <li>
                                <form className="navbar-form">
                                    <button className="btn btn-default">批次合并</button>
                                    &nbsp;
                                    <button className="btn btn-default">全部重试</button>
                                </form>
                            </li>
                        </ul>
                    </div>
					<UploadList env={env}/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});