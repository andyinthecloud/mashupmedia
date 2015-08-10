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

<title><spring:message code="page.default.title.prefix" /> <spring:message
		code="login.title" /></title>
<link rel="icon" type="image/ico"
	href="<c:url value="/default/images/favicon.ico"/>">


<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet"
	href="<c:url value="/jquery-mobile/${jQueryMobileVersion}/jquery.mobile-${jQueryMobileVersion}.min.css" />" />
<script
	src="<c:url value="/jquery-mobile/${jQueryMobileVersion}/jquery-1.11.1.min.js" />"></script>
<script
	src="<c:url value="/jquery-mobile/${jQueryMobileVersion}/jquery.mobile-${jQueryMobileVersion}.min.js" />"></script>

<link href="<c:url value="${themePath}/stylesheets/site.css"/>"
	rel="stylesheet" type="text/css" />

<title><spring:message code="page.title.prefix" /> <spring:message
		code="login.title" /></title>
<!-- @LOGGED-OUT@ -->

<script type="text/javascript">
	function focusUsername() {
		document.getElementById("username").focus();
	}
</script>


</head>
<body onload="focusUsername()">
	<input type="hidden" id="logged-out" value="true" />

	<div id="login-panel" class="panel big-rounded-corners">

		<img alt="Mashup Media"
			src="<c:url value="/images/mashupmedia-logo.png"/>">

		<form method="POST" action="<%=request.getContextPath()%>/login">

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

			<table>
				<tbody>
					<tr>
						<td><label><spring:message code="login.username" /></label></td>
						<td><input type="text" value="" name="username" id="username"></td>
					</tr>
					<tr>
						<td><label><spring:message code="login.password" /></label></td>
						<td><input type="password" name="password"></td>
					</tr>
					<tr>
						<td colspan="2"><input type='checkbox' name='remember-me' />
							<spring:message code="login.remember-me" /></td>
					</tr>
					<tr>
						<td colspan="2" class="buttons"><input class="button"
							type="submit"
							value="<spring:message code="login.button.login" />"
							name="submit"></td>
					</tr>
				</tbody>
			</table>
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