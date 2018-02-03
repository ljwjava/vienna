"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Main = React.createClass({
    submit() {
        var w2 = this.refs.pwd2.value;
        var w3 = this.refs.pwd3.value;
        if (w2 != w3) {
            alert("两次输入的新密码不相同");
        } else common.req("user/password.json", {oldPwd: this.refs.pwd1.value, newPwd: this.refs.pwd2.value}, r => {
            alert(r);
        });
    },
    render() {
        return <div style={{textAlign:"center"}}>
            <div style={{textAlign:"left", margin:"20px", padding:"20px", fontSize:"14pt", border:"2px solid #000"}}>
                <div>原密码： <input type="password" ref="pwd1" style={{color:"#000"}}/></div>
                <br/>
                <div>新密码： <input type="password" ref="pwd2" style={{color:"#000"}}/></div>
                <br/>
                <div>新密码： <input type="password" ref="pwd3" style={{color:"#000"}}/>（校验）</div>
            </div>
            <button className="btn-primary" style={{width:"200px", height:"40px", fontSize:"12pt"}} onClick={this.submit}>提交</button>
        </div>
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});