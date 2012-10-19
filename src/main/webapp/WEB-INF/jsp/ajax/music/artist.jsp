<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("div.albums div.album").hover(function() {
			$(this).addClass("highlight");
		}, function() {
			$(this).removeClass("highlight");
		});

		$("div.albums div.album a").click(function() {
			var albumId = $(this).attr("id");
			albumId = albumId.replace("albumId-", "");
			var addressValue = "address-load-album-" + albumId;
			loadLocation(addressValue);
		});
		

	});
</script>

<h1>${artistPage.artist.name}</h1>

<div class="albums">
	<c:forEach items="${artistPage.artist.albums}" var="album">
		<div class="album">
			<a id="albumId-<c:out value="${album.id}" />" href="javascript:;"> <img src="<c:url value="/app/music/album-art/${album.id}" />"
				title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>
		</div>
	</c:forEach>
</div>

