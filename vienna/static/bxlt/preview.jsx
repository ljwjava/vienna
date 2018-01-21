"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Tabs from '../common/widget.tabs.jsx';
import Form from '../common/widget.form2.jsx';
import Inputer from '../common/widget.inputer.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import DateEditor from '../common/widget.date.jsx';
import ToastIt from '../common/widget.toast.jsx';

var env = {
	proposal:{detail:[]},
	plans:{}
}

var Family = React.createClass({
    render() {
        let t = 0;
    	let c = env.proposal.detail.map(x => {
    	    let v = env.plans[x];
    	    if (v == null)
    	        return null;
    	    t += Number(v.premium);
            return <tr>
                <td>{v.applicant.name}</td>
                <td>{v.insurant.name}</td>
                <td>{v.premium}</td>
            </tr>
        });
        return (<table className="family">
            <thead>
                <tr>
                    <th colSpan="3">家庭计划保费构成</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>投保人</td>
                    <td>被保险人</td>
                    <td>保费合计</td>
                </tr>
                {c}
                <tr>
                    <td colSpan="2">合计</td>
                    <td>{t}</td>
                </tr>
            </tbody>
        </table>);
    }
});

var Plan = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
        common.req("proposal/plan/format.json", {planId:this.props.plan.planId, style: "benefit"}, r => {
            var x = r.benefit.map(v => {
                return <span className="block">{v.name}</span>
            });
            this.setState({str:x, benefit:r.benefit[0]});
        });
    },
    render() {
        let p = this.props.plan;
        let list = p.product == null ? null : p.product.map((v, i) => {
            return (
                <tr key={i}>
                    <td style={{textAlign:"left"}}>{v.name}</td>
                    <td>{v.amount}</td>
                    <td>{v.insure}/{v.pay}</td>
                    <td>{v.premium}</td>
                </tr>
            );
        });
        return (<div className="common">
            <div className="title">▼ 计划{p.planId}</div>
            <div style={{background:"#FFF"}}>
                <div className="subtitle">投保险种</div>
                <table onClick={this.edit}>
                    <thead>
                    <tr>
                        <th>投保险种</th>
                        <th>保额</th>
                        <th>保障/交费期间</th>
                        <th>首年保费</th>
                    </tr>
                    </thead>
                    <tbody>{list}</tbody>
                </table>
            </div>
            <div>{this.state.str}</div>
            <div>{JSON.stringify(this.state.benefit)}</div>
        </div>);
    }
});

var Proposal = React.createClass({
    componentDidMount() {
        common.req("proposal/view.json", {proposalId:env.proposalId}, s => {
            env.proposal = s;
            env.proposal.detail.map(v => {
                common.req("proposal/plan/view.json", {planId:v}, r => {
                    if (r.product != null && r.product.length > 0) {
                        env.plans[v] = r;
                        this.setState({});
                    }
                }, r => {
                    env.plans[planId] = null;
                });
            });
            this.setState({});
        });
    },
   	render() {
        var ps = [];
        for (var k in env.plans) {
            ps.push(<Plan plan={env.plans[k]}/>);
        };
		return (<div>
			<div><Family/></div>
            <div>{ps}</div>
            <br/><br/><br/>
            <div className="console">
                <div className="tab">
                    <div className="row">
                        <div className="col left"></div>
                        <div className="col right">转发</div>
                    </div>
                </div>
            </div>
		</div>);
	}
});

$(document).ready( function() {
	env.proposalId = common.param("proposalId");
	ReactDOM.render(
		<Proposal/>, document.getElementById("content")
	);
});