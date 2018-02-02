"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var PackTree = React.createClass({
    getInitialState() {
        return {packs: []};
    },
	componentDidMount() {
        common.req("sale/list_pack.json", {}, r => {
            this.setState({packs: r});
        });
	},
    render() {
        var r = this.state.packs.map(v => {
            return <div onClick={this.props.parent.show.bind(this, v.packId)}>{v.name}</div>
        });
    	return <div>{r}</div>;
	}
});

var FeeDetail = React.createClass({
    render() {
        var r = this.props.fee.map(v => {
            return <input style={{width:"50px"}}>{v}%</input>
        });
        return <div>{r}</div>;
    }
}

class FeeDefList extends List {
    refresh(packId) {
        common.req("sale/list_feedef.json", {productId: packId}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return [
			<tr>
                <th rowSpan="2"><div>中介</div></th>
				<th rowSpan="2"><div>匹配条件</div></th>
				<th colSpan="5"><div>佣金（平台→本人）</div></th>
                <th colSpan="5"><div>佣金（平台→上线）</div></th>
                <th colSpan="5"><div>保险公司→中介（渠道费用）</div></th>
                <th colSpan="5"><div>平台→本人（奖金）</div></th>
                <th colSpan="5"><div>中介→平台（奖金）</div></th>
				<th rowSpan="2" style={{width:"200px"}}>{this.buildPageComponent()}</th>
			</tr>,
            <tr>
                <th>1</th>
                <th>2</th>
                <th>3</th>
                <th>4</th>
                <th>5</th>
                <th>1</th>
                <th>2</th>
                <th>3</th>
                <th>4</th>
                <th>5</th>
                <th>1</th>
                <th>2</th>
                <th>3</th>
                <th>4</th>
                <th>5</th>
                <th>1</th>
                <th>2</th>
                <th>3</th>
                <th>4</th>
                <th>5</th>
                <th>1</th>
                <th>2</th>
                <th>3</th>
                <th>4</th>
                <th>5</th>
            </tr>
        ];
    }
    buildTableLine(v) {
        return (
			<tr key={v.id}>
                <td>{v.agency}</td>
                <td>{v.group}／交{v.pay_period}年／保至{v.insure}岁</td>
				<td></td>
                <td></td>
			</tr>
        );
    }
}

var Main = React.createClass({
    show(packId) {
        this.refs.list.refresh(packId);
    },
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-3">
					<br/>
					<VendorTree parent={this}/>
				</div>
				<div className="col-sm-9">
					<br/>
					<FeeDefList ref="list"/>
				</div>
			</div>
		</div>;
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main orgId={env.orgId}/>, document.getElementById("content"));
});