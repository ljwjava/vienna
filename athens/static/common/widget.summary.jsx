"use strict";

import React from 'react';

var Summary = React.createClass({
	render() {
		let summary = this.props.content.map(item => {
			let content = null;
			if (item.type == "image") {
				content = <img src={item.content} style={{width:"100%"}}/>;
			} else if (item.type == "docs") {
				content = (
					<div style={{height:"40px", lineHeight:"40px", padding:"0 5px 0 5px"}}>
						查看 { item.content.map(r => {
							return (<span>{r.name}</span>);
						})}
					</div>
				);
			} else if (item.type == "html") {
				content = <div dangerouslySetInnerHTML={{__html:item.content}}/>;
			}
			return (
				<div className="summary-block">
					{ item.title == null || item.title == "" ? null : (<div className="summary-title">{item.title}</div>) }
					<div>{content}</div>
				</div>
			);
		});
		return <div className="summary">{summary}</div>;
	}
});

module.exports = Summary;

