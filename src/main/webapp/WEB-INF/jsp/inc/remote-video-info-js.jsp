<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

	$("#remote div.images .fancybox").fancybox();

	$("#remote").on("click", "a.incorrect", function() {
	    $("#remote-dialog").dialog();
	    $("#remote-dialog input[type=text]").blur();
	});

	var artistNameLabel = "<spring:message code="music.artists.remote.search" />";

	$("#remote-dialog input[type=text]").blur(function() {
	    var artistName = $.trim($(this).val());
	    if (artistName.length == 0) {
		$(this).val(artistNameLabel);
	    }
	});

	$("#remote-dialog input[type=text]").focus(function() {
	    var artistName = $.trim($(this).val());
	    if (artistName == artistNameLabel) {
		$(this).val("");
	    }
	});

	

	
	$("#remote a.arrow-show-hide").click(function() {		
		var overflow = "hidden";
		var arrowIimageSource = "<c:url value="/images/arrow-down.png" />";
		var remoteHeight = "15px";
		if ($("#remote").css("overflow") == "hidden") {
			overflow = "visible";
			arrowIimageSource = "<c:url value="/images/arrow-up.png" />";
			remoteHeight = "auto";
		}
		
		$("#remote").css("overflow", overflow);
		$("#remote").css("height", remoteHeight);		
		$(this).find("img").attr("src", arrowIimageSource);
	});
	
	
    });
    

    	
    
    
    function prepareImageUrl(imageUrl) {
    	imageUrl = $.trim(imageUrl);
    	if (imageUrl.length == 0) {
    	    return "";
    	}

    	if (imageUrl.indexOf("/") == 0) {
    	    imageUrl = imageUrl.substring(1);
    	}

    	imageUrl = "<c:url value="/" />" + imageUrl;
    	return imageUrl;

	}
    

    function displayRemoteArtistInformation(data) {
    	$("#remote div.profile").html(data.introduction);
    	if (data.error) {
    		return;
    	} 
    		
    	$.each(data.remoteImages, function(index, remoteImage){
    		$("#remote div.images").append("<a class=\"fancybox\" rel=\"artist-images\" href=\""+mashupMedia.contextUrl + remoteImage.imageUrl +"\"><img src=\"" + mashupMedia.contextUrl + remoteImage.thumbUrl + "\"></a>");   	    
    	});
    	
    	$("#remote div.disclaimer").show();
    }
</script>

<div id="remote-dialog" class="dialog" title="Find artist">
	<p>
		<input type="text" name="name" class="search-field" value="<spring:message code="music.artists.remote.search" />" /><input type="button"
			value="Search" />
		<ul class="search-results">
	
		</ul>
	</p>
</div>








