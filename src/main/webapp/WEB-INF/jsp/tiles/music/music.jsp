<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="random-album-art">
	<c:forEach items="${musicPage.albums}" var="album">
		<div class="album">
			<div class="image">
				<img src="<c:url value="/app/music/album-art/${album.id}" />" />
			</div>
			<div class="labels">
				<div class="artist-name">
					<c:out value="${album.artist.name}" />
				</div>
				<div class="album-name">
					<c:out value="${album.name}" />
				</div>
			</div>
		</div>
	</c:forEach>
</div>


