var printer = {};
{
	var imageUrl = "https://sv.iyb.tm/printer/resource/template/";

	var imgNum = 0;
	var scale = 100;
	var env = {};

	function enlarge() {
		scale += 25;
		if (scale > 200) scale = 200;
		$("#" + env.contentId).css("width", scale + "%");
	}

	function reduce() {
		scale -= 25;
		if (scale < 100) scale = 100;
		$("#" + env.contentId).css("width", scale + "%");
	}

	function draw(res) {
		imgNum = 0;

		res.forEach(function (e) {
			e.image.forEach(function (m) {
				imgNum++;
			});
		});
		if (imgNum == 0) drawAll(res);
		res.forEach(function (e) {
			e.image.forEach(function (m) {
				if (m.url != null || m.url != "") {
					try {
						var img = new Image();
						img.src = imageUrl + env.templateCode + "/" + m.url;
						if (img.complete) {
							console.log("complete...");
							imgNum--;
							if (imgNum == 0) drawAll(res);
						} else {
							img.onload = function () {
								console.log("onload...");
								imgNum--;
								if (imgNum == 0) drawAll(res);
							};
							img.onerror = function () {
								console.log("onerror...");
								imgNum--;
								if (imgNum == 0) drawAll(res);
							}
						}
					} catch (e) {
						imgNum--;
						if (imgNum == 0) drawAll(res);
					}
				}
				else {
					imgNum--;
					if (imgNum == 0) drawAll(res);
				}
			});
		});
	}

	function drawAll(res) {
		$("#" + env.contentId).html("");
		//$("#content").css("background", "#AAA");
		for (var i = 0; i < res.length; i++) {
			drawPage(i, res[i]);
		}
	}

	function drawPage(index, e) {
		$("#" + env.contentId).append("<div id='d" + index + "' style='margin:5pt auto 5pt auto;padding:0;border:1px solid black;'><canvas id='p" + index + "' style='width:100%;'></canvas></div>");

		var canvas = document.getElementById("p" + index);
		if (canvas == null) return false;
		canvas.setAttribute("width", e.w);
		canvas.setAttribute("height", e.h);

		var div = document.getElementById("d" + index);
		div.style.width = (e.w > e.h ? "99" : "70") + "%";
		div.style.left = (e.w < e.h ? "14" : "0") + "%";

		var context = canvas.getContext("2d");
		context.fillStyle = "#FFFFFF";
		context.fillRect(0, 0, e.w, e.h);
		context.textBaseline = 'top';

		e.image.forEach(function (m) {
			drawBase(context, m.x, m.y, m.w, m.h, m.bg, m.bdc, m.bd);
			drawImage(context, m.x, m.y, m.w, m.h, m.url);
		});

		e.panel.forEach(function (m) {
			drawRect(context, m.x, m.y, m.w, m.h, m.bg);
		});

		e.rect.forEach(function (m) {
			drawBase(context, m.x, m.y, m.w, m.h, m.bg, m.bdc, m.bd);
			drawRect(context, m.x, m.y, m.w, m.h, m.c);
		});

		e.text.forEach(function (m) {
			drawBase(context, m.x, m.y, m.w, m.h, m.bg, m.bdc, m.bd);
			drawText(context, m.x, m.y, m.w, m.h, m.c, m.f, m.fs, m.ha, m.va, m.v, m.lh);
		});
	}

	function drawBase(context, x, y, w, h, c2, borderc, border) {
		if (c2 != null) {
			context.fillStyle = "#" + c2;
			context.fillRect(x, y, w, h);
		}

		if (borderc != null) {
			context.strokeStyle = "#" + borderc;
			context.fillStyle = "#" + borderc;
			drawBorder(context, border[0], x, y, border[0], h);
			drawBorder(context, border[1], x, y, w, border[1]);
			drawBorder(context, border[2], x + w - border[2], y, border[2], h);
			drawBorder(context, border[3], x, y + h - border[3], w, border[3]);
		}
	}

	function drawBorder(context, b, x, y, w, h) {
		if (b == 0) {
			context.beginPath();
			context.moveTo(x, y);
			context.lineTo(x + w, y + h);
			context.stroke();
		}
		else if (b > 0) {
			context.fillRect(x, y, w, h);
		}
	}

	function drawRect(context, x, y, w, h, c1) {
		if (c1 == null || c1 == "") c1 = "FFF";
		context.fillStyle = "#" + c1;
		context.fillRect(x, y, w, h);
	}

	function drawImage(context, x, y, w, h, url) {
		if (url == null || url == "")
			return;
		try {
			var img = new Image();
			img.src = imageUrl + env.templateCode + "/" + url;
			if (img.complete) {
				context.drawImage(img, 0, 0, img.width, img.height, x, y, w, h);
			}
			else {
				img.onload = function () {
					context.drawImage(this, 0, 0, this.width, this.height, x, y, w, h);
				}
			}
		} catch (e) {
		}
	}

	function drawText(context, x, y, w, h, c1, font, fontsize, align, valign, text, lineheight) {
		if (text == null) return;

		if (c1 == null || c1 == "") c1 = "FFF";
		context.fillStyle = "#" + c1;
		fontstr = fontsize + "px";
		if (font == "hei")
			fontstr += " 黑体";
		else if (font == "song")
			fontstr += " 宋体";
		else if (font == "kai")
			fontstr += " 楷体";
		context.font = fontstr;

		var txt = text.split("\n");
		for (var i = 0; i < txt.length; i++) {
			var x1 = x, y1 = y;
			if (align == 0)
				x1 = x + (w - context.measureText(txt[i]).width) / 2;
			else if (align > 0)
				x1 = x + w - context.measureText(txt[i]).width;
			if (valign == 0)
				y1 = y + (h - lineheight * txt.length) / 2;
			else if (valign > 0)
				y1 = y + h - lineheight * txt.length - i;

			context.fillText(txt[i], x1, y1 + lineheight * i);
		}
	}

	printer.test = function(templateCode, contentId) {
		env.templateCode = templateCode;
		env.contentId = contentId;
		common.post("https://sv.iyb.tm/printer/test.json", {templateCode: env.templateCode, outputType: "data"}, r => {
			draw(r);
		});
	}

	printer.preview = function(templateCode, contentId, data) {
		env.templateCode = templateCode;
		env.contentId = contentId;
		common.post("https://sv.iyb.tm/printer/print.json", {templateCode: env.templateCode, outputType: "data", content: data}, r => {
			draw(r);
		});
	}
}