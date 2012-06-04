
function loadIFrame(source, selector) {

	$(selector).html(
			"<iframe src=\"" + source
					+ "\" scrolling=\"no\" frameborder=\"0\"></iframe>");
	
//	console.log(links);
	
	var iFrame = $(selector + " iframe");
	var iFrameHead = $(iFrame).contents().find("head")[0];
	console.log(iFrameHead);

	var links = $("head link");
	
	for(var i = 0; i < links.length; i++) {
//		iFrameHead.appendChild(links[i]);
//		iFrameHead.append("<link />");
	}

	
	/*
	$(iFrameHead).append($("<link/>", 
	    { rel: "stylesheet", href: "/mashupmedia/themes/default/stylesheets/site.css", type: "text/css" }));
*/
	
// var iFrameHead = $(selector + " iframe head");
	
// var link = iFrame.document.

	$(iFrame).load(function() {
		this.style.height =
		    this.contentWindow.document.body.offsetHeight + 'px';

	});
	
//	console.log(head);
	
	
// var height = iFrame.contentWindow.document.body.offsetHeight + 'px';
// iFrame.style.height = height;

}

$('iframe').load(function() {
	alert("iframe");
	this.style.height = this.contentWindow.document.body.offsetHeight + 'px';
});