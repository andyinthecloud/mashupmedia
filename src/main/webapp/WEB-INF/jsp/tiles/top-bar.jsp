<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>
<div id="top-bar" class="top-bar">

	
	<a class="home" href="<c:url value="/" />" target="_blank" ><spring:message code="top-bar.home" /></a>

	<ul class="main-menu">
		<li><a href="<c:url value="/app/configuration/administration/account" />"><spring:message code="top-bar.my-account" /></a></li>
		<li><a href="<c:url value="/j_spring_security_logout" />"><spring:message code="top-bar.log-out" /></a></li>
		<li><a href="http://www.mashupmedia.org" target="_blank"><img title="Mashup Media" src="<c:url value="/images/mashupmedia-logo-inline.png" />" /></a></li>
	</ul>



</div>