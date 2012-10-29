<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
	var pageNumber = 0;
	var searchLetter = "";
	$(document).ready(function() {
		$("ul.items ul.control-menu li.play-album a").click(function() {
			var albumId = $(this).closest("li.item").attr("id");
			albumId = parseId(albumId, "album-id");
			mashupMedia.playAlbum(albumId);
		});
				
		$("ul.items ul.control-menu li.append-album a").click(function() {
			var albumId = $(this).closest("li.item").attr("id");
			albumId = parseId(albumId, "album-id");
			mashupMedia.appendAlbum(albumId);
		});
		
		prepareShowPageTitle();
		prepareShowIndexLetters();

		$("#albums li").hover(function() {
			$(this).find("a.album-cover").addClass("highlight");
		}, function() {
			$(this).find("a.album-cover").removeClass("highlight");
		});

		$(window)
				.scroll(
						function() {
							if ($("#albums li").length == 0) {
								return;
							}

							if ($(window).scrollTop() >= $(
									document).height()
									- $(window).height()) {
								var pageNumber = mashupMedia.filterAlbumsPageNumber + 1;
								mashupMedia.filterAlbumsPageNumber = pageNumber;
								loadAlbums(true);
							}
						});

	});

	function prepareShowIndexLetters() {
		var selector = "ul.index-letters";
		if ($(selector).length == 1) {
			$(selector).show();
		}
	}
</script>

<h1 class="hide content-title">
	<spring:message code="music.albums.title" />
</h1>

<ul class="hide index-letters">
	<li><a href="javascript:;" rel="address:/address-list-albums"><spring:message code="action.all" /></a></li>
	<c:forEach items="${albumsPage.albumIndexLetters}" var="letter">
		<li><a href="javascript:;" rel="address:/address-filter-albums-letter-${letter}"><c:out value="${letter}" /></a></li>
	</c:forEach>
</ul>


<c:set var="indexLetter" value="" />
<ul id="albums" class="items">
	<c:forEach items="${albumsPage.albums}" var="album">
		<li id="album-id-${album.id}" class="item"><c:if test="${indexLetter != album.indexLetter}">
				<div class="index-letter" id="index-letter-${album.indexLetter}">${album.indexLetter}</div>
				<c:set var="indexLetter" value="${album.indexLetter}" />
			</c:if> <a class="album-cover" href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art-thumbnail/${album.id}" />"
				title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
		</a>
			<div class="album">
				<a href="javascript:;" rel="address:/address-load-album-${album.id}"><c:out value="${album.name}" /></a>
			</div>
			<div class="artist">
				<a href="javascript:;" rel="address:/address-artist-${album.artist.id}"><c:out value="${album.artist.name}" /></a>
			</div>
			
			<ul class="control-menu">
				<li class="first play-album"><a href="javascript:;"><spring:message code="action.play-all" /></a></li>
				<li class="append-album"><a href="javascript:;"><spring:message code="action.add-all" /></a></li>
			</ul>			
			
			</li>

	</c:forEach>
</ul>



