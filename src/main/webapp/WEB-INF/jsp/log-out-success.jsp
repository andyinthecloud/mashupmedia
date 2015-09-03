<!DOCTYPE html> 

<%@page import="org.springframework.security.web.WebAttributes"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<html>
<head>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="icon" type="image/ico"
	href="<c:url value="${themePath}/default/images/favicon.ico"/>">

<link rel="stylesheet"
	href="<c:url value="/jquery-mobile/${jQueryMobileVersion}/jquery.mobile-${jQueryMobileVersion}.min.css" />" />
<script
	src="<c:url value="/jquery/${jQueryVersion}/jquery-${jQueryVersion}.min.js" />"></script>
<script
	src="<c:url value="/jquery-mobile/${jQueryMobileVersion}/jquery.mobile-${jQueryMobileVersion}.min.js" />"></script>

<link href="<c:url value="${themePath}/stylesheets/site.css"/>"
	rel="stylesheet" type="text/css" />
<link href="<c:url value="${themePath}/stylesheets/site-desktop.css"/>"
	rel="stylesheet" type="text/css" />




<title><spring:message code="page.title.prefix" /> <spring:message
		code="log-out.title" /></title>
<!-- @LOGGED-OUT@ -->




</head>
<body>

	<div id="login-panel" class="panel">

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