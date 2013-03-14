<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
	});

</script>

<form:form commandName="encodingPage">

	<label class="new-line"><spring:message code="encoding.ffmpeg.path" /></label>
	<form:input path="ffmpegPath" />

	<div class="button-panel">
		<input class="button" type="submit" value="<spring:message code="action.save"/>" />
	</div>

</form:form>
