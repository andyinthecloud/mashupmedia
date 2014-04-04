<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("#sortable").sortable({
			stop : function(event, ui) {
				var item = ui.item;
				var id = $(item).attr("id");
				var mediaItemId = parseId(id, "media-id");
				var mediaContentType = id.replace(/.*content-type-/g, "");
				var index = $(item).index();

				$.get("<c:url value="/app/encode/processes/move-process" />", {
					index : index,
					mediaItemId : mediaItemId,
					mediaContentType : mediaContentType
				}, function(data) {
					listProcesses();
				});

			}
		});
		$("#sortable").disableSelection();

	});
</script>


<c:choose>
	<c:when test="${fn:length(encodingProcessesPage.encodingProcesses) == 0}">
		<spring:message code="encoding-processes.empty" />
	</c:when>

	<c:otherwise>
		<ul class="items cursor-move" id="sortable">
			<c:forEach items="${encodingProcessesPage.encodingProcesses}" var="encodingProcess">

				<c:choose>
					<c:when test="${empty encodingProcess.processStartedOn}">
						<spring:message var="processingMessage" code="encoding-processes.process.queued"
							arguments="${encodingProcess.mediaItem.fileName}, ${encodingProcess.mediaContentType.name}" />
					</c:when>
					<c:otherwise>
						<fmt:formatDate var="processStartedOnValue" value="${encodingProcess.processStartedOn}" pattern="HH:mm:ss dd/MM/yyyy" />
						<spring:message var="processingMessage" code="encoding-processes.process.encoding"
							arguments="${encodingProcess.mediaItem.fileName}, ${encodingProcess.mediaContentType.name}, ${processStartedOnValue}" />
					</c:otherwise>
				</c:choose>

				<li id="media-id-${encodingProcess.mediaItem.id}-content-type-${encodingProcess.mediaContentType.name}"><a class="ui-icon"
					href="javascript:void(0);" title="<spring:message code="encoding-processes.process.delete" />"><span class="ui-icon ui-icon-trash"></span></a>
					${processingMessage}</li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>