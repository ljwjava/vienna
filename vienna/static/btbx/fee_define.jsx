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
            return <div onClick={this.props.parent.show.bind(this, v.packId)}>{v.name}（{v.packName}）</div>
        });
    	return <div>{r}</div>;
	}
});

var FeeDetail = React.createClass({
    render() {
        var r = this.props.fee == null ? [] : this.props.fee;
        for (var i=r.length;i<5;i++)
            r.push("");
        return <table style={{width:"200px"}}><tbody><tr>
            {r.map(v => <td><div><input style={{width:"40px"}} value={v}/></div></td>)}
        </tr></tbody></table>;
    }
});

var FeeDefList = React.createClass({
    getInitialState() {
        return {content: []};
    },
    refresh(packId) {
        common.req("sale/list_feedef.json", {packId: packId}, r => {
            this.setState({content:r});
        });
    },
    fees(r) {
        r = r == null ? [] : r;
        for (var i=r.length;i<5;i++)
            r.push("");
        return r.map(v => <td><input style={{width:"60px"}} value={v}/> %</td>);
    },
    render() {
        var list = this.state.content.map(v => {
            var keys = [];
            var style = {width:"100px", backgroundColor:"#0AF", color:"#FFF", textAlign:"center", margin:"2px"};
            if (v.payPeriod != null)
                keys.push(<div style={style}>交{v.payPeriod}年</div>);
            if (v.insure != null)
                keys.push(<div style={style}>保至{v.insure}岁</div>);
            return [
                <tr>
                    <td rowSpan="3">腾保</td>
                    <td rowSpan="3">{keys}</td>
                    <td>平台 → 本人</td>
                    <td>佣金</td>
                    {this.fees(v.f1)}
                </tr>,
                <tr>
                    <td>厂商 → 中介</td>
                    <td>月结</td>
                    {this.fees(v.f3)}
                </tr>,
                <tr>
                    <td>平台 → 本人</td>
                    <td>奖金</td>
                    {this.fees(v.f4)}
                </tr>
            ]
        })
        return (<div className="listC">
            <table>
                <thead>
                    <tr>
                        <th rowSpan="1"><div>中介</div></th>
                        <th rowSpan="1"><div>匹配条件</div></th>
                        <th rowSpan="1"><div>费用流向</div></th>
                        <th rowSpan="1"><div>费用类型</div></th>
                        <th colSpan="1"><div>1</div></th>
                        <th colSpan="1"><div>2</div></th>
                        <th colSpan="1"><div>3</div></th>
                        <th colSpan="1"><div>4</div></th>
                        <th colSpan="1"><div>5</div></th>
                    </tr>
                </thead>
                <tbody style={{width:"center"}}>{list}</tbody>
            </table>
        </div>);
    }
});

var Main = React.createClass({
    show(packId) {
        this.refs.list.refresh(packId);
    },
    render() {
        return <div className="form-horizontal">
			<div className="form-group">
				<div className="col-sm-3">
					<br/>
					<PackTree parent={this}/>
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
    ReactDOM.render(<Main/>, document.getElementById("content"));
});