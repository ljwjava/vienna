"use strict";

import React from 'react';

var Pagination = React.createClass({
    getInitialState() {
        return {total:0,options:[[12,"12条/页"],[30,"30条/页"],[50,"50条/页"],[100,"100条/页"]]};
    },
    componentDidMount() {
        this.props.parent.refresh();
    },
    page(i) {
        let env = this.props.env;
        env.from = i * env.number;
        if (env.from < 0) {
            env.from = 0;
        }else if (env.from >= env.total) {
            env.from = env.from - env.number;
        }
        this.props.parent.refresh();
    },
    change() {
        let env = this.props.env;
        env.number = parseInt(this.refs.self.val());
        env.from = 0;
        this.props.parent.refresh();
    },
	blur() {
        let input = this.refs.inputPage.val();
        let totalPage = Math.floor(env.total / env.number);
        console.log("==totalPage==",totalPage);
        if(input <= 0){
        	env.from = 0;
		}else if(input >= totalPage){
            env.from = (input-1) * env.number;
        }else{
            env.from = env.number * input - env.number;
		}
        this.props.parent.refresh();
	},
    buildPageAppend() {
        let env = this.props.env;
        env.total = this.props.parent.state.content.total;
        let cur = Math.floor(env.from / env.number);
        let p1 = 9, p2 = 9;
        let page = [];
        page.push(<label key="total" style={{marginTop: "5px", marginRight: "20px"}}>共{env.total}条记录</label>);
        page.push(<button key="first" type="button" className="mr-1 btn btn-info" onClick={this.page.bind(this, 0)}><span className="glyphicon glyphicon-fast-backward"></span></button>);
        page.push(<button key="back" type="button" className={"mr-1 btn btn-info" + (cur > 0 ? "" : " disabled")} onClick={this.page.bind(this, cur - 1)}><span className="glyphicon glyphicon-chevron-left"></span></button>);
        for (var i = cur - p1; i <= cur + p2; i++) {
            if (i >= 0 && i < env.total / env.number)
                page.push(<button key={i} type="button" className={"mr-1 btn btn-" + (i == cur ? "info" : "outline-info")} onClick={this.page.bind(this, i)}>{i + 1}</button>);
        }
        page.push(<button key="next" type="button" className={"mr-1 btn btn-info" + (cur + 1 < env.total / env.number ? "" : " disabled")} onClick={this.page.bind(this, cur + 1)}><span className="glyphicon glyphicon-chevron-right"></span></button>);
        page.push(<button key="last" type="button" className="btn btn-info" onClick={this.page.bind(this, Math.floor(env.total / env.number))}><span className="glyphicon glyphicon-fast-forward"></span></button>);
        page.push(<Selecter key="select" ref="self" onChange={this.change} options={this.state.options} value={10} showAddit={false}/>);
        page.push(<div key="inputPag" style={{marginTop: "3px", marginLeft: "20px"}}>跳转至<Inputer ref="inputPage" className="input-page" valCode="page" valType="number" valReg="^\d$" valMistake="请输入数字" valReq="yes" value={cur + 1} onBlur={this.blur}/>页</div>);
        return (
            <div style={{textAlign:"center"}}>
                <div className="btn-group" role="group" style={{marginTop:"16px"}}>
                    {page}
                </div>
            </div>
        );
    },
    render() {
        return (
            <div>{ this.buildPageAppend() }</div>
        );
    }
});

module.exports = Pagination;

