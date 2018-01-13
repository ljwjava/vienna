"use strict";

import React from 'react';
import Switcher from './widget.switcher.jsx';
import Selecter from './widget.selecter.jsx';
import Occupation from './widget.occupation.jsx';
import CertEditor from './widget.cert.jsx';
import DateEditor from './widget.date.jsx';
import Inputer from './widget.inputer.jsx';
import City from './widget.city.jsx';

var Form = React.createClass({
	getInitialState() {
		return {alert:{}};
	},
	buildForm(form) {
		return form == null ? [] : form.map(v => {
			if (v == null) return null;
			if (this.props.defVal)
				v.value = this.props.defVal[v.code];
			let comp;
			let opt = v.refresh?this.onRefresh:this.onChange;
			if (v.type == "text") {
				comp = (<Inputer ref={v.code} valCode={v.code} valType="text" valReg={v.reg} valMistake={v.mistake} valReq={v.req} onChange={opt} placeholder={v.desc} value={v.value}/>);
			} else if (v.type == "switch") {
				comp = (<Switcher ref={v.code} valCode={v.code} onChange={opt} options={v.options} value={v.value}/>);
			} else if (v.type == "date") {
				comp = (<DateEditor ref={v.code} valCode={v.code} valReq={v.req} onChange={opt} placeholder={v.desc} options={v.options} value={v.value}/>);
			} else if (v.type == "cert") {
				comp = (<CertEditor ref={v.code} valCode={v.code} valReq={v.req} onChange={opt} value={v.value}/>);
			} else if (v.type == "select") {
				comp = (<Selecter ref={v.code} valCode={v.code} onChange={opt} options={v.options} value={v.value}/>);
			} else if (v.type == "occupation") {
				comp = (<Occupation ref={v.code} valCode={v.code} onChange={opt} valReq={v.req} value={v.value}/>);
			} else if (v.type == "city") {
				comp = (<City ref={v.code} valCode={v.code} onChange={opt} valReq={v.req} value={v.value}/>);
			} else if (v.type == "number") {
				comp = (<Inputer ref={v.code} valCode={v.code} valType="number" valReg={v.reg} valMistake={v.mistake} valReq={v.req} onChange={opt} placeholder={"请输入"+v.name} value={v.value}/>);
			} else {
				return null;
			}
			return [v.name, comp];
		});
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
	verify() {
		let change = {};
		for (let v in this.refs) {
			if (this.refs[v].verify) {
				let alert = this.refs[v].verify();
				if (alert != null)
					change[v] = alert;
			}
		}
		if (this.verifyOther)
			change = common.copy(change, this.verifyOther());
		this.setState({alert:change});
		//console.log(change);
		return Object.keys(change).length == 0;
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
			this.setState({alert:change});
			return alert == null;
		} else {
			return true;
		}
	},
	render() {
		let r = this.form().map(v => v == null ? null : (
			<tr key={v[1].ref}>
				<td className="left">{v[0]}</td>
				<td className="right">
					<div>{v[1]}</div>
					<div className="error">{this.state.alert[v[1].ref]}</div>
				</td>
			</tr>
		));
		return (<tbody>{r}</tbody>);
	}
});

module.exports = Form;

