<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<ul class="main-menu">
	<li><a href="<c:url value="/app/videos" />"><spring:message code="home.links.videos"/></a></li>
	<li><a href="<c:url value="/app/music#address-random-albums" />"><spring:message code="home.links.music"/></a></li>
	<li><a href="<c:url value="/app/photos" />"><spring:message code="home.links.photos"/></a></li>
	<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
		<li><a href="<c:url value="/app/configuration" />"><spring:message code="home.links.configuration" /></a></li>
	</sec:authorize>
</ul>
