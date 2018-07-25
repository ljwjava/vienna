"use strict";

import React from 'react';
// import Selecter from './widget.selecter.jsx';

var CityPicker = React.createClass({
	// 实例创建时调用一次，用于初始化每个实例的state，此时可以访问this.props
	getInitialState() {
	    var v = this.props.value;
	    v = v == null ? {} : this.props.value;
	    if (typeof v == "string") v = {code: v};
		return {value: v.value, code: v.code, text: v.text, valType: this.props.valType, city:[], proPickerVal:[], readOnly: !!this.props.readOnly};
    },
    getCode(){
	    return this.state.code || this.state.value;
    },
	// 在完成首次渲染之前调用，此时仍可以修改组件的state
    componentWillMount() {
	},
	// 真实的DOM被渲染出来后调用，在该方法中可通过this.getDOMNode()访问到真实的DOM元素。此时已可以使用其他类库来操作这个DOM。
    componentDidMount() {
        // console.log('city3:', this.props, env.company);
	    let company = (!env.company ? "hqlife" : env.company);
	    if(!!this.props.company){
	        company = this.props.company;
        }
        common.req("dict/view.json", {company: company, name: this.state.valType, version: "new"}, r => {
            let val = this.state.value;
            var proPickerVal = [];
            if(val != null && val != '') {
                var cv1 = val.substr(0, 2) + "0000";
                var cv2 = val.substr(0, 4) + "00";
                for(var co1 in r[this.state.valType]) {
                    var v1 = r[this.state.valType][co1];
                    if(cv1 == v1.value) {
                        proPickerVal.push(v1);
                        var cc1 = v1.children;
                        for(var co2 in cc1) {
                            var v2 = cc1[co2];
                            if(cv2 == v2.value) {
                                proPickerVal.push(v2);
                                var cc2 = v2.children;
                                for(var co3 in cc2) {
                                    var v3 = cc2[co3];
                                    if (val == v3.value) {
                                        proPickerVal.push(v3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // console.log('compdid', this.state);
            this.setState({
                city: r[this.state.valType],
                proPickerVal: proPickerVal
            });
        });
    },
   	val() {
        // console.log('val', this.state.value);
		return {code: this.getCode(), value: this.state.value, text: this.getFullName(this.state.text, this.state.value)};
	},
	verify() {
		let alert = null;
		let val = this.val();
		if (val == null || val.code == null || val.code == "") {
			if (this.props.valReq == "yes")
				alert = "此项为必填项";
		}
		return alert;
	},
    updateProfession(obj, proPickerVal) {
        let v = {
            isCalculated: 'N',
            proPickerVal,
            proPickerV: false
        };
        obj.setState(v);
        var vv = proPickerVal[proPickerVal.length - 1];
        // this.state.value = {occCode: vv.value, occDesc: vv.label, occLevel: vv.level};
        // console.log('update', vv);
        this.setState({isCalculated: 'N', proPickerV: false, open:false, value: vv.value, code: vv.code, text: vv.label, proPickerVal: proPickerVal
        },() => this.props.onChange(this));
        return [];
    },
    getFullName(label, value) {
	    // console.log('full', label, value);
	    if(value == null || value.length < 6) return '';
	    if(this.state.city == null || this.state.city.length <= 0 || value == null || value == "") return label;
	    // console.log(label, value);
        var cv1 = value.substr(0, 2) + "0000";
        var cv2 = value.substr(0, 4) + "00";
        var cv3 = value;
        var desc1 = '';
        var desc2 = '';
        var desc3 = '';
	    for(var co1 in this.state.city) {
            var v1 = this.state.city[co1];
            // console.log("111", v1);
            if(cv1 == v1.value || cv2 == v1.value) {    // 处理跨级情况，如直接写XX市，不过一般不会有这种情况
                desc1 = v1.label;
                var cc1 = v1.children;
                for(var co2 in cc1) {
                    var v2 = cc1[co2];
                    if(cv2 == v2.value || cv3 == v2.value) {    // 处理跨级情况，如直接写XX区，“市辖区”和“县”直接忽略的那种，有这种情况存在，比如上海人寿
                        desc2 = v2.label == desc1 ? '' : v2.label;
                        var cc2 = v2.children;
                        for(var co3 in cc2) {
                            var v3 = cc2[co3];
                            if (cv3 == v3.value) {
                                desc3 = v3.label == desc2 ? '' : v3.label;
                            }
                        }
                    }
                }
            }
        }
        // console.log('000', desc1, desc2, desc3);
	    return desc1 + (desc2 == "县" || desc2 == "市辖区" ? "" : desc2) + desc3;
    },
    /**
	 * 必选的方法，创建虚拟DOM，该方法具有特殊的规则：
	 * 1.可以返回null、false或任何React组件
	 * 2.只能出现一个顶级组件（不能返回数组）
	 * 3.不能改变组件的状态
	 * 4.不能修改DOM的输出
     * @returns {XML}
     */
	render() {
        // console.log('city4:', this.props, this.state.company, env.company);
        return (<PickerStack dataSource={ this.state.city } // readOnly={ true }
							 valueType='label'
							 title='所在地区选择'
							 cols={ 3 }
							 value = {this.state.proPickerVal}
                             readOnly={this.state.readOnly}
							 placeholder="请选择所在地区"
                             showTpl={({label, value}) => this.getFullName(label, value)}
							 // labelTpl={ ({ label, value }) => this.getFullName(label, value) }
							 onOk={(obj, data) => {
                                 this.updateProfession(obj, data);
                             }}
							 validate={data => {
							     // console.log('city.validate', data);
                             }}/>);
	}
});

module.exports = CityPicker;

