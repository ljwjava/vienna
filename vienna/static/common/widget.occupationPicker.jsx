"use strict";

import React from 'react';
// import Selecter from './widget.selecter.jsx';

var OccupationPicker = React.createClass({
	// 实例创建时调用一次，用于初始化每个实例的state，此时可以访问this.props
	getInitialState() {
	    var v = this.props.value;
	    v = v == null ? {} : this.props.value;
		return {value: v.value||v.code, code: v.code, text: v.text, level: v.level, occupation:[], proPickerVal:[]};
    },
	// 在完成首次渲染之前调用，此时仍可以修改组件的state
    componentWillMount() {
	},
	// 真实的DOM被渲染出来后调用，在该方法中可通过this.getDOMNode()访问到真实的DOM元素。此时已可以使用其他类库来操作这个DOM。
    componentDidMount() {
        let company = (!env.company ? "hqlife" : env.company);
        if(!!this.props.company){
            company = this.props.company;
        }
        common.req("dict/view.json", {company: company, name: "occupation", version: "new"}, r => {
            let val = this.state.value;
            var proPickerVal = [];
            if(val != null && val != '') {
                proPickerVal = this.unzipPack(val, r.occupation, proPickerVal, 1);
                /*for(var ov1 in r.occupation) {
                    var oc1 = r.occupation[ov1];
                    if(val.startsWith(oc1.value)){
                        proPickerVal.push(oc1);
                        for(var ov2 in oc1.children) {
                            var oc2 = oc1.children[ov2];
                            if(val.startsWith(oc2.value) || val == "8888888") { // 横琴有个奇葩的‘其他职业’编码是8888888而其上级分类是800
                                proPickerVal.push(oc2);
                                for(var ov3 in oc2.children) {
                                    var oc3 = oc2.children[ov3];
                                    if(val == oc3.value) {
                                        proPickerVal.push(oc3);
                                    }
                                }
                            }
                        }
                    }
                }*/
            }
            this.setState({
                occupation: r.occupation,
                proPickerVal: proPickerVal
            });
        });
    },
    unzipPack(val, occ, propickval, idx){
        propickval = propickval || [];
        if(occ != null){
            for(var ov in occ) {
                var oc = occ[ov];
                if(val.startsWith(oc.value) || (idx == 2 && val == "8888888")) {
                    propickval.push(oc);
                    if(oc.children != null && oc.children != ""){
                        return this.unzipPack(val, oc.children, propickval, ++idx);
                    }else if(val == oc.value){
                        return propickval;
                    }
                }
            }
        }

        return propickval;
    },
   	val() {
		return {value: this.state.value, code: this.state.code, text: this.state.text, level: this.state.level};
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
    validateChange(data){

    },
    updateProfession(obj, value, proPickerVal) {
        let { occupation_level, premium, commission } = obj.state;
        occupation_level = value;
        let v = {
            isCalculated: 'N',
            occupation_level,
            proPickerVal,
            proPickerV: false
        };
        obj.setState(v);
        var vv = proPickerVal[proPickerVal.length - 1];
        // this.state.value = {occCode: vv.value, occDesc: vv.label, occLevel: vv.level};
        this.setState({
            value: vv.value, code: vv.code || vv.value, text: vv.label, level: vv.level, open:false, proPickerVal: proPickerVal
        },() => this.props.onChange(this));
        return [];
    },
    changeStateValue() {
        var v = this.state.value;
        v = v == null ? "" : this.state.value;
        if(this.state.reset && this.state.occupation != null && this.state.occupation.length > 0 && v != null && v != ''){
            this.setState({
                proPickerVal: this.unzipPack(v, this.state.occupation, [], 1),
                reset: false
            });
        }
    },
    // props改变
    // componentWillReceiveProps(){
    //     this.changeStateValue();
    // },
    // props和state任意一个改变
    componentWillUpdate(){
        this.changeStateValue();
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
        return (<PickerStack dataSource={ this.state.occupation } // readOnly={ true }
							 valueType='label'
							 title='职业类别选择'
							 cols={ 5 }
							 value = {this.state.proPickerVal}
							 placeholder="请选择职业类别"
							 labelTpl={ ({ label, level }) => level === undefined ? label : `${ label }(${ level }类)` }
							 onOk={(obj, data) => {
                                 let { level } = data[data.length - 1],
                                     value = '3';

                                 if (level == '4') {
                                     value = '4';
                                 }
                                 // 描述：data.label, 编码：data.value, 等级：level
                                 this.updateProfession(obj, value, data);
                             }}
							 validate={data => {
                                 // if (data.length === 3) {
                                 //     let { level } = data[2];
                                 //
                                 //     if (level < 1 || level > 4) {
                                 //         return `职业类别不符合投保要求，仅限1-4类职业类别`;
                                 //     }
                                 // }
                             }}/>);
	}
});

module.exports = OccupationPicker;

