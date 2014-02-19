<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {

		window.scrollTo(0, 0);

		$("#play-all").click(function() {
			var albumId = $(this).closest("div").attr("id");
			albumId = albumId.replace("album-id-", "");
			mashupMedia.playAlbum(albumId);
		});

		$("#add-all").click(function() {
			var albumId = $(this).closest("div").attr("id");
			albumId = albumId.replace("album-id-", "");
			mashupMedia.appendAlbum(albumId);
		});

		$("ul.album-menu a.play").click(function() {
			var songId = $(this).closest("li").attr("id");
			songId = parseId(songId, "song-id");
			mashupMedia.playSong(songId);
		});

		$("ul.album-menu a.add").click(function() {
			var songId = $(this).closest("li").attr("id");
			songId = parseId(songId, "song-id");
			mashupMedia.appendSong(songId);
		});

		$("h1 a").click(function() {
			fireRelLink(this);
		});
		
		$.getJSON("<c:url value="/app/ajax/music/artist/remote/${albumPage.album.artist.id}" />", function( data ) {
			displayRemoteArtistInformation(data);
		});			

	});
</script>

<jsp:include page="/WEB-INF/jsp/inc/remote-music-info-js.jsp" />

<input type="hidden" id="artist-id" name="artist-id" value="${albumPage.album.artist.id}" />


<div class="title-with-player-control">
	<h1>
		<a href="javascript:;" rel="address:/address-artist-${albumPage.album.artist.id}"><c:out value="${albumPage.album.artist.name}" /></a> -
		<c:out value="${albumPage.album.name}" />
	</h1>

	<div class="control-menu" id="album-id-<c:out value="${albumPage.album.id}" />">
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
		<a href="http://www.last.fm" target="_blank" title=""><img title="last.fm" src="<c:url value="/images/lastfm.png" />" /></a>. <a class="incorrect" href="javascript:;"><spring:message
				code="music.artists.remote.correct" /></a>
	</div>	
</div>


<div class="album-art">
	<img src="<c:url value="/app/music/album-art/original/${albumPage.album.id}" />"
		title="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />"
		alt="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />" />
</div>

<ul class="album-menu">
	<c:forEach items="${albumPage.songs}" var="song">
		<li id="song-id-<c:out value="${song.id}"/>">
			<div class="controls">
				<a class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a>
				<c:if test="${isPlaylistOwner}">
					<a class="add" href="javascript:;" title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus"></span></a>
				</c:if>
			</div> <c:out value="${song.displayTitle}" />

			<div class="meta">
				<c:out value="${song.meta}" />
			</div>

		</li>
	</c:forEach>
</ul>






