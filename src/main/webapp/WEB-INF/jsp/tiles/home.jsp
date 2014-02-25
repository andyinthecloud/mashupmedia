<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<ul class="main-menu">
	<li><a href="#"><spring:message code="home.links.videos"/></a></li>
	<li><a href="<c:url value="/app/music#address-random-albums" />"><spring:message code="home.links.nusic"/></a></li>
	<sec:authorize ifAllGranted="ROLE_ADMINISTRATOR">
		<li><a href="<c:url value="/app/configuration" />"><spring:message code="home.links.configuration" /></a></li>
	</sec:authorize>
</ul>
