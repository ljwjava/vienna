"use strict";

import React from 'react';

var DateEditor = React.createClass({
	getInitialState() {
		return {alert: null, text: null};
    },
    val() {
		return this.refs.self.value;
	},
	verify() {
		let alert = null;
		let val = this.val();
		if (val == null || val == "") {
			if (this.props.valReq == "yes")
				alert = "此项为必填项";
		} else {
			var opt = this.props.options;
			if (opt != null) {
				var p0 = new Date(Number(val.substr(0,4)),Number(val.substr(5,2))-1,Number(val.substr(8,2)));
				var bgn = opt[0];
                var end = opt[1];
                if (typeof bgn == 'number') {
                    bgn = [0, 0, bgn];
                }
                if (typeof end == 'number') {
                    end = [0, 0, end];
                }
                var now = new Date();
                var p1 = bgn == null ? null : new Date(now.getFullYear()+bgn[0], now.getMonth()+bgn[1], now.getDate()+bgn[2]);
                var p2 = end == null ? null : new Date(now.getFullYear()+end[0], now.getMonth()+end[1], now.getDate()+end[2]);
                if ((p1 != null && p1 > p0) || (p2 != null && p2 < p0)) {
                    alert = "范围限定："+p1.format('yyyy-MM-dd')+" ~ "+p2.format('yyyy-MM-dd');
				}
			}
		}
		return alert;
	},
	setTime(time) {
		this.refs.self.value = time;
		this.setState({text:time});
	},
	onChange() {
		if (this.props.onChange)
			this.props.onChange(this);
        this.setState({text:this.refs.self.value});
	},
	change(val) {
        this.setTime(val);
	},
	render() {
		let val = this.props.value;
		if (val == null || val == "" || val == "now" || val == "today") {
			val = new Date().format("yyyy-MM-dd");
        } else if (val == "tomorrow") {
			var d = new Date();
			d.setDate(d.getDate() + 1);
            val = d.format("yyyy-MM-dd");
		}
		return (<input ref="self" type="date" placeholder={this.props.placeholder} onChange={this.onChange} onBlur={this.onChange} defaultValue={val} disabled={!!this.props.readOnly}/>);
	}
});

module.exports = DateEditor;

