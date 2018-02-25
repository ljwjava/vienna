"use strict";

import React from 'react';
import ReactDOM from 'react-dom';
import List from '../common/component.list.jsx';

var env = {
    search: null,
    from: 0,
    number: 10,
}

var TypeTree = React.createClass({
    getInitialState() {
        return {tree: []};
    },
    componentDidMount() {
        common.req("btbx/product/type.json", {}, r => {
            this.setState({tree: r});
            r.map(v => {
                this.findChildren(v);
            });
        });
    },
    findChildren(tp) {
        if (tp.children != null) {
            tp.children = null;
            this.forceUpdate();
        } else common.req("btbx/product/type.json", {typeId: tp.id}, r => {
            tp.children = r;
            this.forceUpdate();
        });
    },
    build(tree) {
        return tree.map(tp => {
            var children = tp.children == null ? null : this.build(tp.children);
            var icon = <img src={tp.children == null ? "../images/folder.png" : "../images/folder-open.png"} style={{width:"24px", height:"24px"}} onClick={this.findChildren.bind(this, tp)}/>;
            return <div key={tp.id}>
                <div>{icon} <a onClick={this.props.parent.refs.list.refresh.bind(this.props.parent.refs.list, tp.id)}>{tp.name}</a></div>
                <div style={{paddingLeft: "12px"}}>{children}</div>
            </div>;
        })
    },
    render() {
        return <div>{ this.build(this.state.tree) }</div>;
    }
});

class ProductList extends List {
    open(id) {
        document.location.href = "product.web?productId=" + id;
    }
    refresh(typeId) {
        common.req("btbx/product/list.json", {typeId: typeId, search: env.search, from: env.from, number: env.number}, r => {
            this.setState({content:r});
        });
    }
    buildTableTitle() {
        return (
			<tr>
                <th><div>产品ID</div></th>
				<th><div>产品CODE</div></th>
                <th><div>产品名称</div></th>
				<th><div>所属公司</div></th>
                <th><div>状态</div></th>
				<th></th>
			</tr>
        );
    }
    buildTableLine(v) {
        if (v.extra == null) v.extra = {};
        return (
			<tr key={v.id}>
                <td>{v.id}</td>
                <td>{v.code}</td>
                <td>{v.name}</td>
                <td>{v.companyName}</td>
                <td>{v.status}</td>
				<td>
					<a onClick={this.open.bind(this, v.id)}>编辑</a>
				</td>
			</tr>
        );
    }
}

var Main = React.createClass({
    render() {
        return (
            <div className="form-row">
                <div className="col-sm-2">
                    <TypeTree parent={this}/>
                </div>
                <div className="col-sm-10">
                    <ProductList ref="list" env={env}/>
                </div>
            </div>
        );
    }
});

$(document).ready( function() {
    ReactDOM.render(<Main/>, document.getElementById("content"));
});