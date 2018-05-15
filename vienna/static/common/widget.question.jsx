"use strict";

import React from 'react';

var QuestionBox = React.createClass({
	getInitialState() {
		// type: 1-单行选项 2-多行选项
		return {value: this.props.value, options: this.props.options, answer: this.props.answer, autoAlert: this.props.autoAlert == true, isReq: this.props.isReq == true, type: this.props.type, title: this.props.title, seqIdx: this.props.seqIdx, code: this.props.code};
    },
	val() {
		// return (this.state.value != null ? this.state.value : (this.state.options == null ? null : this.state.options[0].value));
		return this.state.value;
	},
	text() {
		let code = this.val();
		for (var v in this.state.options)
			if (v.value == code)
				return v.show;
		return null;
	},
	verify() {
		return null;
	},
	change(code) {
		this.state.value = code; //setState是异步方法
		if (this.props.onChange)
			this.props.onChange(this, code);
		this.setState({value: code});
	},
	/* 是否正确 */
	isCorrect(){
		if(this.state.answer != null && this.state.answer != '')
			return this.val() == this.state.answer;
		return null;
	},
	render() {
		let btns;
		let pclas = this.state.type == 2 ? "line-" : "";
		if (this.state.options != null)
			btns = this.state.options.map(v => (<div key={v.value} className={this.state.value==v.value ? (this.state.autoAlert ? (this.state.value==this.state.answer ? pclas+"blockGreen" : pclas+"blockRed") : pclas+"blockSel") : pclas+"block"} onClick={this.change.bind(this, v.value)}>{v.show}</div>));
		return (<p className="">
					<div dangerouslySetInnerHTML={{__html: this.state.seqIdx + "、" + this.state.title}}></div>
					<div>{btns}</div>
				</p>);
	}
});

module.exports = QuestionBox;

