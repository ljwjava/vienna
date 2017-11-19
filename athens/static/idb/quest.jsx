"use strict";

import React from 'react';

var Quest = React.createClass({
	getInitialState() {
		return {plus:false, quests:{}, size:0, selected: {}};
    },
	componentWillUpdate() {
		if (this.refs.Y != null)
			this.state.plus = this.refs.Y.checked;
	},
	setAlert(text) {
		this.setState({alert:text});
	},
	verify() {
		let x = this.props.options;
		if (x.type == "combo" && x.req == "yes") {
			let pass = false;
			for (let i in x.option) {
				if (this.refs[i].checked) {
					pass = true;
					break;
				}
			}
			if (!pass) {
				this.setState({alert:"请至少选择一个答案"});
				return false;
			}
		}
		for (let v in this.refs) {
			if (this.refs[v].verify != null) {
				if (!this.refs[v].verify()) {
					this.setState({alert:"请检查问题答案"});
					return false;
				}
			}
		}
		this.setState({alert:null});
		return true;
	},
    text() {
    	let r = {};
    	let v = this.props.options;
    	if (v.code != null && (v.type == "ask" || v.type == "line" || v.type == "combo")) {
    		r[v.code] = {text:v.text==null?v.quest:v.text, version:v.version};
    	} else if (v.type == "list") {
    		for (let i in this.refs)
				common.copy(r, this.refs[i].text());
    	}
    	return r;
    },
    select(index) {
		this.state.selected[index] = this.refs[index].checked;
    	for (let i in this.state.selected) {
    		if (this.state.selected[i]) {
		    	this.setState({plus: true});
		    	return;
			}
    	}
    	this.setState({plus: false});
    },
	render() {
		let res = null;
		let v = this.props.options;

		if (v == null)  {
		} else if (v.type == "list") {
			let i = 0;
			let c = v.option.map(function(v) {
				i++;
				if (typeof v == 'string') {
					return (<div key={i}>{v}</div>);
				} else if (v.type == 'text') {
					return (<div key={i}><input type="text" name={v.code}/></div>);
				} else {
					return (<div key={i}><Quest ref={v.code} options={v}/></div>);
				}
			});
			res = (
				<div>
					<div>{v.quest}</div>
					{c}
				</div>
			);
		} else if (v.type == "ask") {
			res = (
				<div>
					<div><input type="checkbox" ref="Y" name={v.code} value="Y" onClick={this.select.bind(this, "Y")}/>{v.quest}</div>
					{v.plus != null ? <QuestLine ref="line" show={this.state.plus} content={v.plus}/> : null}
					{this.state.alert != null ? (<div className="alert">{this.state.alert}</div>) : null}
				</div>
			);
		} else if (v.type == "line") {
			res = (
				<div>
					<QuestLine ref="line" content={v.option}/>
					{this.state.alert != null ? (<div className="alert">{this.state.alert}</div>) : null}
				</div>
			);
		} else if (v.type == "combo") {
			let opt = [];
			for (let i in v.option) {
				let vv = v.option[i];
				if (typeof vv == 'string') {
					opt.push(<span key={i}><input type="checkbox" name={v.code} ref={i} value={i} onClick={this.select.bind(this, i)}/>{vv}</span>);
				} else if (vv.type == 'text') {
					opt.push(<span key={i}><input type="checkbox" name={v.code} ref={i} value={i} onClick={this.select.bind(this, i)}/><input type="text"/></span>);
				} else {
					opt.push(<span key={i}><input type="checkbox" name={v.code} ref={i} value={i} onClick={this.select.bind(this, i)}/><QuestLine content={vv.option}/></span>);
				}
			}
			res = (
				<div>
					<div>{v.quest}</div>
					<div>
					{opt}
					{v.plus != null && this.state.plus ? <QuestLine content={v.plus}/> : null}
					</div>
					{this.state.alert != null ? (<div className="alert">{this.state.alert}</div>) : null}
				</div>
			);
		}

		return res;
	}
});

var QuestLine = React.createClass({
	getInitialState() {
		return {};
	},
	verify() {
		let r = true;
		if (this.props.show==null||this.props.show) {
			let i = 0;
			this.props.content.map(v => {
				i++;
				if (v.req == "yes") {
					let val = this.refs[i].value;
					r = r && (val != null && val != "")
				}
				if (v.reg != null) {
					r = r && !new RegExp(v.reg).test(this.refs[i].value);
				}
			});
		}
		return r;
	},
	notice(i, jsNotice) {
		if (jsNotice != null) {
			let val = this.refs[i].value;
			let res = {};
			res[i] = eval(jsNotice);
			this.setState(res);
		}
	},
   	render() {
   		let i = 0;
		let notice = [];
		let line = this.props.content.map(v => {
			i++;
			if (typeof v == 'string') {
				return (<span key={i}>{v}</span>);
			} else if (v.type == 'text') {
				if (this.state[i] != null)
					notice.push(<span key={"n"+i} className="notice">{this.state[i]} </span>);
				return (<input ref={i} key={i} type="text" name={v.code} className={v.length} placeholder={v.tag} onChange={this.notice.bind(this, i, v.jsNotice)}/>);
			} else {
				return (<Quest key={i} options={v}/>);
			}
		});
		return (
			<div style={{display:this.props.show==null||this.props.show?"inline":"none"}}>
				{line}
				{notice}
			</div>)
	}
});

module.exports = Quest;

