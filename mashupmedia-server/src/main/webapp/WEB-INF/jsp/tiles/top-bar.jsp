<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>
<div id="top-bar" class="top-bar">


	<a class="home" href="<c:url value="/" />"><spring:message code="top-bar.home" /></a>

	<ul class="main-menu">
		<li><a href="<c:url value="/music" />"><spring:message code="top-bar.music" /></a></li>
		<li><a href="<c:url value="/videos" />"><spring:message code="top-bar.videos" /></a></li>
		<li><a href="<c:url value="/photo/list" />"><spring:message
					code="top-bar.photos" /></a></li>		
		<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">		
		<li><a href="<c:url value="/encode/processes" />"><spring:message code="top-bar.encoding.queue" /></a></li>
		</sec:authorize>
		<li><a href="<c:url value="/configuration/administration/account" />"><spring:message code="top-bar.my-account" /></a></li>
		<li><a id="log-out" href="#" id="log-out"><spring:message code="top-bar.log-out" /></a></li>
		<li><a href="http://www.mashupmedia.org" target="_blank"><img title="Mashup Media"
				src="<c:url value="/images/mashupmedia-logo-inline.png" />" /></a></li>
		<c:if test="${isNewMashupMediaVersionAvailable}">
			<li class="update-available ui-corner-all"><a href="http://www.mashupmedia.org/download" target="_blank" title="<spring:message code="top-bar.new-update.message" />"><span class="ui-icon ui-icon-circle-arrow-s"></span></a></li>
		</c:if>

	</ul>

</div>