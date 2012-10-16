<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

{"response" : 
	{ 
	"isSuccessful": "${isSuccessful}",
	"message": "<spring:message code="${messageCode}"/>"
	}
}