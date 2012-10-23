<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		prepareShowPageTitle();
		
		$("div.albums div.album").hover(function() {
			$(this).addClass("highlight");
		}, function() {
			$(this).removeClass("highlight");
		});
		
		$(window).scroll(function () {
			if ($("div.random-album-art").length == 0) {
				return;
			}
			
		    if ($(window).scrollTop() >= $(document).height() - $(window).height()) {
		    	loadRandomAlbums(true);
		    }
		});

	});
</script>

<h1 class="hide content-title">
	<spring:message code="music.random-albums.title" />
</h1>

<div class="random-album-art albums">
	<c:forEach items="${albums}" var="album">
		<div class="album">
			<a href="javascript:;" rel="address:/address-load-album-${album.id}" > <img src="<c:url value="/app/music/album-art-thumbnail/${album.id}" />"
				title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>
		</div>
	</c:forEach>
</div>

