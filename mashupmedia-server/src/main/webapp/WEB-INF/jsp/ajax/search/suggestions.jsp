<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

[
<c:forEach items="${suggestions}" var="suggestion" varStatus="status">

	<c:if test="${!status.first}">,</c:if>

	{
	    "suggestion" : "${suggestion}"
	}

</c:forEach>
]