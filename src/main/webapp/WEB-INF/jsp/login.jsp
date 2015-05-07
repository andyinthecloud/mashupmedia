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