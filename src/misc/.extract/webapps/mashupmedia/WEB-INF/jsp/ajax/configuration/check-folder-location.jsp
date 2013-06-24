<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

{"response" : { 
	"isValid": "<c:out value="${isValid}" />",
	"message": "<spring:message code="${messageCode}"/>"
}
}