"use strict";

import React from 'react';
import Selecter from './widget.selecter.jsx';

var City = React.createClass({
	getInitialState() {
		return {value: this.props.value, text: null, open:false, opt1:[], opt2:[], opt3:[]};
    },
    componentDidMount() {
		let company = (env.company != null) ? env.company : 'gwlife';
		common.req("dict/view.json", {company: company, name:"city"}, r => {
			let val3 = this.state.value; // == null ? ((env.company == "gwlife") ? "110101" : "110000") : this.state.value;
			let val2 = val3 == null ? ((company == "gwlife") ? "110100" : "110000") : val3.substr(0, 4) + "00";
			let val1 = val3 == null ? "110000" : val2.substr(0, 2) + "0000";
			let opt2 = r.city[val1] == null ? {} : r.city[val1].list;
			let opt3 = opt2[val2] == null ? {} : opt2[val2].list;
			let t1 = r.city[val1] == null ? null : r.city[val1].text;
			let t2 = opt2[val2] == null ? null : opt2[val2].text;
			let t3 = opt3[val3] == null ? null : opt3[val3].text;
			let text = val3 == null ? null : ((t1 == null || t1 == t2 ? "" : t1) + (t2 == null || t2 == t3 ? "" : t2) + (t3 == null ? "" : t3));
			this.setState({
	    		options: r.city,
				val1: val1,
				val2: val2,
	    		opt1: this.build(r.city),
	    		opt2: this.build(opt2),
	    		opt3: this.build(opt3),
				text: text
	    	});
		});
	},
    build(options) {
    	let opt = [];
    	for (let code in options) {
    		opt.push([code, options[code].text]);
    	}
    	return opt;
    },
	text() {
		let r = this.state.options[this.state.val1];
		let t1 = r.text;
		let t2 = r.list[this.state.val2].text;
		let t3 = r.list[this.state.val2].list[this.state.value].text;
		return (t1 == t2 ? "" : t1) + (t2 == t3 ? "" : t2) + t3;
	},
   	val() {
		return this.state.value;
	},
	verify() {
		let alert = null;
		let val = this.val();
		if (val == null || val == "") {
			if (this.props.valReq == "yes")
				alert = "此项为必填项";
		}
		return alert;
	},
	onChange1() {
		let val1 = this.refs.lv1.val();
		let val2;
		let opt2 = this.state.options[val1].list;
		let opt3;
		for (let code in opt2) {
			val2 = code;
    		opt3 = opt2[code].list;
    		break;
    	}
		this.setState({
			val1: val1,
			val2: val2,
			opt2: this.build(opt2),
			opt3: this.build(opt3)
		});
	},
	onChange2() {
		let val1 = this.refs.lv1.val();
		let val2 = this.refs.lv2.val();
		this.setState({
			val1: val1,
			val2: val2,
			opt3: this.build(this.state.options[val1].list[val2].list)
		});
	},
    onChange3() {
    },
	open() {
		this.setState({open:true});
	},
	select() {
		let v1 = this.refs.lv1.val();
		let v2 = this.refs.lv2.val();
		let v3 = this.refs.lv3.val();
		this.state.value = v3;

		let r = this.state.options[v1];
		let t1 = r.text;
		let t2 = r.list[v2].text;
		let t3 = r.list[v2].list[v3].text;
		this.setState({text:((t1 == t2 ? "" : t1) + (t2 == t3 ? "" : t2) + t3), open:false});

		if (this.props.onChange)
			this.props.onChange(this);
	},
	close() {
		this.setState({open:false});
	},
	render() {
		let oct = this.state.open ? (
			<div className="popwin">
				<div>
					<div>选择地区</div>
					<div className="line"/>
					<div>
						<br/>
						<Selecter ref="lv1" onChange={this.onChange1} options={this.state.opt1} value={this.state.val1}/>
						<br/><br/>
						<Selecter ref="lv2" onChange={this.onChange2} options={this.state.opt2} value={this.state.val2}/>
						<br/><br/>
						<Selecter ref="lv3" onChange={this.onChange3} options={this.state.opt3} value={this.state.value}/>
						<br/><br/>
					</div>
					<div className="line"/>
					<div>
						<span className="blockSel" onClick={this.select}>确定</span>
						<span className="blockSel" onClick={this.close}>取消</span>
					</div>
				</div>
			</div>
		) : null;
		return (<div><input readOnly="readOnly" placeholder="请选择地区" type="text" value={this.state.text==null?"":this.state.text} onClick={this.open}/>{oct}</div>);
	}
});

module.exports = City;

