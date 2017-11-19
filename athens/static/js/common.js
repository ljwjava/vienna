var env = {};

var common = {};

common.url = function(url) {
	return common.server() + "/api/" + url;
}

common.link = function(link) {
	return common.server() + "/" + link;
}

common.post = function(url, val, callback, failback) {
	$.ajax({url:url, type:"POST", data:JSON.stringify(val), xhrFields: { withCredentials: true }, contentType:'application/json;charset=UTF-8', success:function(r) {
		if (r.result == "success")
			callback(r.content);
		else if (failback == null)
			alert("失败 - " + r.reason);
		else
			failback(r.reason);
	}, fail: function(r) {
		alert("访问服务器失败");
	}, dataType:"json"});
};

common.req = function(url, val, callback, failback) {
	common.post(common.url(url), val, callback, failback);
};

common.param = function(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}

common.server = function() {
	var server = location.protocol + "//" + location.host;
	if (location.pathname.startsWith("/rel/"))
		server += "/rel";
	else if (location.pathname.startsWith("/uat/"))
		server += "/uat";
	return server;
}

common.copy = function(a, b) {
	if (b != null)
		for (var x in b)
			if (b[x] != null)
				a[x] = b[x];
	return a;
}

String.prototype.startsWith = function (prefix) {
	return this.slice(0, prefix.length) === prefix;
};

String.prototype.endsWith = function (str) {
	return this.slice(this.length - str.length) === str;
};

Date.prototype.format = function(format){
	 //eg:format="yyyy-MM-dd hh:mm:ss";
	 var o = {
	 "M+" : this.getMonth()+1, //month
	 "d+" : this.getDate(),   //day
	 "h+" : this.getHours(),  //hour
	   "m+" : this.getMinutes(), //minute
	   "s+" : this.getSeconds(), //second
	   "q+" : Math.floor((this.getMonth()+3)/3), //quarter
	   "S" : this.getMilliseconds() //millisecond
	  }
	  
	  if(/(y+)/.test(format)) {
	  format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	  }
	  
	  for(var k in o) {
	  if(new RegExp("("+ k +")").test(format)) {
	   format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
	  }
	  }
	 return format;
	}

common.getFormData = function(formId) {
	var data = {};
	$("#" + formId).find("input,select").each(function() {
		var name = $(this).attr("name");
		if (name) {
			var type = $(this).attr("type");
			var str = $(this).val();
			if (type == "checkbox") {
				if (!$(this).is(':checked'))
					str = null;
			} else if (type == "radio") {
				if (!$(this).is(':checked'))
					str = null;
			}
			if (str != null)
				data[name] = data[name] == null ? str : data[name] + "," + str;
		}
	});
	return data;
}

common.setFormData = function(formId, vals) {
	$("#" + formId).find("input,select").each(function() {
		var name = $(this).attr("name");
		if (name) {
			var type = $(this).attr("type");
			var val = vals[name];
			if (type == "checkbox") {
				var str = $(this).val();
				if ((","+val+",").indexOf(","+str+",")>=0)
					$(this).attr("checked",true);
			} else if (type == "radio") {
				var str = $(this).val();
				if (str == val)
					$(this).attr("checked",true);
			} else if (val) {
				$(this).val(val);
			}
		}
	});
}

common.ifNull = function(v1, v2) {
	return v1 == null ? v2 : v1;
}

common.save = function(k, v) {
	window.sessionStorage.setItem(k, v);
	window.sessionStorage.setItem(k + "/time", new Date().getTime());
}

common.load = function(k, overtime) {
	if (overtime == null)
		return window.sessionStorage.getItem(k);
	var t = window.sessionStorage.getItem(k + "/time");
	if (t != null && new Date().getTime() - t < overtime)
		return window.sessionStorage.getItem(k);
	return null;
}


common.age = function(strBirthdayArr) {
	var returnAge;

	var birthYear = Number(strBirthdayArr.substr(0,4));
	var birthMonth = Number(strBirthdayArr.substr(5,2));
	var birthDay = Number(strBirthdayArr.substr(8,2));

	var d = new Date();
	var nowYear = d.getFullYear();
	var nowMonth = d.getMonth() + 1;
	var nowDay = d.getDate();

	if(nowYear == birthYear) {
		returnAge = 0;//同年 则为0岁
	}  else {
		var ageDiff = nowYear - birthYear;
		if(ageDiff > 0) {
			if(nowMonth == birthMonth) {
				var dayDiff = nowDay - birthDay;
				if(dayDiff < 0)
					returnAge = ageDiff - 1;
				else
					returnAge = ageDiff ;
			} else {
				var monthDiff = nowMonth - birthMonth;
				if(monthDiff < 0)
					returnAge = ageDiff - 1;
				else
					returnAge = ageDiff ;
			}
		} else {
			returnAge = -1;
		}
	}

	return returnAge;
}

common.logout = function() {
	common.req("user/logout.json", null, function() {
		document.location.href = common.link("login.html");
	});
}

