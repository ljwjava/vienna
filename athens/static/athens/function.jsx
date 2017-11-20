"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Main = React.createClass({
    getInitialState() {
        return {plan: {}, show: null};
    },
    componentWillMount() {
        this.refresh();
    },
    refresh(r) {
        if (r != null)
            this.setState({plan: r});
        else common.req("proposal/plan/edit.json", {planId: this.props.planId}, r => {
            this.setState({plan: r});
        });
    },
    render() {
        return (
            <div>
                <br/>
                <select id="functions" className="form-control" onChange={this.refresh}></select>
                <br/>
                <textarea id="script" className="form-control" style={{height:"300"}}></textarea>
                <br/>
                <input type="button" className="btn btn-primary btn-lg" value="测试 >>>>" onClick="test.insure()"/>
                <input type="button" className="btn btn-primary btn-lg" value="重置 >>>>" onClick="test.reset()"/>
                <br/>
                <br/>
                <pre id="result"></pre>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(
        <Main env={env}/>, document.getElementById("content")
    );
});
