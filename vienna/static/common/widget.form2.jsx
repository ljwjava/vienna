"use strict";

import React from 'react';
import Switcher from './widget.switcher.jsx';
import Selecter from './widget.selecter.jsx';
import IdCard from './widget.idcard.jsx';
import CertValidEditor from './widget.certValidate.jsx';
import DateEditor from './widget.date.jsx';
import Inputer from './widget.inputer.jsx';
import OccupationPicker from './widget.occupationPicker.jsx';
import CityPicker from './widget.cityPicker.jsx';


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
			} else if (v.type == "date") {
				comp = (<DateEditor ref={v.code} valCode={v.code} valReq={v.req} onChange={opt} placeholder={v.desc} options={v.options} value={v.value}/>);
			} else if (v.type == "idcard") {
				comp = (<IdCard ref={v.code} valCode={v.code} valReq={v.req} valRelation={this.refs[v.relation]} onSucc={v.succ} onChange={opt} placeholder={v.desc} value={v.value} isIdCert={v.isIdCert}/>);
			} else if (v.type == "certValidate") {
				comp = (<CertValidEditor ref={v.code} valCode={v.code} valReq={v.req} onChange={opt} value={v.value}/>);
			} else if (v.type == "select") {
				comp = (<Selecter ref={v.code} valCode={v.code} onChange={opt} options={v.options} value={v.value} showAddit={v.showAddit}/>);
            } else if (v.type == "city") {
                comp = (<CityPicker ref={v.code} valCode={v.code} valType="city" company={v.company} onChange={opt} valReq={v.req} value={v.value}/>);
            } else if (v.type == "bankCity") {
                comp = (<CityPicker ref={v.code} valCode={v.code} valType="bankCity" company={v.company} onChange={opt} valReq={v.req} value={v.value}/>);
            } else if (v.type == "cityCorrect") {
                comp = (<CityPicker ref={v.code} valCode={v.code} valType="cityCorrect" company={v.company} onChange={opt} valReq={v.req} value={v.value}/>);
            } else if (v.type == "occupation") {
                comp = (<OccupationPicker ref={v.code} valCode={v.code} onChange={opt} valReq={v.req} value={v.value}/>);
			} else if (v.type == "number") {
				comp = (<Inputer ref={v.code} valCode={v.code} valType="number" valReg={v.reg} valMistake={v.mistake} valReq={v.req} onChange={opt} placeholder={"请输入"+v.name} value={v.value}/>);
			} else if (v.type == "static") {
                comp = (<input ref={v.code} type="text" readOnly="true" value={v.value}/>);
			} else if (v.type == "label") {
                comp = (<div style={{textAlign: "right", paddingRight: "5px"}}>{v.value}</div>);
			} else if (v.type == "labelvalue") {
                comp = (<div style={{textAlign: "right", paddingRight: "5px"}}><div dangerouslySetInnerHTML={{__html:v.options[0]}}></div><div style={{display: "none"}}><input ref={v.code} type="text" readOnly="true" value={v.value}/></div></div>);
            } else if (v.type == "hidden") {
                comp = (<div style={{display: "none"}}><input readOnly="true" ref={v.code} value={v.value}/></div>);
            } else {
				return null;
			}
			return [v.name, comp, v.code, v.type != "hidden"];
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
		let r = this.form().map(v => {
			if (v == null) return null;
			let txt = this.state.alert[v[2]];
			let canShow = v.length < 4 || v[3] ? "" : "none";
			return (
				<div className="row" key={v[2]} style={{display: canShow}}>
					<div className="col line">
						<div className="tab">
							<div className="row">
								<div className="col left">{v[0]}</div>
								<div className="col right">{v[1]}</div>
							</div>
						</div>
						{ txt == null || txt == "" ? null :
							<div className="error">{txt}</div>
						}
					</div>
				</div>
			);
		});
		return (<div className="tab">{r}</div>);
	}
});

module.exports = Form;

