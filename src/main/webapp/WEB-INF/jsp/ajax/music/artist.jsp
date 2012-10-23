<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("div.albums div.album").hover(function() {
			$(this).addClass("highlight");
		}, function() {
			$(this).removeClass("highlight");
		});

	});
</script>

<h1>${artistPage.artist.name}</h1>


<ul id="albums">
	<c:forEach items="${artistPage.artist.albums}" var="album">
		<li><a class="album-cover" href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art-thumbnail/${album.id}" />"
				title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
		</a>
			<div class="artist">
				<a href="javascript:;" rel="address:/address-artist-${album.artist.id}"><c:out value="${album.artist.name}" /></a>
			</div>
			<div class="album">
				<a href="javascript:;" rel="address:/address-load-album-${album.id}"><c:out value="${album.name}" /></a>
			</div></li>
	</c:forEach>
</ul>
