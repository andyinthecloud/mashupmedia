<!DOCTYPE html>

<%@page import="org.springframework.security.web.WebAttributes"%>
<%@page import="org.mashupmedia.constants.MashUpMediaConstants"%>



<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<%
	request.setAttribute(MashUpMediaConstants.MODEL_KEY_JQUERY_MOBILE_VERSION,
			MashUpMediaConstants.JQUERY_MOBILE_VERSION);
	request.setAttribute(MashUpMediaConstants.MODEL_KEY_JQUERY_VERSION, MashUpMediaConstants.JQUERY_VERSION);
%>

<html>
<head>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="icon" type="image/ico"
	href="<c:url value="${themePath}/default/images/favicon.ico"/>">

<title><spring:message code="page.title.prefix" /> <spring:message
		code="login.title" /></title>

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

<script type="text/javascript">
$(document).ready(function() {
	$("#username").focus();
});
</script>


<!-- @LOGGED-OUT@ -->

</head>
<body>

	<input type="hidden" id="logged-out" value="true" />

	<div id="login-panel" class="panel">

		<img alt="Mashup Media"
			src="<c:url value="/images/mashupmedia-logo.png"/>">

		<form method="POST" action="<%=request.getContextPath()%>/login" data-ajax="false">

			<sec:csrfInput />

			<%
				if (session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null) {
			%>
			<div class="error-box">
				<spring:message code="login.error" />
			</div>
			
			<%
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
				}
			%>

			<div class="new-line">
				<label><spring:message code="login.username" /></label> <input
					type="text" value="" name="username" id="username" data-clear-btn="true" />
			</div>

			<div class="new-line">
				<label><spring:message code="login.password" /></label> <input
					type="password" name="password" data-clear-btn="true" />
			</div>

			<div class="new-line">
				<label> <input type="checkbox" name="remember-me" /> <spring:message
						code="login.remember-me" />
				</label>
			</div>

			<div class="new-line">
				<input class="button" type="submit"
					value="<spring:message code="login.button.login" />" name="submit">
			</div>


		</form>
	</div>

	<div id="footer">
		<div class="meta">
			<spring:message code="application.meta"
				arguments="${version},${currentYear}" />
		</div>
	</div>
</body>
</html>