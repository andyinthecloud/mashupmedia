<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
        $("#encodingPage").submit(function(event) {
            event.preventDefault();
            submitAjaxForm($(this), "<spring:message code ="encoding.title" />", "<c:url value="/app/configuration/encoding" />");
        });
    });
</script>

<form:form commandName="encodingPage">


	<form:errors path="*" cssClass="error-box" />

	<spring:message var="ffmpegStatusText"
		code="encoding.ffmpeg.path.invalid" />
	<c:if test="${encodingPage.isFfmpegFound}">
		<spring:message var="ffmpegStatusText"
			code="encoding.ffmpeg.path.valid" />
	</c:if>

	<c:if test="${!empty encodingPage.additionalErrorMessage}">
		<c:set var="ffmpegStatusText"
			value="${encodingPage.additionalErrorMessage}" />
	</c:if>


	<div class="status-message">${ffmpegStatusText}</div>

	<div>
		<p>
			<spring:message code="encoding.explanation" />
		</p>
		<p>
			<spring:message code="encoding.instructions"
				arguments="${encodingPage.ffmpegFolderPath}" />
		</p>
	</div>

	<div class="new-line">
		<input class="button" type="submit"
			value="<spring:message code="action.refresh"/>" />
	</div>

	<fieldset>
		<div class="new-line">
			<label title="<spring:message code="encoding.processes.total.tip"/>"><spring:message
					code="encoding.processes.total" /></label>
			<form:input path="totalFfmpegProcesses" cssClass="small-inline" />
		</div>
	</fieldset>

	<div class="new-line">
		<input class="button" type="submit"
			value="<spring:message code="action.save"/>" />
	</div>

</form:form>
