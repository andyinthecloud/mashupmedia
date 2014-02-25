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

	$("#remote-dialog input[type=button]").click(function() {
	    var searchArtist = $("#remote-dialog input[type=text]").val();
	    if (searchArtist == artistNameLabel) {
		return;
	    }

	    $.post("<c:url value="/app/ajax/remote/artist/search" />", {
		name : searchArtist
	    }).done(function(data) {
		var artistsHtml = "";
		$.each(data, function(index) {
		    artistsHtml += "<li><a id=\"search-results-remote-id-" + data[index].remoteId + "\" href=\"javascript:;\">" + data[index].name + "</a></li>"
		});
		$("#remote-dialog ul.search-results").html(artistsHtml);
	    });
	});

	$("#remote-dialog ul.search-results").on("click", "li a", function(event) {
	    var remoteArtistId = $(this).attr("id");
	    remoteArtistId = remoteArtistId.replace("search-results-remote-id-", "")	    
	    var artistId = $("#artist-id").val();
	    $.post("<c:url value="/app/ajax/remote/artist/save" />", {
	    remoteArtistId : remoteArtistId,
		artistId : artistId
	    }).done(function(data) {
		$.get("<c:url value="/app/ajax/music/artist/remote/" />" + artistId).done(function(data) {
		    // console.log(data);
		    $("div.music-sub-panel h1").html(data.name);
		    $("div.music-sub-panel div.information div.profile").html(data.profile);

		    var artistImagesHtml = "";
		    $.each(data.remoteImages, function(index) {
			var imageUrl = prepareImageUrl(data.remoteImages[index].imageUrl);
			var thumbUrl = prepareImageUrl(data.remoteImages[index].thumbUrl);

			artistImagesHtml += "<a class=\"fancybox\" rel=\"artist-images\" href=\"" + imageUrl + "\"><img src=\"" + thumbUrl + "\" /></a>";
		    });
		    $("div.music-sub-panel div.information div.images").html(artistImagesHtml);

		});
	    });

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
