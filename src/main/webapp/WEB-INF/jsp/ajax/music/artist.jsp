<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

	window.scrollTo(0, 0);

	$("div.albums div.album").hover(function() {
	    $(this).addClass("highlight");
	}, function() {
	    $(this).removeClass("highlight");
	});

	$("#play-all").click(function() {
	    var artistId = $(this).closest("div.control-menu").attr("id");
	    artistId = parseId(artistId, "artist-id");
	    mashupMedia.playArtist(artistId);
	});

	$("#add-all").click(function() {
	    var artistId = $(this).closest("div.control-menu").attr("id");
	    artistId = parseId(artistId, "artist-id");
	    mashupMedia.appendArtist(artistId);
	});

	$("div.albums div.album-control a.play").click(function() {
	    var albumId = $(this).closest("div.album").attr("id");
	    albumId = parseId(albumId, "album-id");
	    mashupMedia.playAlbum(albumId);
	});

	$("div.albums div.album-control a.add").click(function() {
	    var albumId = $(this).closest("div.album").attr("id");
	    albumId = parseId(albumId, "album-id");
	    mashupMedia.appendAlbum(albumId);
	});

	$("div.information div.images .fancybox").fancybox();

	$("div.information div.discogs a.incorrect").click(function() {
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
	    var artistId = $(this).closest("ul").attr("id");
	    artistId = parseId(artistId, "search-results-artist-id");
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
	<ul class="search-results" id="search-results-artist-id-${artistPage.artist.id}">

	</ul>


	</p>
</div>


<div class="title-with-player-control">
	<h1>${artistPage.artist.name}</h1>

	<div class="control-menu" id="artist-id-<c:out value="${artistPage.artist.id}" />">
		<a id="play-all" class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play">&nbsp;</span></a>
		<c:if test="${isPlaylistOwner}">
			<a id="add-all" class="add" href="javascript:;" title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus">&nbsp;</span></a>
		</c:if>
	</div>

</div>



<div class="information">
	<div class="profile">
		<c:out value="${artistPage.remoteMediaMeta.profile}" escapeXml="false" />
	</div>

	<div class="images">
		<c:forEach items="${artistPage.remoteMediaMeta.remoteImages}" var="remoteImage">
			<a class="fancybox" rel="artist-images" href="<c:url value="${remoteImage.imageUrl}" />"><img src="<c:url value="${remoteImage.thumbUrl}" />" /></a>
		</c:forEach>
	</div>

	<div class="discogs">
		<spring:message code="music.artists.discogs" />
		<a href="http://www.discogs.com" target="_blank"><img src="<c:url value="/images/discogs.png" />" /></a>. <a class="incorrect" href="javascript:;"><spring:message code="music.artists.discogs.correct" /></a>

	</div>
</div>


<div id="albums" class="albums">

	<c:forEach items="${artistPage.artist.albums}" var="album">
		<div class="album" id="album-id-${album.id}">
			<a href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art-thumbnail/${album.id}" />" title="${album.artist.name} - ${album.name}" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>

			<div>
				<a href="javascript:;" rel="address:/address-artist-${album.artist.id}">${album.artist.name}</a>
			</div>
			<div>
				<a href="javascript:;" rel="address:/address-load-album-${album.id}">${album.name}</a>
			</div>

			<div class="album-control">

				<a class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a>
				<c:if test="${isPlaylistOwner}">
					<a class="add" href="javascript:;" title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus"></span></a>
				</c:if>

			</div>
		</div>
	</c:forEach>

</div>
