<!DOCTYPE html> 

<%@page import="org.springframework.security.web.WebAttributes"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<html>
<head>
<link href="<c:url value="${themePath}/stylesheets/site.css"/>"
	rel="stylesheet" type="text/css" />

<title><spring:message code="page.title.prefix" /> <spring:message
		code="login.title" /></title>
<!-- @LOGGED-OUT@ -->




</head>
<body>

	<div id="login-panel" class="panel big-rounded-corners">

		<img alt="Mashup Media"
			src="<c:url value="/images/mashupmedia-logo.png"/>">

		<c:url var="rootUrl" value="/"/>
		<p><spring:message code="log-out.information" arguments="${rootUrl}"/></p>
	</div>

	<div id="footer">
		<div class="meta">
			<spring:message code="application.meta"
				arguments="${version},${currentYear}" />
		</div>
	</div>
</body>
</html>