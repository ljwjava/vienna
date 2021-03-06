"use strict";

import React from 'react';

var Summary = React.createClass({
	open(url) {
		document.location.href = url;
	},
	render() {
		let summary = this.props.content.map(item => {
			let content = null;
			if (item.type == "image") {
				content = <img src={item.content} style={{width:"100%"}}/>;
			} else if (item.type == "docs") {
				let docs = [];
                if (item.content) for (let d in item.content) {
                	let r = item.content[d];
                    if (docs.length > 0)
                        docs.push("、");
                    docs.push(<a style={{color:"#2BF"}} key={r.name} onClick={this.open.bind(this,r.url)}>{r.name}</a>);
                }
				content = (<div style={{lineHeight:"30px", padding:"10px 5px"}}>查看 {docs}</div>);
            } else if (item.type == "list") {
                let docs = item.content == null ? null : item.content.map(r => {
                    return <div><a style={{color:"#2BF"}} key={r.name} onClick={this.open.bind(this,r.url)}>{r.name}</a></div>;
                });
                content = (<div style={{lineHeight:"30px", padding:"10px 5px"}}>{docs}</div>);
			} else if (item.type == "table" && this.props.vals != null) {
				let v = this.props.vals[item.content];
				let tables = v.map((t2,k) => {
					let head = t2.head.map((t3,j) => {
						let row = t3.map((t4,i) => {
							if (t4 == null) return null;
							return (<th colSpan={t4.col} rowSpan={t4.row} key={i++}>{t4.text}</th>);
						});
						return (<tr key={j++}>{row}</tr>);
					});
					let body = t2.body.map((t3,j) => {
						let i=0;
						let row = t3.map((t4,i) => {
							return (<td key={i++}>{t4}</td>);
						});
						return (<tr key={j++} style={{textAlign:"right"}}>{row}</tr>);
					});
					return (<table className="bordered" width={'100%'} key={k}>
						<thead>{head}</thead>
						<tbody>{body}</tbody>
					</table>)
				});
				content = <div className="html">{tables}</div>;
			} else if (item.type == "html") {
				content = <div className="html" dangerouslySetInnerHTML={{__html:item.content}}/>;
			} else if (item.type == "dhtml") {
                let str = "";
				if (typeof item.content == "string") {
					str = item.content;
                    if (this.props.vals) {
                    	for (var i = 0; i < item.vals.length; i++) {
                            str = str.replace(new RegExp("[$]" + (i + 1) + "[$]", 'gm'), this.props.vals[item.vals[i]]);
                        }
                        for (var v in this.props.vals) {
                            str = str.replace(new RegExp("[$]" + v + "[$]", 'gm'), this.props.vals[v]);
                        }
                    }
                } else if (typeof item.content == "object") {
                    item.content.map(v => {
                    	if (typeof v == "string")
	                        str = str + v;
                    	else if (this.props.vals && this.props.vals[v.name])
                    		str = str + v.html;
                    });
                    if (this.props.vals) for (var v in this.props.vals) {
                        str = str.replace(new RegExp("[$]" + v + "[$]", 'gm'), this.props.vals[v]);
                    }
                }
				content = <div className="html" dangerouslySetInnerHTML={{__html:str}}/>;
			}
			return (
				<div className="summary-block" key={item.title}>
					{ item.title == null || item.title == "" ? null : (<div className="summary-title">{item.title}</div>) }
					<div>{content}</div>
				</div>
			);
		});
		return <div className="summary">{summary}</div>;
	}
});

module.exports = Summary;

