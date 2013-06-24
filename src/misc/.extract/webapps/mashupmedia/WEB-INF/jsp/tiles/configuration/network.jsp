<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		processForm();
		$("input[type='radio'][name='proxyEnabled']").change(function() {
			processForm();
		});
	});

	function processForm() {
		var isDisabled = false;
		if ($("input[type='radio'][name='proxyEnabled']:checked").val() == "false") {			
			isDisabled = true;
		}
	
		var fieldSelector = "input[type='text'], input[type='password']";
		
		$(fieldSelector).prop("disabled", isDisabled);

		if (isDisabled) {
			$(fieldSelector).addClass("disabled");
		} else {
			$(fieldSelector).removeClass("disabled");
		}
		
	}
</script>

<form:form commandName="networkPage">

	<form:errors path="*" cssClass="error-box" />


	<form:radiobutton path="proxyEnabled" value="true" />
	<label for="proxyEnabled1"><spring:message code="network.proxy.enabled" /></label>
	<form:radiobutton path="proxyEnabled" value="false" />
	<label for="proxyEnabled2"><spring:message code="network.proxy.disabled" /></label>
	<br />
	<label class="new-line"><spring:message code="network.proxy.url" /></label>
	<form:input path="proxyUrl" />
	<br />
	<label class="new-line"><spring:message code="network.proxy.port" /></label>
	<form:input path="proxyPort" />
	<br />
	<label class="new-line"><spring:message code="network.proxy.username" /></label>
	<form:input path="proxyUsername" />
	<br />
	<label class="new-line"><spring:message code="network.proxy.password" /></label>
	<form:password path="proxyPassword" />

	<div class="button-panel">
		<input class="button" type="submit" value="<spring:message code="action.save"/>" />
	</div>


</form:form>
