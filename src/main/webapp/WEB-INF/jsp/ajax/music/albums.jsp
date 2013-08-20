<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<c:if test="${!isAppend}">
	<script type="text/javascript">
		var pageNumber = 0;
		var searchLetter = "";
		$(document).ready(function() {

			window.scrollTo(0, 0);

			$("div.music-sub-panel").on("mouseover", "div.albums div.album", function() {
				$(this).addClass("highlight");
			});
			
			$("div.music-sub-panel").on("mouseout", "div.albums div.album", function() {
				$(this).removeClass("highlight");
			});			
			

			$("ul.index-letters a").click(function() {
				fireRelLink(this);
			});

			$("div.music-sub-panel").on("click", "div.albums div.album a", function() {
				fireRelLink(this);
			});
			
			prepareShowIndexLetters();

			$("div.music-sub-panel").on("click", "div.albums div.album-control a.play", function() {
				var albumId = $(this).closest("div.album").attr("id");
				albumId = parseId(albumId, "album-id");
				mashupMedia.playAlbum(albumId);
			});

			$("div.music-sub-panel").on("click", "div.albums div.album-control a.add", function() {
				var albumId = $(this).closest("div.album").attr("id");
				albumId = parseId(albumId, "album-id");
				mashupMedia.appendAlbum(albumId);
			});
			
			$(window).scroll(function() {				
				if ($("div.albums div.album").length == 0) {
					return;
				}				
				appendContentsOnScroll();
			});

		});

		function prepareShowIndexLetters() {
			var selector = "ul.index-letters";
			if ($(selector).length == 1) {
				$(selector).show();
			}
		}
	</script>

	<h1>
		<spring:message code="music.albums.title" />
	</h1>

	<ul class="hide index-letters">
		<li><a href="javascript:;" rel="address:/address-list-albums"><spring:message code="action.all" /></a></li>
		<c:forEach items="${albumsPage.albumIndexLetters}" var="letter">
			<li><a href="javascript:;" rel="address:/address-filter-albums-letter-${letter}"><c:out value="${letter}" /></a></li>
		</c:forEach>
	</ul>


	<c:set var="indexLetter" value="" />
</c:if>



<div class="albums">
	<c:forEach items="${albumsPage.albums}" var="album">

		<c:if test="${indexLetter != album.indexLetter}">
			<div class="index-letter" id="index-letter-${album.indexLetter}">${album.indexLetter}</div>
			<c:set var="indexLetter" value="${album.indexLetter}" />
		</c:if>

		<div class="album" id="album-id-${album.id}">
			<a href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art/thumbnail/${album.id}" />"
				title="${album.artist.name} - ${album.name}" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
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



