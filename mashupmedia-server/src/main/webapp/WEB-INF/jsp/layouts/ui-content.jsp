<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:if test="${fn:length(breadcrumbs) > 1 && !isAppend}">
	<div class="breadcrumbs">
		<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
			<c:choose>
				<c:when test="${status.last}">
					<span><c:out value="${breadcrumb.name}" /></span>
				</c:when>
				<c:otherwise>
					<span><a href="<c:url value="${breadcrumb.link}" />"
						rel="internal" title="${breadcrumb.name}"><c:out
								value="${breadcrumb.name}" /></a> &gt; </span>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</div>
</c:if>

<tiles:insertAttribute name="body" />
