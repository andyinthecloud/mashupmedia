<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

{"mediaItem" : { 
	"id": "${mediaItem.id}",
	"format": "${mediaItem.format}",
	"jPlayerFormat": "${jPlayerFormat}",
	"fileName": "${mediaItem.fileName}"
	}
}