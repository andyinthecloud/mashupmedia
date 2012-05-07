<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/inc/jquery.jsp" />


<link href="<c:url value="/stylesheets/site.css"/> rel=" stylesheet" type="text/css" />


<title><tiles:getAsString name="title" /></title>

</head>

<body>

	<div class="panel breadcrumbs">
		<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
			<span> <c:choose>
					<c:when test="${status.last}">
						<c:out value="${breadcrumb.name}" />
					</c:when>

					<c:otherwise>
						<a href="<c:url value="${breadcrumb.link}" />"><c:out value="${breadcrumb.name}" /></a> &gt;
					</c:otherwise>

				</c:choose>
			</span>
		</c:forEach>

	</div>

	<tiles:insertAttribute name="body" />

</body>

</html>
