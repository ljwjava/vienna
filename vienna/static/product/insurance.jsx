"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';
import DateEditor from '../common/widget.date.jsx';
import Inputer from '../common/widget.inputer.jsx';
import MultiSwt from '../common/widget.multiswt.jsx';
import Form from '../common/component.form.jsx';

var env = {};

class BaseForm extends Form {
    form() {
        if (this.props.fields == null)
            return [];
        return this.buildForm(this.props.fields.map(v => {return {name:v.label, code:v.name, type:v.widget, refresh:"yes", options:v.detail, long:v.length, value:v.value}}));
    }
}

var RuleForm = React.createClass({
    render() {
        var rules = this.props.value;
        var form = rules.map(v => {
            return  <div style={{border:"2px solid #00AAFF", height:"90px", padding:"3px 0 3px 0", marginBottom:"5px"}}>
                <div className="col-sm-10 field-comp">
                    <Inputer ref="rule" value={v.rule}/>
                </div>
                <div className="col-sm-2 field-comp">
                    <Selecter ref="type" options={[["default", "默认"],["product", "产品"],["customer", "客户"]]} value={v.type}/>
                </div>
                {/*<div className="col-sm-1" style={{textAlign:"right"}}>*/}
                    {/*<img src="../images/delete.png" style={{width:"30px", height:"30px"}}/>*/}
                {/*</div>*/}
                <div className="col-sm-10 field-comp">
                    <Inputer ref="text" value={v.text}/>
                </div>
                <div className="col-sm-2 field-comp">
                    <Selecter ref="level" options={[["default", "默认"],["fail", "失败"],["alert", "警告"]]} value={v.level}/>
                </div>
            </div>;
        });
        return <div>{form}</div>;
    }
});

var Main = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
    },
    render() {
        var keys = ["base", "data", "param", "rider", "duty", "rule"];
        var form = keys.map(v => {
            var f = env.product[v];
            return f == null ? null : <div className="container-fluid">
                <div className="form-label"><span style={{color:"#00AAFF"}}>█</span>&nbsp;&nbsp;{f.name}</div>
                {f.type == "rule" ? <RuleForm value={f.value}/> : <BaseForm fields={f.form}/>}
            </div>;
        });
        return (
            <div>{form}</div>
        );
    }
});

$(document).ready( function() {
    common.req("product/life/edit.json", {productId:common.param("productId")}, function (r) {
        env.product = r;
        ReactDOM.render(
            <Main/>, document.getElementById("content")
        );
    });
});