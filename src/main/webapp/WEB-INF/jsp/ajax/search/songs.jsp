<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	var pageNumber = 0;
	var searchLetter = "";
	$(document)
			.ready(
					function() {
						$("ul.items div.controls a.play").click(
								function() {
									var songId = $(this).closest("li.item")
											.attr("id");
									songId = parseId(songId, "song-id");
									mashupMedia.playSong(songId);
								});

						$("ul.items div.controls a.add").click(
								function() {
									var songId = $(this).closest("li.item")
											.attr("id");
									songId = parseId(songId, "song-id");
									mashupMedia.appendSong(songId);
								});

						prepareShowPageTitle();

						$("ul.items li").hover(
								function() {
									$(this).find("a.album-cover").addClass(
											"highlight");
								},
								function() {
									$(this).find("a.album-cover").removeClass(
											"highlight");
								});

						$(window).scroll(
								function() {
									if ($("ul.items li").length == 0) {
										return;
									}

									if ($(window).scrollTop() >= $(
											document).height()
											- $(window).height()) {
										var pageNumber = mashupMedia.filterPageNumber + 1;
										mashupMedia.filterPageNumber = pageNumber;
										loadSongSearchResults(true);
									}
								});

					});


</script>


<h1 class="hide content-title">
	<spring:message code="music.search-results.title" />
</h1>

<ul class="items">

	<c:forEach items="${songs}" var="song" varStatus="status">
		<li id="song-id-${song.id}" class="item"><a class="album-cover" href="javascript:;" rel="address:/address-load-album-${song.album.id}"> <img src="<c:url value="/app/music/album-art-thumbnail/${song.album.id}" />"
				title="<c:out value="${song.album.artist.name}" /> - <c:out value="${song.album.name}" />" alt="<c:out value="${song.album.artist.name}" /> - <c:out value="${song.album.name}" />" />
		</a>

			<div class="song">
				<c:out value="${song.displayTitle}" />
			</div>

			<div class="artist-and-album">
				<a href="javascript:;" rel="address:/address-artist-${song.artist.id}"><c:out value="${song.artist.name}" /></a> - <a href="javascript:;" rel="address:/address-load-album-${song.album.id}"><c:out
						value="${song.album.name}" /></a>
			</div>

			<div class="controls">
				<a class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a> <a class="add" href="javascript:;"
					title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus"></span></a>
			</div></li>
	</c:forEach>

</ul>