<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

    });
</script>

<form:form commandName="encodingPage">


	<form:errors path="*" cssClass="error-box" />

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

	
	<fieldset>
	<label title="<spring:message code="encoding.processes.total.tip"/>"><spring:message code="encoding.processes.total"/></label>
	<form:input path="totalFfmpegProcesses" cssClass="small-inline"/>
	</fieldset>

	<div class="button-panel">
		<input class="button" type="submit" value="<spring:message code="action.reload"/>"/>
	</div>

</form:form>
