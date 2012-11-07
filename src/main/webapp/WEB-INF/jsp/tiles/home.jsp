<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

	<ul class="main-menu">
		<li><a href="#">Movies</a></li>
		<li><a href="#">Radio</a></li>
		<li><a href="<c:url value="/app/music#address-random-albums" />">Music</a></li>
		<li><a href="<c:url value="/app/configuration" />"><spring:message
					code="home.links.configuration" /></a></li>
	</ul>
