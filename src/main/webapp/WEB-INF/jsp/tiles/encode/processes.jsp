<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="sub-panel">

	<c:choose>
		<c:when test="${fn:length(encodingProcessesPage.encodingProcesses) == 0}">
			<spring:message code="encoding-processes.empty" />
		</c:when>

		<c:otherwise>
			<ul class="main-menu">
				<c:forEach items="${encodingProcessesPage.encodingProcesses}" var="encodingProcess">
					<fmt:formatDate var="startedOnValue" value="${encodingProcess.mediaItem.displayTitle}" pattern="HH:mm:ss dd/MM/yyyy"/>
					<li><a 
						href="<c:url value="/app/encode/process/" />"><spring:message code="encoding-processes.process" arguments="${encodingProcess.mediaItem.displayTitle}, ${encodingProcess.displayText}, ${startedOnValue}"/></a></li>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
</div>