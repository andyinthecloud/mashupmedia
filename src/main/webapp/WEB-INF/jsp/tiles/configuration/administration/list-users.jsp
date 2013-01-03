<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="sub-panel">

			<ul class="main-menu">
				<c:forEach items="${listUsersPage.users}" var="user">
					<li><a
						href="<c:url value="/app/configuration/administration/edit-user/${user.id}" />"><c:out
								value="${user.name}" /></a></li>
				</c:forEach>
			</ul>

</div>

<div class="button-panel">
	<a class="button" href="<c:url value="/app/configuration/administration/new-user" />"><spring:message
			code="configuration.administration.list-users.add-user" /></a>
</div>