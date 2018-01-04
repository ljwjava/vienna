"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import Tabs from '../common/widget.tabs.jsx';
import Form from '../common/widget.form2.jsx';
import Switcher from '../common/widget.switcher.jsx';
import Selecter from '../common/widget.selecter.jsx';

var Ware = React.createClass({
    getInitialState() {
        let r = {quest:false, alertQuest:false, vendor:{}};
        let v = this.props.detail;
        if (v != null && v.detail != null && v.detail.length > 1) {
            r.packs = [];
            for (var i=0;i<v.detail.length;i++) {
                r.packs.push([i, v.detail[i].name]);
            }
        }
        return r;
    },
    componentDidMount() {
    },
    refresh() {
    },
    render() {
        var wares = this.state.ware.detail.map(v => {
            var pack = this.state.packs[v.target];
            var packDiv = <div className="form-horizontal">
                <div className="form-group">
                    <div className="col-sm-1">CODE</div>
                    <div className="col-sm-2">
                    </div>
                    <div className="col-sm-1">名称</div>
                    <div className="col-sm-2">
                    </div>
                    <div className="col-sm-1">简称</div>
                    <div className="col-sm-2">
                    </div>
                    <div className="col-sm-1">保险公司</div>
                    <div className="col-sm-2">
                    </div>
                </div>
                <div className="form-group">
                    <div className="col-sm-1">类型</div>
                    <div className="col-sm-2">
                    </div>
                    <div className="col-sm-1">展示价格</div>
                    <div className="col-sm-2">
                    </div>
                    <div className="col-sm-1">介绍</div>
                    <div className="col-sm-5">
                    </div>
                </div>
                <div className="form-group">
                    <div className="col-sm-1">banner</div>
                    <div className="col-sm-5">
                    </div>
                    <div className="col-sm-1">logo</div>
                    <div className="col-sm-5">
                    </div>
                </div>
            </div>;
            return <div>{v.name}<div>{}</div></div>;
        });
        return (
            <div>
                <div>商品信息</div>
                <div className="form-horizontal">
                    <div className="form-group">
                        <div className="col-sm-1">CODE</div>
                        <div className="col-sm-2">
                        </div>
                        <div className="col-sm-1">名称</div>
                        <div className="col-sm-2">
                        </div>
                        <div className="col-sm-1">简称</div>
                        <div className="col-sm-2">
                        </div>
                        <div className="col-sm-1">保险公司</div>
                        <div className="col-sm-2">
                        </div>
                    </div>
                    <div className="form-group">
                        <div className="col-sm-1">类型</div>
                        <div className="col-sm-2">
                        </div>
                        <div className="col-sm-1">展示价格</div>
                        <div className="col-sm-2">
                        </div>
                        <div className="col-sm-1">介绍</div>
                        <div className="col-sm-5">
                        </div>
                    </div>
                    <div className="form-group">
                        <div className="col-sm-1">banner</div>
                        <div className="col-sm-5">
                        </div>
                        <div className="col-sm-1">logo</div>
                        <div className="col-sm-5">
                        </div>
                    </div>
                </div>
                <div>产品组合</div>
            </div>
        );
    }
});

$(document).ready( function() {
    common.req("sale/view.json", {wareId:common.param("wareId")}, function (r) {
        env.ware = r;
        ReactDOM.render(
            <Ware detail={r}/>, document.getElementById("content")
        );
    });
});