"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var CheckboxItem = React.createClass({
    getInitialState() {
        return {
            checked: this.props.checked,
            name: this.props.name
        };
    },
    componentWillReceiveProps(nextProps,prevProps){
        if(nextProps.checked != prevProps.checked){
            this.setState({
                checked: nextProps.checked
            })
        }
    },

    handleChange(){
        const { checked,name } = this.state
        this.setState({
            checked: !checked
        },function(){
            this.props.handleItemChange({ name: name, checked: !checked });
        })
    },

    render(){
        const {checked,name} = this.state;
        let tableLines = this.props.buildTableLine;
        return(
            <tr key={this.props.seq}>
                {
                    tableLines && tableLines.map((item,index)=>{
                        let hasCheckbox = item.key === "checkbox"?true:false;
                        if(hasCheckbox) {
                            return (
                                <td key={index}>
                                    <label key={`lb${index}`} className="checkbox-item"><input key={`cb${index}`} type="checkbox" checked={checked}
                                                                            onChange={this.handleChange}/></label>
                                </td>);
                        }else if("operate"===item.key){
                            return (
                                <td key={index} style={{padding:"6px"}}>
                                {
                                    item.value && item.value.map((val,i)=>{
                                    return (<button key={`btn${i}`} className="btn btn-outline-success mr-1" data-toggle="modal" data-target="#editor">{val.value}</button>                                            );
                                    })
                                }
                                </td>
                                );

                        }else{
                            return  (<td key={index}>{item.value}</td>);
                        }
                    })
                }
            </tr>
        )
    }
});

var CheckboxList = React.createClass({
    getInitialState() {
        return {
            data: [],
            checkedAll: false, //全选状态
            checkedInvert: false //反选状态
        };
    },
    componentWillUnmount(){

    },
    // 全选事件
    handleAllChange(){
        const { checkedAll,data } = this.state;

        data.map((item,index)=>{
            return item.checked = !checkedAll;
        })

        this.setState({
            checkedAll: !checkedAll,
            checkedInvert: false,
            data: data
        });
    },

    // 反选事件
    handleInvertChange(){
        const { checkedInvert, data } = this.state;

        data.map((item,index)=>{
            return item.checked = !item.checked;
        })

        const checkedAll = data.every(function(item,index){
            return item.checked;
        })

        this.setState({
            checkedAll: checkedAll,
            checkedInvert: !checkedInvert,
            data: data
        });
    },

    // CheckItem事件
    handleItemChange(ckitem){
        const data = this.state.data;
        const checked = ckitem.checked;
        let checkedInvert = this.state.checkedInvert;

        data.map((item,index)=>{
            if(item.name === ckitem.name){
                return item.checked = ckitem.checked
            }
        })

        const checkedAll = data.every((item,index)=>{
            return item.checked;
        })

        checkedInvert = checkedAll ? false : checkedInvert;

        this.setState({
            data: data,
            checkedAll: checkedAll,
            checkedInvert: checkedInvert
        })
    },
    render(){
        const {checkedAll,checkedInvert} = this.state;
        let that = this;
        let tableTitles = this.props.tableTitle();
        this.state.data = this.props.parent.state.content.list;
        return(
            <div>
                <table className="table table-bordered">
                        <thead className="thead-light">
                            <tr >
                            {
                                tableTitles && tableTitles.map((item,index)=>{
                                    let hasCheckbox = item.key === "checkbox"?true:false;
                                    if(hasCheckbox){
                                        return  (
                                            <th key={index}>
                                                <label key={`lball${index}`}><input key={`cball${index}`} type="checkbox" checked={checkedAll} onChange={ this.handleAllChange }/>全选</label>
                                                <label key={`lbnoall${index}`}><input key={`cbnoall${index}`} type="checkbox" checked={checkedInvert} onChange={ this.handleInvertChange }/>反选</label>
                                            </th>);
                                    }else{
                                        return  (
                                            <th key={index}>{item.name}</th>);
                                    }
                                })
                            }
                            </tr>
                        </thead>
                        <tbody className="tbody-light">
                            {
                                this.state.data && this.state.data.map((item,index)=>{
                                    return (<CheckboxItem {...item} key={index} seq={index} buildTableLine={ this.props.tableLine(item) } handleItemChange={that.handleItemChange}/>);
                                })
                            }
                        </tbody>

                </table>
                <div style={{textAlign:"center"}}>
                    <Pagination total={ this.props.parent.state.content.total } env={this.props.env} parent={this.props.parent}/>
                </div>
            </div>
        )
    }
});

module.exports = CheckboxList;