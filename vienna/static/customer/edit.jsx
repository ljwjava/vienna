"use strict";

import React from 'react';
import ReactDOM from 'react-dom';

var CustomerForm = React.createClass({
	getInitialState() {
		return null;
	},
	componentDidMount() {
	},
	render() {
		let v = this.props.content;
		return (
			<form className="form-horizontal">
				<div className="col-sm-11">
					<div className="form-group has-success has-feedback">
						<div className="col-sm-4">
							<label className="control-label col-sm-3" for="inputSuccess3">姓名</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" id="inputSuccess3" aria-describedby="inputSuccess3Status"/>
								<span className="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
								<span id="inputSuccess3Status" className="sr-only">(success)</span>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3" for="inputSuccess3">证件</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" id="inputSuccess3" aria-describedby="inputSuccess3Status"/>
								<span className="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
								<span id="inputSuccess3Status" className="sr-only">(success)</span>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3" for="inputSuccess3">手机</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" id="inputSuccess3" aria-describedby="inputSuccess3Status"/>
								<span className="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
								<span id="inputSuccess3Status" className="sr-only">(success)</span>
							</div>
						</div>
					</div>
					<div className="form-group has-success has-feedback">
						<div className="col-sm-4">
							<label className="control-label col-sm-3" for="inputSuccess3">姓名</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" id="inputSuccess3" aria-describedby="inputSuccess3Status"/>
								<span className="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
								<span id="inputSuccess3Status" className="sr-only">(success)</span>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3" for="inputSuccess3">证件</label>
							<div className="col-sm-9">
								<input type="text" className="form-control" id="inputSuccess3" aria-describedby="inputSuccess3Status"/>
								<span className="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
								<span id="inputSuccess3Status" className="sr-only">(success)</span>
							</div>
						</div>
						<div className="col-sm-4">
							<label className="control-label col-sm-3" for="inputSuccess3">手机</label>
							<div className="col-sm-9">
								<form className="form-inline">
									<div className="form-group">
										<label className="sr-only" for="exampleInputAmount">Amount (in dollars)</label>
										<div className="input-group">
											<div className="input-group-addon">$</div>
											<input type="text" className="form-control" id="exampleInputAmount" placeholder="Amount"/>
											<div className="input-group-addon">.00</div>
										</div>
									</div>
									<button type="submit" className="btn btn-primary">Transfer cash</button>
								</form>
							</div>
						</div>
					</div>
				</div>
				<div className="col-sm-1">
				</div>
				<div className="col-sm-12">
					<div className="container-fluid">
						<div className="collapse navbar-collapse">
							<div className="nav navbar-nav navbar-center">
								<button type="button" className="btn btn-success" onClick={this.save}>&nbsp;&nbsp;保存&nbsp;&nbsp;</button>
								&nbsp;&nbsp;
								<button type="button" className="btn btn-default" onClick={this.cancel}>&nbsp;&nbsp;取消&nbsp;&nbsp;</button>
							</div>
						</div>
					</div>
				</div>
			</form>
		);
	}
});

var Main = React.createClass({
    cancel() {
        document.location.href = "list.web";
    },
	render() {
		return (
			<div>
				<CustomerForm content={this.props.content}/>
			</div>
		);
	}
});

function refresh(r) {
	ReactDOM.render(
		<Main content={r}/>, document.getElementById("content")
	);
}

$(document).ready( function() {
	let customerId = common.param("customerId");
	if (customerId == null || customerId == "") {
		refresh({});
	} else common.req("customer/view.json", {customerId:customerId}, function(r) {
		refresh(r);
	});
});