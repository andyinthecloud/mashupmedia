<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>
<a class="home" href="<c:url value="/" />"><spring:message code="top-bar.home" /></a>

<ul class="main-menu">
	<li><a href="#">User</a></li>
	<li><a href="#"><spring:message code="top-bar.now-playing" /></a></li>
	<li><a href="#">Sunny</a></li>
	<li><a href="#"><spring:message code="top-bar.log-out" /></a></li>
</ul>