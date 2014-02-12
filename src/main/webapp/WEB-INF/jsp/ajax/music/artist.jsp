<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
	window.scrollTo(0, 0);

	
	$("div.albums div.album a").click(function() {
		fireRelLink(this);
	});
	
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
	
	$.getJSON("<c:url value="/app/ajax/music/artist/remote/${artistPage.artist.id}" />", function( data ) {
		displayRemoteArtistInformation(data);
		
		//$("#discogs div.profile").html(data.introduction);
		/*
		
		var items = [];
		$.each( data, function( key, val ) {
		items.push( "<li id='" + key + "'>" + val + "</li>" );
		});
		$( "<ul/>", {
		"class": "my-new-list",
		html: items.join( "" )
		}).appendTo( "body" );
		*/
		
	});	
	

    });


</script>

<jsp:include page="/WEB-INF/jsp/inc/remote-music-info-js.jsp" />

<input type="hidden" id="discogs-artist-id" name="discogs-artist-id" value="${artistPage.artist.id}" />

<div class="title-with-player-control">
	<h1>${artistPage.artist.name}</h1>

	<div class="control-menu" id="artist-id-<c:out value="${artistPage.artist.id}" />">
		<a id="play-all" class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play">&nbsp;</span></a>
		<c:if test="${isPlaylistOwner}">
			<a id="add-all" class="add" href="javascript:;" title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus">&nbsp;</span></a>
		</c:if>
	</div>

</div>



<div id="remote">
	<div class="profile"></div>
	<div class="images"></div>

	<div class="disclaimer">
		<spring:message code="music.artists.remote" />
		<a href="http://www.discogs.com" target="_blank"><img src="<c:url value="/images/discogs.png" />" /></a>. <a class="incorrect" href="javascript:;"><spring:message
				code="music.artists.remote.correct" /></a>
	</div>	
</div>


<div id="albums" class="albums">

	<c:forEach items="${artistPage.artist.albums}" var="album">
		<div class="album" id="album-id-${album.id}">
			<a href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art/thumbnail/${album.id}" />" title="${album.artist.name} - ${album.name}" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
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
