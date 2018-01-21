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
	customers: []
}

var Customer = React.createClass({
    add() {
        this.props.parent.setState({popCus:1});
    },
    del(i) {
        env.customers.splice(i, 1);
        this.props.parent.setState({});
    },
    render() {
    	let c = env.customers.map((v, i) => {
            return <div className="row" key={i}>
                <div className="col line left">{v.name}</div>
                <div className="col line right"><span className="blockSel" onClick={this.del.bind(this,i)}>删除</span></div>
            </div>
        });
        return (<div className="common">
            <div className="title">▼ 客户<div style={{float:"right", paddingRight:"12px"}} onClick={this.add}>添加</div></div>
            <div className="form">
                <div className="tab">{c}</div>
            </div>
		</div>);
    }
});

class CForm extends Form {
    form() {
        if (this.props.fields == null)
            return [];
        return this.buildForm(this.props.fields.map(function(v) {
            return {name:v.label, code:v.name, type:v.widget, refresh:"yes", options:v.detail, value:v.value};
        }));
    }
}

var PopWin = React.createClass({
    getInitialState() {
        let plan = this.props.plan;
        let main = plan == null || plan.product == null || plan.product.length < 1 ? null : plan.product[0];
        return {mainId:(main == null ? null : main.productId), riders:null, products:{}};
    },
    componentDidMount() {
        if (this.state.mainId != null)
            this.main(null, this.state.mainId);
    },
    find(pid) {
        if (this.props.plan.product == null)
            return -1;
        for (let i in this.props.plan.product) {
            if (this.props.plan.product[i].productId == pid)
                return i;
        }
        return -1;
    },
    main(c, code) {
        if (code == null || code == "") {
            this.setState({mainId:null, riders:null, products:{}});
        } else {
            common.req("proposal/plan/view_clause.json", {productId:code, index:this.find(code), planId:this.props.plan.planId}, r => {
                let s = {mainId:code, rider:null, products:{}};
                s.products[code] = r;
                this.resetSuv();
                this.setState(s, () => {
                    common.req("proposal/plan/list_clause.json", {productId:code, planId:this.props.plan.planId}, r => {
                        this.setState({riders:r});
                        this.props.plan.product.map(p => {
                            if (code != p.productId) this.append(p.productId);
                        });
                    });
                });
            });
        }
    },
    refresh(mainId) {
        common.req("proposal/plan/list_clause.json", {productId:mainId, planId:this.props.plan.planId}, r => {
            this.setState({riders:r});
            this.props.plan.product.map(p => {
                if (mainId != p.productId) this.append(p.productId);
            });
        });
    },
    onMainChange(c, code) {
        if (code == null || code == "") {
            this.setState({mainId:null, riders:null, products:{}});
        } else {
            common.req("proposal/plan/view_clause.json", {productId:code, index:this.find(code), planId:this.props.plan.planId}, r => {
                let s = {mainId:code, rider:null, products:{}};
                s.products[code] = r;
                this.resetSuv();
                this.setState(s, () => {
                    this.refresh(code);
                });
            });
        }
    },
    append(pid) {
        if (this.state.products[pid] != null) {
            this.state.products[pid] = null;
            this.resetSuv();
            this.setState({});
        } else common.req("proposal/plan/view_clause.json", {productId:pid, index:this.find(pid), planId:this.props.plan.planId}, r => {
            this.state.products[pid] = r;
            this.resetSuv();
            this.setState({});
        });
    },
    resetSuv() {
        let v1 = this.state.products["HUA00001"];
        let v2 = this.state.products["HUA00003"];
        let v3 = this.state.products["HUA00004"];
        if (v1 != null && (v2 != null || v3 != null)) {
            let opt = [];
            if (v2 != null)
                opt.push(["HUA00003",v2.name]);
            if (v3 != null)
                opt.push(["HUA00004",v3.name]);
            v1.other = {
                widget: "switch",
                name: "SAVE",
                detail: opt,
                label: "生存金存入"
            };
        } else {
            if (v1 != null) v1.other = null;
        }
    },
    submit() {
        let param = {planId:this.props.plan.planId, detail:[{
            productId: this.state.mainId,
            factors: this.refs[this.state.mainId].val()
        }]};
        for (let pid in this.state.products) {
            if (pid != this.state.mainId && this.refs[pid]) {
                param.detail.push({
                    productId: pid,
                    parentId: this.state.mainId,
                    factors: this.refs[pid].val()
                });
            }
        }
        common.req("proposal/plan/rebuild.json", param, r => {
            let plans = this.props.parent.state.plans;
            plans[r.planId] = r;
            this.props.parent.setState({pop:null, plans:plans});
        });
    },
    cancel() {
        this.props.parent.setState({pop:null});
    },
    render() {
        let mainId = this.state.mainId;
        let mainFF = mainId == null ? null : this.state.products[mainId];
        let riders = this.state.riders == null ? null : this.state.riders.map(c => {
            let ff = this.state.products[c.id];
            return <div>
                <div className="title" onClick={this.append.bind(this, c.id)}>
                    {c.name}
                    <div style={{verticalAlign:"middle", paddingRight:"10px", float:"right"}}>{ff == null ? "☆" : "★"}</div>
                </div>
                <div className="form">
                    {ff == null ? null : <CForm ref={c.id} fields={ff.factors}/>}
                </div>
            </div>;
        });
        let mainForm = mainFF == null ? null : mainFF.factors;
        if (mainForm != null && mainFF.other != null) {
            mainForm.push(mainFF.other);
        }
        return (<div className="pop">
            <div className="title">投保计划</div>
            <div className="form">
                <div className="tab">
                    <div className="row">
                        <div className="col line left">保险产品</div>
                        <div className="col line right"><Selecter key={mainId} options={[["","请选择"],["HUA00001","福临门年金"]]} value={mainId} onChange={this.main}/></div>
                    </div>
                </div>
                {mainForm == null ? null : <CForm ref={mainId} fields={mainForm}/>}
            </div>
            {riders}
            <br/><br/><br/>
            <div className="console" style={{zIndex: "2"}}>
                <div className="tab">
                    <div className="row">
                        <div className="col left" onClick={this.submit}>确定</div>
                        <div className="col right" onClick={this.cancel}>取消</div>
                    </div>
                </div>
            </div>
		</div>);
    }
});

