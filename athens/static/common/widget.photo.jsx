"use strict";

import React from 'react';

var Photo = React.createClass({
	getInitialState() {
		let vlist = {}; // 存放曾经保存的图片，并展示，留底以免取值时再次被压缩
		let list = [];
		let now = 1;
		let v = this.props.value;
		if(v != null && v.length > 0){
			for(var i in v){
				list.push(now++);
				vlist[i] = v[i];
			}
		}
		return {list:list, now:now, vlist: vlist};
    },
    componentDidMount(){
        let v = this.props.value;
        if(v != null && v.length > 0){
            for(var i in v){
                compress(parseInt(i)+1, v[i]);
            }
        }
	},
	onChange(v) {
		var reader = new FileReader();
		let onChge = this.props.onChange;
		reader.onload = function(e) {
			compress(v, this.result, onChge);
		};
		reader.readAsDataURL(document.getElementById('photoCom' + v).files[0]);

		this.state.list.push(v);
		this.setState({now:this.state.now+1});
	},
	remove(v) {
		for (var i=0;i<this.state.list.length;i++) {
			if (this.state.list[i] == v) {
				this.state.list.splice(i, 1);
			}
		}
		this.setState({list:this.state.list});
        if (this.props.onChange){
            this.props.onChange();
        }
	},
	val() {
		return this.state.list.map(v => {
			// 若当前是一开始初始化进来就有了的，则直接取出返回，防止无限制压缩图片，导致不清晰的情况
			if(this.state.vlist != null && this.state.vlist[v-1] != null){
				return this.state.vlist[v-1];
			}
			return document.getElementById("photoImg" + v).toDataURL( 'image/jpeg', 0.6);
		});
	},
	render() {
		let photos = this.state.list.map(v => {
			return (
				<div key={v}>
					<div onClick={this.remove.bind(this, v)}/>
					<canvas id={"photoImg" + v}></canvas>
				</div>
			);
		});
		return (
			<div className="photo">
				{photos}
				<div>
					<input type="file" accept= "image/*" capture= "camera" id={"photoCom" + this.state.now} onChange={this.onChange.bind(this, this.state.now)} />
					<canvas id={"photoImg" + this.state.now}></canvas>
				</div>
			</div>
		);
	}
});

function compress(v, res, callback) { //res代表上传的图片，fileSize大小图片的大小
	var img = new Image(),
		maxW = 640; //设置最大宽度

	img.onload = function () {
		var cvs = document.getElementById( 'photoImg' + v),
			ctx = cvs.getContext( '2d');

		if(img.width > maxW) {
			img.height *= maxW / img.width;
			img.width = maxW;
		}

		cvs.width = img.width;
		cvs.height = img.height;

		ctx.clearRect(0, 0, cvs.width, cvs.height);
		ctx.drawImage(img, 0, 0, img.width, img.height);
		if(callback){
            callback();
		}
	};

	img.src = res;
}

module.exports = Photo;

