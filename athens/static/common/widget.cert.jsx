"use strict";

import React from 'react';
import Selecter from './widget.selecter.jsx';
import Switcher from './widget.switcher.jsx';
import DateEditor from './widget.date.jsx';
import Inputer from './widget.inputer.jsx';

var CertEditor = React.createClass({
	reg: new RegExp("/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/"),
	getInitialState() {
		return {long:true};
    },
	val() {
		return {
			certNo: this.refs.certNo.val(),
			certType: this.refs.certType.val(),
			certExpire: this.refs.certExpire.val(),
			certLong: this.state.long
		};
	},
	verify() {
		let alert = null;
		let ctp = this.refs.certType.val();
		let val = this.refs.certNo.val();
		if (val == null || val == "") {
			if (this.props.valReq == "yes")
				alert = "此项为必填项";
			else
				alert = null;
		} else if (ctp == "0") {
			alert = IdentityCodeValid(val);
		} else if (ctp == "4") {
			if (val.length == 18) {
				alert = IdentityCodeValid(val);
			} else if (val.length == 8) {
				alert = null;
			} else {
				alert = "户口本需要位8位或者18位";
			}
		}
		let expire = this.refs.certExpire.val();
		if (expire != null && expire != "") {
			let y2 = Number(expire.substr(0, 4));
			let m2 = Number(expire.substr(5, 2));
			let d2 = Number(expire.substr(8, 2));
			let d = new Date();
			let y1 = d.getFullYear();
			let m1 = d.getMonth() + 1;
			let d1 = d.getDate();
			let pass = false;
			if (y2 > y1)
				pass = true;
			else if (y2 == y1 && m2 > m1)
				pass = true;
			else if (y2 == y1 && m2 == m1 && d2 > d1)
				pass = true;
			if (!pass)
				alert = "证件有效期需要在当前时间之后";
		}
		return alert;
	},
	onChange() {
		if (this.props.onChange)
			this.props.onChange(this); 
	},
	setExpire() {
		let val = this.refs.certExpire.val();
		this.setState({long:val == null || val == ""});
	},
	setLong() {
		this.refs.certExpire.setTime(null);
		this.setState({long:true});
	},
	render() {
		let val = this.props.value == null ? {} : this.props.value;
		if (val.long) this.state.long = val.long;
		return (
			<div>
				<Selecter ref="certType" options={[["0","身份证"],["1","护照"],["2","军官证"],["3","异常身份证"],["4","户口本"],["6","警官证"],["7","出生证"],["8","其他"],["9","港澳台回乡证"]]} onChange={this.onChange} value={val.certType}/>
				<Inputer ref="certNo" type="text" placeholder="请输入证件号码" onChange={this.onChange} value={val.certNo}/>
				<DateEditor ref="certExpire" valReq="yes" defaultValue="" onChange={this.setExpire} value={val.certExpire}/>
				<span className={this.state.long?"blockSel":"block"} onClick={this.setLong}>长期</span>
			</div>
		);
	}
});

function IdentityCodeValid(code) { 
            var city={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北 ",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外 "};
            var tip = null;
            
            if(!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)){
                tip = "证件号码格式错误";
            }
            
           else if(!city[code.substr(0,2)]){
            	console.log("证件号码地址编码错误");
                // tip = "证件号码地址编码错误";
                tip = "请输入有效证件号码";
            }
            else{
                if(code.length == 18){
                    code = code.split('');
                    var factor = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ];
                    var parity = [ 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 ];
                    var sum = 0;
                    var ai = 0;
                    var wi = 0;
                    for (var i = 0; i < 17; i++)
                    {
                        ai = code[i];
                        wi = factor[i];
                        sum += ai * wi;
                    }
                    var last = parity[sum % 11];
                    if(parity[sum % 11] != code[17]){
                    	console.log("证件号码校验位错误");
                        // tip = "证件号码校验位错误";
                        tip = "请输入有效证件号码";
                    }
                } else {
					tip = "证件号码需要为18位";
				}
            }
            return tip;
        }

module.exports = CertEditor;

