<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
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
	});
</script>



	<h1>
		<a href="javascript:;" rel="address:/address-artist-${albumPage.album.artist.id}"><c:out
				value="${albumPage.album.artist.name}" /></a> -
		<c:out value="${albumPage.album.name}" />
	</h1>




	<ul class="control-menu" id="albumId-<c:out value="${albumPage.album.id}" />">
		<li class="first"><a href="javascript:;" id="play-all"> <spring:message
					code="action.play-all" />
		</a></li>

		<li><a href="javascript:;" id="add-all"> <spring:message code="action.add-all" />
		</a></li>

	</ul>



	<div class="album-art">
		<img src="<c:url value="/app/music/album-art/${albumPage.album.id}" />"
			title="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />"
			alt="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />" />
	</div>

	<ul class="album-menu">
		<c:forEach items="${albumPage.songs}" var="song">
			<li id="songId-<c:out value="${song.id}"/>">
				<div class="controls">
					<a class="play" href="javascript:;" title="<spring:message code="control.play" />"><span
						class="ui-icon ui-icon-play"></span></a> <a class="add" href="javascript:void(0);"
						title="<spring:message code="control.add" />"><span class="ui-icon ui-icon-plus"></span></a></div>
				<c:out value="${song.displayTitle}" />

				<div class="meta">
					<c:out value="${song.meta}" />
				</div>

			</li>
		</c:forEach>
	</ul>






