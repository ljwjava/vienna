"use strict";

import React from 'react';

var Selecter = React.createClass({
    getInitialState() {
        return {/*showAddit: true*/};
    },
	val() {
		return this.refs.self.value;
	},
	addit(){
        let code = this.val();
        for (var v in this.props.options)
            if (this.props.options[v].length > 2 && this.props.options[v][0] == code)
                return this.props.options[v][2];
        return null;
	},
	text() {
		let code = this.val();
		for (var v in this.props.options)
			if (this.props.options[v][0] == code)
				return this.props.options[v][1];
		return null;
	},
	verify() {
		return null;
	},
	select(code) {
		this.refs.self.value = code;
		onChange();
	},
	onChange() {
		if (this.props.onChange)
			this.props.onChange(this, this.val());
	},
	change(code) {
		this.refs.self.value = code;
	},
	render() {
		if (this.props.options == null || this.props.options.length == 0)
			return (<span ref="self"></span>);
		let showAddit = true;
		if (this.props.showAddit != null)
            showAddit = this.props.showAddit;
		let btns = this.props.options.map(v => (<option key={v[0]} value={v[0]}>{v[1]+((v.length > 2 && showAddit) ? ">"+v[2] : "")}</option>));
		return (<select ref="self" onChange={!this.props.readOnly && this.onChange} defaultValue={this.props.value} disabled={!!this.props.readOnly}>{btns}</select>);
	}
});

module.exports = Selecter;

