<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

    });
</script>

<form:form commandName="encodingPage">

	<div>
		<p>
			<spring:message code="encoding.explanation" />
		</p>
		<p>
			<spring:message code="encoding.instructions" arguments="${encodingPage.ffmpegFolderPath}" />
		</p>
	</div>

	<br />

	<spring:message var="ffmpegStatusText" code="encoding.ffmpeg.path.invalid" />
	<c:if test="${encodingPage.isFfmpegFound}">
		<spring:message var="ffmpegStatusText" code="encoding.ffmpeg.path.valid" />
	</c:if>

	<c:if test="${!empty encodingPage.additionalErrorMessage}">
		<c:set var="ffmpegStatusText" value="${encodingPage.additionalErrorMessage}" />
	</c:if>


	<div class="status-message">
		${ffmpegStatusText}
	</div>


	<div class="button-panel">
		<a class="button" href="<c:url value="/app/configuration/encoding" />"><spring:message code="action.refresh"/></a>
	</div>

</form:form>
