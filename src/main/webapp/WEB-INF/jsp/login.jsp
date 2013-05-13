<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<html>
<head>
<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />


<title><spring:message code="page.title.prefix" /> <spring:message code="login.title" /></title>



<!-- @LOGGED-OUT@ -->

</head>
<body>
	<input type="hidden" id="logged-out" value="true" />
	
	<div class="login-panel panel">	
	
	<img alt="Mashup Media" src="<c:url value="/images/mashupmedia-logo.png"/>">
	
	
	<form method="POST" action="<%=request.getContextPath()%>/j_spring_security_check" name="f">
		<table>
			<tbody>
				<tr>
					<td>User:</td>
					<td><input type="text" value="" name="j_username"></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type="password" name="j_password"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="Login" name="submit"></td>
				</tr>
			</tbody>
		</table>
	</form>
	</div>
</body>
</html>