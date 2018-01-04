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

var Main = React.createClass({
    getInitialState() {
        return {};
    },
    componentDidMount() {
    },
    render() {
        var keys = ["base", "data", "param", "rider", "duty"];
        var form = keys.map(v => {
            var f = env.product[v];
            if (f == null) {
                return null;
            }
            return <div className="container-fluid">
                <div>{f.name}</div>
                <BaseForm fields={f.form}/>
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