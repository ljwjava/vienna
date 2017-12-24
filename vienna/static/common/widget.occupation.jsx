"use strict";

import React from 'react';
import Selecter from './widget.selecter.jsx';

var Occupation = React.createClass({
	getInitialState() {
		return {value: this.props.value, text: null, open:false, opt1:[], opt2:[], options:{}};
    },
    componentDidMount() {
		common.req("dict/view.json", {company:"gwlife", name:"occupation"}, r => {
			let val2 = this.state.value;
			let val1 = val2 == null ? "101" : val2.substr(0, 3);
	    	this.setState({
	    		options: r.occupation,
				val1: val1,
	    		opt1: this.build(r.occupation),
	    		opt2: this.build(r.occupation[val1].list),
				text: val2 == null ? null : r.occupation[val1].list[val2].text
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
	onChange() {
		let val1 = this.refs.lv1.val();
		this.setState({
			val1:val1,
			opt2:this.build(this.state.options[val1].list)
		})
	},
    onChange2() {
    },
	open() {
		this.setState({open:true});
	},
	select() {
		let v1 = this.refs.lv1.val();
		let v2 = this.refs.lv2.val();
		this.state.value = v2;
		this.setState({text:this.state.options[v1].list[v2].text, open:false});

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
					<div>选择职业</div>
					<div className="line"/>
					<div>
						<br/>
						<Selecter ref="lv1" onChange={this.onChange} options={this.state.opt1} value={this.state.val1}/>
						<br/><br/>
						<Selecter ref="lv2" onChange={this.onChange2} options={this.state.opt2} value={this.state.value}/>
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
		return (<div><input readOnly="readOnly" placeholder="请选择职业" type="text" value={this.state.text==null?"":this.state.text} onClick={this.open}/>{oct}</div>);
	}
});

module.exports = Occupation;

