<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

{"albums" : [
<c:forEach items="${albums}" var="album" varStatus="status">

	<c:if test="${!status.first}">,</c:if>

	{
	    "id": "<c:out value="${album.id}" />",
	    "albumName": "<c:out value="${album.name}" />",
	    "artistName": "<c:out value="${album.artist.name}" />"	    
	}

</c:forEach>
]}