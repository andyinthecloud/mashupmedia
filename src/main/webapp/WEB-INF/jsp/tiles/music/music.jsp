<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="random-album-art">
	<c:forEach items="${musicPage.albums}" var="album">
		<div class="album">
			<a href="<c:url value="/app/music/album/${album.id}" />"> <img
				src="<c:url value="/app/music/album-art/${album.id}" />"
				title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />"
				alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>
		</div>
	</c:forEach>
</div>


