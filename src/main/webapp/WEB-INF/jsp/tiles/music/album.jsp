<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

        window.scrollTo(0, 0);
        showFooterTabs("music", "music-albums");

        $("h1.edit").editable("<c:url value="/app/restful/media/music/save-album-name" />", {
            tooltip: "<spring:message code="action.click.edit" />"
        });

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

        $("div.dynamic-content ul.tracks a.play").click(function() {
            var songId = $(this).closest("li").attr("id");
            songId = parseId(songId, "song-id");
            mashupMedia.playSong(songId);
        });

        $("div.dynamic-content ul.tracks a.add").click(function() {
            var songId = $(this).closest("li").attr("id");
            songId = parseId(songId, "song-id");
            mashupMedia.appendSong(songId);
        });

        $.getJSON("<c:url value="/app/ajax/music/artist/remote/${albumPage.album.artist.id}" />", function(data) {
            displayRemoteArtistInformation(data);
        });

        $("div.dynamic-content").off().on("click", "div.title-with-player-control div.re-encode a", function() {
            $.post("<c:url value="/app/restful/encode/music-album" />", { id: <c:out value="${albumPage.album.id}" /> })
            .done(function( data ) {
                mashupMedia.showMessage(data);          
            });            
        });

    });
</script>

<jsp:include page="/WEB-INF/jsp/inc/remote-music-info-js.jsp" />


<input type="hidden" id="artist-id" name="artist-id"
	value="${albumPage.album.artist.id}" />


<div class="title-with-player-control">

	<h1 class="edit" id="album-id-${albumPage.album.id}">${albumPage.album.name}</h1>

	<h2>
		- <a href="javascript:;"
			rel="address:/address-artist-${albumPage.album.artist.id}">${albumPage.album.artist.name}</a>
	</h2>




	<div class="control-menu"
		id="album-id-<c:out value="${albumPage.album.id}" />">

		<a href="javascript:;" id="play-all"
			title="<spring:message code="action.play" />"><img
			src="<c:url value="${themePath}/images/controls/play.png"/>" /></a>

		<c:if test="${isPlaylistOwner}">
			<a href="javascript:;" id="add-all"
				title="<spring:message code="action.add" />"><img
				src="<c:url value="${themePath}/images/controls/add.png"/>" /></a>

		</c:if>
	</div>

	<div class="re-encode">
		<a href="javascript:;"
			title="<spring:message code="action.re-encode.tip" />"><spring:message
				code="action.re-encode" /></a>
	</div>
</div>

<div id="remote" class="teaser-on">
	<a class="arrow-show-hide" href="javascript:void(0)"> <img
		src="<c:url value="/images/arrow-down.png" />" /></a>
	<div class="profile"></div>
	<ul class="images"></ul>

	<div class="disclaimer">
		<spring:message code="music.artists.remote" />
		<a href="http://www.last.fm" target="_blank" title=""><img
			title="last.fm" src="<c:url value="/images/lastfm.png" />" /></a>.
	</div>
</div>

<div class="album">

	<div class="album-art">
		<img
			src="<c:url value="/app/music/album-art/original/${albumPage.album.id}" />"
			title="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />"
			alt="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />" />
	</div>

	<ul class="tracks">
		<c:forEach items="${albumPage.songs}" var="song">
			<li id="song-id-<c:out value="${song.id}"/>">
				<div class="controls">
					<a class="play" href="javascript:;"
						title="<spring:message code="action.play" />"><img
						src="<c:url value="${themePath}/images/controls/play.png"/>" /></a>
					<c:if test="${isPlaylistOwner}">
						<a class="add" href="javascript:;"
							title="<spring:message code="action.add" />"><img
							src="<c:url value="${themePath}/images/controls/add.png"/>" /></a>
					</c:if>
				</div> <c:out value="${song.displayTitle}" />

				<div class="meta">
					<c:out value="${song.meta}" />
				</div>

			</li>
		</c:forEach>
	</ul>

</div>




