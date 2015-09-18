<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:if test="${fn:length(breadcrumbs) > 1}">
	<div class="breadcrumbs">
		<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
			<span> <c:choose>
					<c:when test="${status.last}">
						<c:out value="${breadcrumb.name}" />
					</c:when>

					<c:otherwise>
						<a href="<c:url value="${breadcrumb.link}" />" rel="internal"
							title="${breadcrumb.name}"><c:out value="${breadcrumb.name}" /></a> &gt;
							</c:otherwise>
				</c:choose>
			</span>
		</c:forEach>
	</div>
</c:if>

<tiles:insertAttribute name="body" />
