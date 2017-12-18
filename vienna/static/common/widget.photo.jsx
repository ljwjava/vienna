"use strict";

import React from 'react';

var Photo = React.createClass({
	getInitialState() {
		let list = [];
		let now = 1;
		let v = this.props.value;
		if(v != null && v.length > 0){
			for(var i in v){
				list.push({
					idx: now++,
					v: v[i]
				});
			}
		}
		return {list:list, now:now};
    },
    componentDidMount(){
        let v = this.props.value;
        if(v != null && v.length > 0){
            for(var i in v){
                // compress(parseInt(i)+1, "http://localhost:7771/image/load/" + v[i]);
                common.reqSync("image/image/get/base64.json", {"pathName": v[i]}, r => {
                	compress(parseInt(i)+1, r);
                }, f => {
                    ToastIt("图片加载失败");
                });
            }
        }
	},
	/** 新增图片 **/
	onChange(v) {
		var reader = new FileReader();
		let onChge = this.props.onChange;
		reader.onload = function(e) {
			compress(v, this.result, onChge);
		};
		reader.readAsDataURL(document.getElementById('photoCom' + v).files[0]);

		this.state.list.push({idx: v});
		this.setState({now:this.state.now+1});
	},
	/** 删除 **/
	remove(v) {
		for (var i=0;i<this.state.list.length;i++) {
			if (this.state.list[i].idx == v) {
				this.state.list.splice(i, 1);
			}
		}
		this.setState({list:this.state.list});
        if (this.props.onChange){
            this.props.onChange();
        }
	},
	val() {
        var uplist = this.state.list.map(v => {
            // 新传base64提交
			if(v.v != null) {
                return null;
            }
            return document.getElementById("photoImg" + v.idx).toDataURL( 'image/jpeg', 0.6);
        });
        for(var i = 0; i < uplist.length; i++){if(uplist[i] == null){uplist.splice(i, 1);i--;}}
		// 上传图片文件
		if(uplist != null && uplist.length > 0){
            var param = uplist.map(v => {
            	return {
            		"class": "apply_cert",
					"data": v
				};
			});
            console.log(param);
            common.reqSync("image/image/upload/base64.json", {imageDatas: param}, r => {
				if(r != null && r.length > 0){
					var i = 0;
                    this.state.list.map(v => {
                        if(v.v == null){
                        	v.v = r[i++];
						}
					});
				}
			}, f => {
                ToastIt("上传图片失败");
			});
		}

		/*return this.state.list.map(v => {
			// 若当前是一开始初始化进来就有了的，则直接取出返回，防止无限制压缩图片，导致不清晰的情况
			if(this.state.vlist != null && this.state.vlist[v-1] != null){
				return this.state.vlist[v-1];
			}
			return document.getElementById("photoImg" + v).toDataURL( 'image/jpeg', 0.6);
		});*/
		return this.state.list.map(v=>{
			return v.v;
		});
	},
	render() {
		let photos = this.state.list.map(v => {
			return (
				<div key={v.idx}>
					<div onClick={this.remove.bind(this, v.idx)}/>
					<canvas id={"photoImg" + v.idx}></canvas>
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

