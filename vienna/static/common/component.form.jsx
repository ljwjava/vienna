"use strict";

import React from 'react';
import Switcher from './widget.switcher.jsx';
import Selecter from './widget.selecter.jsx';
import DateEditor from './widget.date.jsx';
import Inputer from './widget.inputer.jsx';
import MultiSwt from './widget.multiswt.jsx';

var Form = React.createClass({
	getInitialState() {
		return {alert:{}};
	},
	buildForm(form) {
		return form == null ? [] : form.map(v => {
			if (v == null) return null;
			if (this.props.defVal) {
				let vv = this.props.defVal[v.code];
				if (vv) v.value = vv;
			}
			let comp;
			let opt = v.onChange != null ? v.onChange : (v.refresh?this.onRefresh:this.onChange);
			if (v.type == "text") {
				comp = (<Inputer ref={v.code} valCode={v.code} valType="text" valReg={v.reg} valMistake={v.mistake} valReq={v.req} onChange={opt} placeholder={v.desc} value={v.value}/>);
			} else if (v.type == "switch") {
				comp = (<Switcher ref={v.code} valCode={v.code} onChange={opt} options={v.options} value={v.value}/>);
            } else if (v.type == "multiswt") {
                comp = (<MultiSwt ref={v.code} valCode={v.code} onChange={opt} options={v.options} value={v.value}/>);
			} else if (v.type == "date") {
				comp = (<DateEditor ref={v.code} valCode={v.code} valReq={v.req} onChange={opt} placeholder={v.desc} options={v.options} value={v.value}/>);
			} else if (v.type == "select") {
				comp = (<Selecter ref={v.code} valCode={v.code} onChange={opt} options={v.options} value={v.value} showAddit={v.showAddit}/>);
			} else if (v.type == "number") {
				comp = (<Inputer ref={v.code} valCode={v.code} valType="number" valReg={v.reg} valMistake={v.mistake} valReq={v.req} onChange={opt} placeholder={"请输入"+v.name} value={v.value}/>);
			} else if (v.type == "static") {
                comp = (<input ref={v.code} type="text" readOnly="true" value={v.value}/>);
            } else if (v.type == "label") {
                comp = (<div style={{textAlign: "right", paddingRight: "5px"}}>{v.value}</div>);
            } else if (v.type == "hidden") {
                comp = (<div style={{display: "none"}}><input ref={v.code} value={v.value}/></div>);
            } else {
				return null;
			}
			return [v.name, comp, v.code, v.type != "hidden", v.long];
		});
	},
	verifyAll() {
		let r = true;
		for (let v in this.refs) {
			r = this.onChange(this.refs[v]) & r;
		}
		return r;
	},
	val() {
		let r = {};
		for (let v in this.refs) {
			if (this.refs[v].val)
				r[v] = this.refs[v].val();
			else if (this.refs[v].value)
				r[v] = this.refs[v].value;
		}
		return r;
	},
	onRefresh(comp) {
		if (this.onChange(comp) && this.props.onRefresh != null)
			this.props.onRefresh();
	},
	onChange(comp) {
		if (comp != null && comp.verify != null) {
			let alert = comp.verify();
			let change = this.state.alert;
			change[comp.props.valCode] = alert;
			if (alert == null && this.verify)
				change = common.copy(change, this.verify(comp.props.valCode, comp.val()));
			this.setState({alert:change});
			return alert == null;
		} else {
			return true;
		}
	},
	render() {
		var r1 = [];
		var form = this.form();
		for (var i = 0; i < form.length;) {
			if (form[i] == null) {
				i++;
			} else if (form[i].length > 4 && form[i][4] > 1) {
				r1.push(<div className="form-field">
					<div className="col-sm-2 field-label">{form[i][0]}</div>
					<div className="col-sm-10 field-comp">{form[i][1]}</div>
				</div>);
				i++;
			} else {
				var r2 = [];
				for (var j = 0; j < 3 && i < form.length; i++) {
                    if (form[i] != null) {
                    	if (form[i].length > 4 && form[i][4] > 1) {
                    		break;
                        }
                        r2.push(<div className="col-sm-2 field-label">{form[i][0]}</div>);
                        r2.push(<div className="col-sm-2 field-comp">{form[i][1]}</div>);
                        j++;
                    }
				}
				r1.push(<div className="form-field">{r2}</div>);
			}
		}
		return (<div className="form-horizontal">{r1}</div>);
	}
});

module.exports = Form;

