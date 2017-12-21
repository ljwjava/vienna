"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var Main = React.createClass({
	render() {
		return (
			<div className="form-horizontal">
			</div>
		);
	}
});

$(document).ready( function() {
	ReactDOM.render(
		<Main/>, document.getElementById("content")
	);
});