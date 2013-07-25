<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.springframework.security.web.WebAttributes"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<html>
<head>
<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />

<title><spring:message code="page.title.prefix" /> <spring:message code="login.title" /></title>
<!-- @LOGGED-OUT@ -->

<script type="text/javascript">
    function focusUsername() {
	document.getElementById("j_username").focus();
    }
</script>


</head>
<body onload="focusUsername()">
	<input type="hidden" id="logged-out" value="true" />

	<div id="login-panel" class="panel big-rounded-corners">

		<img alt="Mashup Media" src="<c:url value="/images/mashupmedia-logo.png"/>">

		<form method="POST" action="<%=request.getContextPath()%>/j_spring_security_check">

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
						<td><input type="text" value="" name="j_username" id="j_username"></td>
					</tr>
					<tr>
						<td><label><spring:message code="login.password" /></label></td>
						<td><input type="password" name="j_password"></td>
					</tr>
					<tr>
						<td colspan="2" class="buttons"><input class="button" type="submit" value="<spring:message code="login.button.login" />" name="submit"></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>

	<div id="footer">
		<div class="meta">
			<spring:message code="application.meta" arguments="${version},${currentYear}" />
		</div>
	</div>
</body>
</html>