var CusWin = React.createClass({
    getInitialState() {
        return {list:[]};
    },
    componentDidMount() {
        common.req("customer/query.json", {}, s => {
            this.setState({list:s});
        });
    },
    close() {
        this.props.parent.setState({popCus:null});
    },
    add(i) {
        env.customers.push(this.state.list[i]);
        this.props.parent.setState({popCus:null});
    },
    render() {
        var list = this.state.list.map((v, i) => {
            return <div className="row" key={v.id} onClick={this.add.bind(this,i)}>
                <div className="col line left">{v.name}</div>
                <div className="col line right">{v.birthday}</div>
            </div>
        });
        return (<div className="pop">
            <div className="head">
                客户列表
                <div style={{float:"right", paddingRight:"12px"}} onClick={this.close}>关闭</div>
            </div>
            <br/><br/>
            <div className="form">
                <div className="tab">{list}</div>
            </div>
        </div>);
    }
});

var Plan = React.createClass({
    edit() {
        this.props.parent.popWin(this.props.plan.planId);
    },
    app(c, i) {
        common.req("proposal/plan/customer.json", {planId:this.props.plan.planId, applicant:env.customers[i]}, s => {
            this.props.parent.resetPlan(s);
        });
    },
    ins(c, i) {
        common.req("proposal/plan/customer.json", {planId:this.props.plan.planId, insurant:env.customers[i]}, s => {
            this.props.parent.resetPlan(s);
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
        let cs = env.customers.map((v, i) => {
            return [i, v.name];
        })
        return (<div className="common">
            <div className="title">▼ 计划{p.planId}<div style={{float:"right", paddingRight:"12px"}} onClick={this.props.parent.clear.bind(this.props.parent, p.planId)}>删除</div></div>
            <div style={{background:"#FFF"}}>
                <div className="subtitle">客户</div>
                <div className="form">
                    <div className="tab">
                        <div className="row">
                            <div className="col line left">投保人</div>
                            <div className="col line right"><Switcher key="app" options={cs} onChange={this.app}/></div>
                        </div>
                        <div className="row">
                            <div className="col line left">被保险人</div>
                            <div className="col line right"><Switcher key="ins" options={cs} onChange={this.ins}/></div>
                        </div>
                    </div>
                </div>
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
		</div>);
    }
});


var Proposal = React.createClass({
    getInitialState() {
    	return {proposal:{detail:[]}, plans:{}, popCus:null, pop:null};
    },
    componentDidMount() {
        if (env.proposalId != null) {
            common.req("proposal/view.json", {proposalId:env.proposalId}, s => {
                this.refresh(s);
            });
        } else {
            common.req("proposal/create.json", {}, s => {
                this.refresh(s);
            });
        }
    },
	newPlan() {
    	let app = env.customers[0];
    	let ins = env.customers[1];
        common.req("proposal/create_plan.json", {proposalId:env.proposalId, applicant:app, insurant:ins}, s => {
            this.refresh(s);
        });
	},
    popWin(planId) {
        let plan = this.state.plans[planId];
        this.setState({pop:plan});
    },
    clear(planId) {
        common.req("proposal/delete_plan.json", {proposalId:env.proposalId, planId:planId}, r => {
            this.refresh(r);
        });
    },
    resetPlan(plan) {
        this.state.plans[plan.planId] = plan;
        this.setState({});
    },
    refresh(s) {
        this.setState({proposal: s});
        s.detail.map(planId => {
            common.req("proposal/plan/view.json", {planId:planId}, r => {
            	this.resetPlan(r);
            }, r => {
                this.state.plans[planId] = null;
                this.setState({});
                this.clear(planId);
            });
        });
    },
    preview() {
        document.location.href = "preview.mobile?proposalId=" + env.proposalId;
    },
   	render() {
    	let plans = this.state.proposal.detail.map((v, i) => {
    		let plan = this.state.plans[v];
            return <div>
				{plan == null ? "loading..." : <Plan parent={this} plan={plan}/>}
			</div>;
		});
    	var fixed;
    	if (this.state.popCus != null) {
    	    fixed = [<div className="desk"></div>, <CusWin parent={this}/>];
        } else if (this.state.pop != null) {
            fixed = [<div className="desk"></div>, <PopWin ref="popwin" parent={this} plan={this.state.pop}/>];
        } else {
    	    fixed = <div className="console">
                <div className="tab">
                    <div className="row">
                        <div className="col left" onClick={this.newPlan}>新的计划</div>
                        <div className="col right" onClick={this.preview}>预览</div>
                    </div>
                </div>
            </div>;
        }
        return (<div>
			<div><Customer parent={this}/></div>
			<div>{plans}</div>
            <br/><br/><br/>
            {fixed}
		</div>);
	}
});

$(document).ready( function() {
	env.proposalId = common.param("proposalId");
    common.req("user/login.json", {loginName:"test", password:"123456"}, r => {
        ReactDOM.render(
            <Proposal/>, document.getElementById("content")
        );
    });
});