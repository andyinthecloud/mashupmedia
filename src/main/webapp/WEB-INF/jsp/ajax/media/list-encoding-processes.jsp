<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<script type="text/javascript">
    $(document).ready(function() {
        $("#sortable").sortable({
            stop: function(event, ui) {
                var item = ui.item;
                var id = $(item).attr("id");
                var mediaItemId = parseId(id, "media-id");
                var mediaContentType = id.replace(/.*content-type-/g, "");
                var index = $(item).index();

                $.get("<c:url value="/app/encode/queue/move-process" />", {
                    index: index,
                    mediaItemId: mediaItemId,
                    mediaContentType: mediaContentType
                }, function(data) {
                    listProcesses();
                });

            }
        });
        $("#sortable").disableSelection();

    });
</script>



<c:choose>
	<c:when
		test="${fn:length(encodingProcessesPage.encodingProcesses) == 0}">
		<spring:message code="encoding-processes.empty" />
	</c:when>

	<c:otherwise>
		<ul class="items" id="sortable">
			<c:forEach items="${encodingProcessesPage.encodingProcesses}"
				var="encodingProcess">

				<c:choose>
					<c:when test="${empty encodingProcess.processStartedOn}">
						<spring:message var="processingMessage"
							code="encoding-processes.process.queued"
							arguments="${encodingProcess.mediaItem.fileName}, ${encodingProcess.mediaContentType.name}" />
					</c:when>
					<c:otherwise>
						<fmt:formatDate var="processStartedOnValue"
							value="${encodingProcess.processStartedOn}"
							pattern="HH:mm:ss dd/MM/yyyy" />
						<spring:message var="processingMessage"
							code="encoding-processes.process.encoding"
							arguments="${encodingProcess.mediaItem.fileName}, ${encodingProcess.mediaContentType.name}, ${processStartedOnValue}" />
					</c:otherwise>
				</c:choose>

				<li
					id="media-id-${encodingProcess.mediaItem.id}-content-type-${encodingProcess.mediaContentType.name}">

					<div class="item">
						<div class="meta">${processingMessage}</div>
					</div>

					<div class="icons-right">
						<img class="cursor-move"
							alt="<spring:message code="action.reorder"/>"
							title="<spring:message code="action.reorder"/>"
							src="<c:url value="${themePath}/images/controls/up-down.png"/>" />

						<a href="javascript:;" class="delete"><img
							alt="<spring:message code="action.playlist.item.remove"/>"
							title="<spring:message code="action.playlist.item.remove"/>"
							src="<c:url value="${themePath}/images/controls/delete.png"/>" /></a>
					</div>

				</li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>
