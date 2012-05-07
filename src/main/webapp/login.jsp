<html>
<head>
<title>Login Page</title>
</head>
<body >
	<h1>Login with Username and Password</h1>
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
</body>
</html>