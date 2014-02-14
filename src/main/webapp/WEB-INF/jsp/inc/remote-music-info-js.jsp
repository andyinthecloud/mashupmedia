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

	    $.post("<c:url value="/app/ajax/discogs/search-artist" />", {
		name : searchArtist
	    }).done(function(data) {
		var artistsHtml = "";
		$.each(data, function(index) {
		    artistsHtml += "<li><a id=\"search-results-discogs-id-" + data[index].id + "\" href=\"javascript:;\">" + data[index].name + "</a></li>"
		});
		$("#remote-dialog ul.search-results").html(artistsHtml);
	    });
	});

	$("#remote-dialog ul.search-results").on("click", "li a", function(event) {
	    var discogsId = $(this).attr("id");
	    discogsId = parseId(discogsId, "search-results-discogs-id");
	    var artistId = $("#remote-artist-id").val();
	    $.post("<c:url value="/app/ajax/discogs/save-artist" />", {
		discogsId : discogsId,
		artistId : artistId
	    }).done(function(data) {
		$.get("<c:url value="/app/ajax/discogs/discogs-artist-id/" />/" + discogsId).done(function(data) {
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
	
	$("a.arrow-show-hide").click(function() {
		
		if ($("div.information div.introduction").is(":visible")) {
			$("div.information div.introduction").hide();
			$("div.information div.content").show();
			
		} else {
			$("div.information div.introduction").show();
			$("div.information div.content").hide();			
		}
		
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
    	$.each(data.remoteImages, function(index, remoteImage){
    	//	<a class="fancybox" rel="artist-images" href="<c:url value="${remoteImage.imageUrl}" />"><img src="<c:url value="${remoteImage.thumbUrl}" />" /></a>
    		
//    		$("#remote div.images").append("<a class=\"fancybox\" rel=\"artist-images\" href=\""+mashupMedia.contextUrl + remoteImage.imageUrl +"\"><img src=\"" + mashupMedia.contextUrl + remoteImage.thumbUrl + "/></a>");
    		$("#remote div.images").append("<a class=\"fancybox\" rel=\"artist-images\" href=\""+mashupMedia.contextUrl + remoteImage.imageUrl +"\"><img src=\"" + mashupMedia.contextUrl + remoteImage.thumbUrl + "\"></a>");
    		
    	    
    	});
    }
</script>

<div id="remote-dialog" class="dialog" title="Search Discogs for artist information">
	<p>
		<input type="text" name="name" class="search-field" value="<spring:message code="music.artists.remote.search" />" /><input type="button"
			value="Search" />
	<ul class="search-results">

	</ul>


	</p>
</div>
