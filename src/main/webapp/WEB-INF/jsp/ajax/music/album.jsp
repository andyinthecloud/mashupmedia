<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		window.scrollTo(0, 0);

		$("#play-all").click(function() {
			var albumId = $(this).closest("ul").attr("id");
			albumId = albumId.replace("albumId-", "");
			mashupMedia.playAlbum(albumId);
		});

		$("#add-all").click(function() {
			var albumId = $(this).closest("ul").attr("id");
			albumId = albumId.replace("albumId-", "");
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

		
	});
</script>

<jsp:include page="/WEB-INF/jsp/inc/discogs-js.jsp" />

<div class="title-with-player-control">
	<h1>
		<a href="javascript:;" rel="address:/address-artist-${albumPage.album.artist.id}"><c:out value="${albumPage.album.artist.name}" /></a> -
		<c:out value="${albumPage.album.name}" />
	</h1>

	<div class="control-menu" id="artist-id-<c:out value="${artistPage.artist.id}" />">
		<a id="play-all" class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play">&nbsp;</span></a>
		<c:if test="${isPlaylistOwner}">
			<a id="add-all" class="add" href="javascript:;" title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus">&nbsp;</span></a>
		</c:if>
	</div>

</div>


<div class="information">
	<div class="introduction">
		<c:out value="${albumPage.remoteMediaMetaItem.introduction}" escapeXml="false" />
		<a href="javascript:;" class="information-more"> <spring:message code="more" /></a>
	</div>

	<div class="content hide">
		<div class="profile">
			<c:out value="${albumPage.remoteMediaMetaItem.profile}" escapeXml="false" />
		</div>

		<div class="images">
			<c:forEach items="${albumPage.remoteMediaMetaItem.remoteImages}" var="remoteImage">
				<a class="fancybox" rel="artist-images" href="<c:url value="${remoteImage.imageUrl}" />"><img src="<c:url value="${remoteImage.thumbUrl}" />" /></a>
			</c:forEach>
		</div>

		<div class="discogs">
			<spring:message code="music.artists.discogs" />
			<a href="http://www.discogs.com" target="_blank"><img src="<c:url value="/images/discogs.png" />" /></a>. <a class="incorrect" href="javascript:;"><spring:message
					code="music.artists.discogs.correct" /></a>
			<a href="javascript:;" class="information-less"> <spring:message code="less" /></a>
		</div>
	</div>
</div>


<div class="album-art">
	<img src="<c:url value="/app/music/album-art/${albumPage.album.id}?thumb" />"
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






