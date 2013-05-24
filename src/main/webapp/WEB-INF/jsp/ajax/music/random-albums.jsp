<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {

		$("div.albums div.album").hover(function() {
			$(this).addClass("highlight");
		}, function() {
			$(this).removeClass("highlight");
		});

		$("div.albums div.album a").click(function() {
			$.address.value($(this).attr('rel'));
		});

		$(window).scroll(function() {
			if ($("div.random-album-art div.album").length == 0) {
				return;
			}

			appendContentsOnScroll();
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

	});
</script>

<c:if test="${showTitle}">
	<h1>
		<spring:message code="music.random-albums.title" />
	</h1>
</c:if>


<div class="random-album-art albums">
	<c:forEach items="${albums}" var="album">
		<div class="album" id="album-id-${album.id}">
			<a href="javascript:;" rel="address:/address-load-album-${album.id}">
				<img
				src="<c:url value="/app/music/album-art-thumbnail/${album.id}" />"
				title="${album.artist.name} - ${album.name}"
				alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>
			
			<div><a href="javascript:;" rel="address:/address-artist-${album.artist.id}" >${album.artist.name}</a></div>
			<div><a href="javascript:;" rel="address:/address-load-album-${album.id}">${album.name}</a></div>
			
			<div class="album-control">
			
				<a class="play" href="javascript:;"
					title="<spring:message code="action.play" />"><span
					class="ui-icon ui-icon-play"></span></a>
				<c:if test="${isPlaylistOwner}">
					<a class="add" href="javascript:;"
						title="<spring:message code="action.add" />"><span
						class="ui-icon ui-icon-plus"></span></a>
				</c:if>			
			
			</div>
		</div>
	</c:forEach>
</div>


