<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="sub-panel">

	<h1>
		<a id="artistId-<c:out value="${albumPage.album.artist.id}" />" href="javascript:void(0);"><c:out
				value="${albumPage.album.artist.name}" /></a> -
		<c:out value="${albumPage.album.name}" />
	</h1>

	<div class="actions">
		<a href="javascript:void(0);"> <spring:message code="action.play-all" />
		</a> <a href="javascript:void(0);"> <spring:message code="action.add-all" />
		</a>
	</div>

	<div class="album-art">
		<img src="<c:url value="/app/music/album-art/${albumPage.album.id}" />"
			title="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />"
			alt="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />" />
	</div>

	<ul class="album-menu">
		<c:forEach items="${albumPage.songs}" var="song">
			<li id="songId-<c:out value="${song.id}"/>">
				<div class="controls">
					<a class="play" href="javascript:void(0);"><img
						src="<c:url value="$themePath/images/controls/play.png" />"
						title="<spring:message code="control.play" />" /></a> <a class="add" href="javascript:void(0);"><img
						src="<c:url value="$themePath/images/controls/add.png" />"
						title="<spring:message code="control.add" />" /></a>
				</div>
				<div class="meta">
					<c:out value="${song.meta}" />
				</div> <c:out value="${song.trackNumber}" /> - <c:out value="${song.title}" />
			</li>
		</c:forEach>
	</ul>

</div>


