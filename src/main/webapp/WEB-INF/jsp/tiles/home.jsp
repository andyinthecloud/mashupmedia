<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="panel main-menu-panel">
	<ul>
		<li><a href="#">Movies</a></li>
		<li><a href="#">Radio</a></li>
		<li><a href="<c:url value="/app/music" />">Music</a></li>
		<li><a href="<c:url value="/app/configuration" />"><spring:message
					code="home.links.configuration" /></a></li>
	</ul>
</div>