<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">

$(document).ready(function() {
	showFooterTabs("music-playlist");
});

</script>


<ul class="main-menu ui-listview-inset ui-corner-all ui-shadow"
	data-role="listview">

	<c:forEach items="${playlists}" var="playlist">

		<c:set var="playlistUrl" value="" />

		<c:choose>
			<c:when test="${playlistType == 'MUSIC'}">
				<c:url var="playlistUrl" value="/playlist/music" />
			</c:when>
		</c:choose>


		<li><a rel="internal"
			title="<spring:message code="music.playlist.title" />"
			data-media="${playlistType}"
			href="${playlistUrl}?playlist=<c:out value="${playlist.id}" />"><c:out
					value="${playlist.name}" /></a></li>
	</c:forEach>
</ul>