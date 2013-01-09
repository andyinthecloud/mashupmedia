<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="sub-panel">

			<ul class="main-menu">
				<c:forEach items="${listGroupsPage.groups}" var="user">
					<li><a
						href="<c:url value="/app/configuration/administration/edit-group/${user.id}" />"><c:out
								value="${group.name}" /></a></li>
				</c:forEach>
			</ul>

</div>

<div class="button-panel">
	<a class="button" href="<c:url value="/app/configuration/administration/add-user" />"><spring:message
			code="configuration.administration.list-users.add-user" /></a>
</div>