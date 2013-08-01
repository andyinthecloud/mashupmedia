<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

	$("div.information div.images .fancybox").fancybox();

	$("div.music-sub-panel").on("click", "div.information div.discogs a.incorrect", function() {
	    $("#discogs-dialog").dialog();
	    $("#discogs-dialog input[type=text]").blur();
	});

	var artistNameLabel = "<spring:message code="music.artists.discogs.search" />";

	$("#discogs-dialog input[type=text]").blur(function() {
	    var artistName = $.trim($(this).val());
	    if (artistName.length == 0) {
		$(this).val(artistNameLabel);
	    }
	});

	$("#discogs-dialog input[type=text]").focus(function() {
	    var artistName = $.trim($(this).val());
	    if (artistName == artistNameLabel) {
		$(this).val("");
	    }
	});

	$("#discogs-dialog input[type=button]").click(function() {
	    var searchArtist = $("#discogs-dialog input[type=text]").val();
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
		$("#discogs-dialog ul.search-results").html(artistsHtml);
	    });
	});

	$("#discogs-dialog ul.search-results").on("click", "li a", function(event) {
	    var discogsId = $(this).attr("id");
	    discogsId = parseId(discogsId, "search-results-discogs-id");
	    var artistId = $("#discogs-artist-id").val();
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
</script>

<div id="discogs-dialog" class="dialog" title="Search Discogs for artist information">
	<p>
		<input type="text" name="name" class="search-field" value="<spring:message code="music.artists.discogs.search" />" /><input type="button" value="Search" />
	<ul class="search-results">

	</ul>


	</p>
</div>