<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<h1 class="bottom-margin">
	<spring:message code="playlists.title" />
</h1>

<ul class="main-menu">
	<c:forEach items="${playlists}" var="playlist">
		<li><a href="javascript:;" rel="address:/address-playlist-${playlist.id}">${playlist.name} <c:if test="${playlist.isUserDefault}">
					<spring:message code="playlist.user.default" />
				</c:if>
		</a></li>
	</c:forEach>
</ul>