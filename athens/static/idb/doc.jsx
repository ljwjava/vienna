"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Navi from '../common/widget.navi.jsx';

var Ground = React.createClass({
    left() {
        history.back();
    },
    render() {
       return (
           <div className="graph">
               <Navi title="投保结果" left={[this.left, "<返回"]}/>
               <iframe style={{width:"100%", height:"100%"}} src="../doc/gwlife/FuJiaKangJianRenShengZhongJiClause20161012.pdf"></iframe>
           </div>
       );
   }
});

$(document).ready( function() {
    ReactDOM.render(
        <Ground/>, document.getElementById("content")
    );
});
