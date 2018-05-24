"use strict";

import React from 'react';
import {Pagination} from "../common/component.page.jsx";
import {City} from "../common/widget.city";
import Selecter from '../common/widget.selecter.jsx';
import Inputer from '../common/widget.inputer.jsx';
// import {  Icon, Button, Table, Pagination } from 'dragon-ui'
// import { fetchAPI } from 'utils/fetch'
// import classnames from 'classnames';

var env = {
    from: 0,
    number: 12,
};

var ShopList = React.createClass({
    getInitialState() {
        return {
            productList: [],
            btn: false,
            btns: {
                visible: false,
                text: '展开更多',
                up: false
            },
            index: 0,
            tagDatas: [],
            prdDetails: {
                list: [],
                page: [],
            },
            currentPage: 0,
            // pageSize: 0,
            activeTab: 0,
            // start: 1,
            // limit: 4,
            // total: 30,
            tagId: 0,
            content: { total: 30 }
        }
    },
    
    componentDidMount(){

    },
    componentWillUnmount(){
       
    },
    refresh(){
        let resultJson = {
            "code": "0",
            "result": {
            "page": {
                "start": 1,
                    "limit": 4,
                    "totalItem": 30
            },
            "tagDatas": [{
                "tagName": "全部",
                "prdNumbers": 9
            }, {
                "tagName": "旅行险",
                "prdNumbers": 2,
                "tagId": 1
            }, {
                "tagName": "拓展险",
                "prdNumbers": 1,
                "tagId": 2
            }, {
                "tagName": "户外险",
                "prdNumbers": 1,
                "tagId": 3
            }, {
                "tagName": "团意险",
                "prdNumbers": 4,
                "tagId": 4
            }, {
                "tagName": "雇主责任险",
                "prdNumbers": 1,
                "tagId": 5
            }, {
                "tagName": "保高空",
                "prdNumbers": 2,
                "tagId": 6
            }],
                "prdDetails": [{
                "id": 14,
                "isDeleted": "N",
                "modifier": "system",
                "creator": "system",
                "gmtCreated": "2018-01-12 19:23:09",
                "gmtModified": "2018-01-12 19:23:18",
                "productId": 14,
                "sortIndex": 1007,
                "type": "0",
                "detailJson": {
                    "imgUrl": "https://cdn.iyb.tm/app/config/img/14757.png",
                    "prdName": "众安团险意外险（推广费固定）",
                    "price": "51.00",
                    "rules": [{
                        "ruleValue": "16-65周岁",
                        "ruleName": "投保年龄"
                    }, {
                        "ruleValue": "1-6类",
                        "ruleName": "承包企业"
                    }, {
                        "ruleValue": "5人起",
                        "ruleName": "起保人数"
                    }],
                    "factors": [{
                        "factorName": "意外身故、残疾",
                        "factorValue": "10-100万"
                    }, {
                        "factorName": "意外医疗",
                        "factorValue": "1-10万(无免赔，100%赔付)"
                    }, {
                        "factorName": "意外住院津贴",
                        "factorValue": "50-150元/天"
                    }],
                    "promotion": "50%+10%（活动）推广费"
                },
                "dictKey": "DG000000001"
            }, {
                "id": 3,
                "isDeleted": "N",
                "modifier": "system",
                "creator": "system",
                "gmtCreated": "2018-01-12 19:23:09",
                "gmtModified": "2018-01-12 19:23:18",
                "productId": 3,
                "sortIndex": 1006,
                "type": "0",
                "detailJson": {
                    "imgUrl": "https://cdn.iyb.tm/app/config/img/14756.png",
                    "factorsSupply": [{
                        "factorName": "扩展24小时",
                        "factorValue": ""
                    }, {
                        "factorName": "提升伤残赔付比例",
                        "factorValue": ""
                    }],
                    "prdName": "雇主责任险",
                    "price": "42.00",
                    "rules": [{
                        "ruleValue": "16-65周岁",
                        "ruleName": "投保年龄"
                    }, {
                        "ruleValue": "1-6类",
                        "ruleName": "承包企业"
                    }, {
                        "ruleValue": "5人起",
                        "ruleName": "起保人数"
                    }],
                    "factors": [{
                        "factorName": "意外身故、残疾",
                        "factorValue": "10-100万"
                    }, {
                        "factorName": "意外医疗",
                        "factorValue": "1-10万(无免赔，100%赔付)"
                    }, {
                        "factorName": "意外医疗扩展自费药",
                        "factorValue": "1-10万(无免赔，100%赔付)"
                    }, {
                        "factorName": "误工费用",
                        "factorValue": "50-150元/天"
                    }, {
                        "factorName": "意外住院津贴",
                        "factorValue": "60-180元/天"
                    }],
                    "promotion": "10% 15% 25% 30%+6%（活动）推广费"
                },
                "dictKey": "GZ000000001"
            }, {
                "id": 25,
                "isDeleted": "N",
                "modifier": "system",
                "creator": "system",
                "gmtCreated": "2018-01-12 19:23:06",
                "gmtModified": "2018-01-12 19:23:16",
                "productId": 25,
                "sortIndex": 996,
                "type": "0",
                "detailJson": {
                    "imgUrl": "https://cdn.iyb.tm/app/config/img/14375.png",
                    "thumbnailUrl ": "https://cdn.iyb.tm/app/config/img/210_140/14375.png",
                    "prdName": "史带境内户外运动保险",
                    "price": "5.00",
                    "rules": [{
                        "ruleValue": "1-65周岁",
                        "ruleName": "投保年龄"
                    }, {
                        "ruleValue": "1份",
                        "ruleName": "购买分数"
                    }],
                    "factors": [{
                        "factorName": "意外身故、残疾",
                        "factorValue": "10-30万"
                    }, {
                        "factorName": "意外医疗",
                        "factorValue": "1-3万（无免赔，80%赔付）"
                    }, {
                        "factorName": "紧急医疗运送及送返",
                        "factorValue": "2-10万"
                    }],
                    "promotion": "25%推广费"
                },
                "dictKey": "SD000000003"
            }]
        }
        }

        if(resultJson.code === "0" ) {
            this.setState({
                productList: resultJson.result.tagDatas.concat(),
                tagDatas   : resultJson.result.tagDatas || [],
                prdDetails : resultJson.result.prdDetails || [],
                start      : resultJson.result.page.start,
                content: { total : resultJson.result.page.totalItem }
            },()=>{
                this.getElementShowMore();
            })
        }
        
    },
    getElementShowMore(){
        
        let ui_tab_header_width = document.querySelector('.ui-tab-head').clientWidth;
        let ui_tab_item = document.querySelectorAll('.ui-tab-head-item'),
            width = 0,
            step = 1;//第几行
        
        for( var i=0; i < ui_tab_item.length; i++ ){

            width += ui_tab_item[i].getBoundingClientRect().width + 15;

            if( width > ui_tab_header_width && step === 2){
                let { productList } = this.state;
                let new_list = [];

                productList.map((item,index)=>{
                    if(index < i){
                        new_list.push(item);
                    }
                })
                let btns = {
                    visible : true,
                    text    : '展开更多',
                    up      : false
                }

                this.setState({
                    productList :   new_list,
                    index       :   i,
                    btns,
                })
               
                break;
            }else if(step === 1 && width > ui_tab_header_width){

                width = ui_tab_item[i].getBoundingClientRect().width + 15;
                step = step + 1;
                
            }
        }
        
    },
    // appendElement(){
        
    //     let div = document.createElement('div');
    //         div.className='list-more';
    //         div.id = 'J_btn';
    //         div.innerText = '展开更多';
    //         document.querySelector('.ui-tab').appendChild(div);
    //     this.bindEvent();

    // }

    bindEvent(){

        const { btns : { visible , text, up}, tagDatas, productList } = this.state;
        let temp_btn = {
            visible : true,
            text    : up ? '折叠收起' : '展开更多',
            up      : !up,
        }
        
        this.setState({
            productList : up ? tagDatas.slice(0,this.state.index) : tagDatas.concat() ,
            btns : temp_btn,
        })
    },

    getCurrentPageData(i){
       
        this.setState({
            start : 1,
            tagId : i,
        },()=>{
            let params = {
                start  : 1,
                limit  : this.state.limit,
                tagId  : i
            }
            this.getElementList(params);
        })
        
    },

    render() {
        const { productList, prdDetails ,limit, start, total, tagId, btns } = this.state;
        return (
            <div className="content">
                <div className="page-product-list">
                    <h1>产品中心</h1>
                    <div>
                        <div className="product-list-search">
                            <input type="textbox" placeholder="请输入产品名称"/><button type="button">搜索</button>
                        </div>
                        <div className="ui-tab">
                            <ul className="ui-tab-head">
                                {
                                    productList.length > 0 && productList.map((item,index)=>{
                                        return <li key={index} className={`ui-tab-head-item ${tagId == item.tagId ? 'active' : null}`} onClick={ ()=>{ this.getCurrentPageData(item.tagId,index)}}> { `${item.tagName} （${item.prdNumbers}）`}</li>
                                    })
                                }
                            </ul>
                            {
                               btns.visible && <div className={ btns.up ? 'list-close' : 'list-more'} ref='J_btn' onClick={()=>{this.bindEvent()}}>{ btns.up ? '折叠收起' : '展开更多'}</div>
                            }
                        </div>
                        <ul className="">
                        {
                            prdDetails.length > 0 && prdDetails.map((item,index)=>{
                                return this.getRenderList(item,index)
                            })
                        }
                        </ul>
                    </div>

                    {/*<div className="pagination-wrap">*/}
                        {/*<div className="pagination-interlayer">*/}
                            {/*<Pagination*/}
                                {/*style   = {{marginTop: 10}}*/}
                                {/*value   = { start }*/}
                                {/*pageSize= { limit }*/}
                                {/*total   = { total }*/}
                                {/*onPageChange={(value) => {*/}
                                    {/*this.changePage(value);*/}
                                {/*}} radius*/}
                            {/*/>*/}
                        {/*</div>*/}
                    {/*</div>*/}

                </div>
                <div style={{textAlign:"center"}}>
                    {/*<Pagination total={ total } from={ start } number={ limit } />*/}
                    <Pagination env={env} parent={this}/>
                </div>
            </div>
        );
    },
    getRenderList(val,i){
        let item = val.detailJson;
        if(item){
            return (<li key={i} className="list-item-wrap">
            {
                item.hotTag && <div className="icon" style={{background:'url(http://static.iyunbao.com/website/health/pc/assets/images/hot.png) no-repeat'}}></div>
            }
            <div className="list-item-left">
                <div className="item-img-wrap">
                    <img src={item.imgUrl} alt={item.prdName}/>
                </div>
                <div className="item-product">
                    <h3 className=""> { item.prdName }</h3>
                    <div className="product-rule">
                        {
                            item.rules && item.rules.length > 0 && item.rules.map((item,index)=>{
                                return  <p className="icon-wrap" key={index}><i className="icon-color"/><span>{item.ruleName}：{ item.ruleValue }</span></p>
                            })

                        }
                    </div>
                    <ul className="item-detail">
                        
                        {
                            item.factors && item.factors.length > 0 && item.factors.map((val,index)=>{
                                return <li key={index}> { `${val.factorName ? val.factorName+'：' : ''} ${val.factorValue}`}</li>
                            })
                        }
                        {
                            item.factorsSupply && item.factorsSupply.length > 0 && item.factorsSupply.map((val,index)=>{
                                return <li key={index}> { val.factorName } </li>
                            })
                        }
                    </ul>
                </div>
            </div>
            <div className="list-item-right">
                <div className="list-right-wrap">
                    <div className="price">
                        <span>{ `¥${ item.price }`}</span>
                        <span className="sup">/人起</span>
                    </div>
                    <div className="promote"> {item.promotion}</div>
                    <button type="button" className="ui-button radius theme-info size-sm bttom" onClick={ ()=>{this.toProductDetailPage(val.dictKey)} }>页面地址</button>

                </div>
            </div>
        </li>)
        }else {
            return ''
        }
        
    },
    changePage(value){
        
        let params = {
            start   : value,
            limit    : this.state.limit,
            tagId   : this.state.tagId || ''
        }
        this.getElementList(params);
    },
    toProductDetailPage(id){
        
        fetchAPI({
            server: 'mercury',
            path: `/v1/product/operate`,
            method: 'POST',
            data : {
                "productDictKey":id,
                "flag":"enquiry"
            }
          })
          .then(({ code, result }) => {
            if(+code === 0 ) {
               //this.context.router.push(result);
               window.location.href = result;
            }
            
        })
    }
});

// ShopList.contextTypes = {
//     router: React.PropTypes.object.isRequired
// }

$(document).ready( function() {
    ReactDOM.render(<ShopList/>, document.getElementById("content"));
});

