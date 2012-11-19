<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<ul class="main-menu">
	<c:forEach items="${playlists}" var="playlist">
		<li><a href="javascript:;" rel="address:/address-playlist-${playlist.id}">${playlist.name}</a></li>
	</c:forEach>
</ul>