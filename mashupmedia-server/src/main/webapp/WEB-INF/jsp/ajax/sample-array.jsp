<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

{"keywords" : [
<c:forEach items="${keywords}" var="keyword" varStatus="status">

	<c:if test="${!status.first}">,</c:if>

	{
	    "id": "<c:out value="${keyword.id}" />",
	    "code": "<c:out value="${keyword.code}" />",
	    "description": "<c:out value="${keyword.description}" />"
	}

</c:forEach>
]}