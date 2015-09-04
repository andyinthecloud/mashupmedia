<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		processForm();
		$("#proxyEnabled").change(function() {
			processForm();
		});

		$("#networkPage").submit(function(event) {
			event.preventDefault();
			var formAction = $(this).attr("action");
			var formData = $(this).serialize();			
			$.post(formAction, formData, function(data) {
				$("div.ui-content").html(data);
			});

		});

	});

	function processForm() {
		var isDisabled = false;

		if ($("#proxyEnabled").val() == "false") {
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

	<div class="new-line">
		<label for="proxyEnabled"><spring:message
				code="network.proxy.enable" /></label>
		<form:select path="proxyEnabled" data-role="slider">
			<form:option value="true">
				<spring:message code="yes" />
			</form:option>
			<form:option value="false">
				<spring:message code="no" />
			</form:option>
		</form:select>
	</div>

	<div class="new-line">
		<label><spring:message code="network.proxy.url" /></label>
		<form:input path="proxyUrl" data-clear-btn="true" />
	</div>

	<div class="new-line">
		<label><spring:message code="network.proxy.port" /></label>
		<form:input path="proxyPort" data-clear-btn="true" />
	</div>

	<div class="new-line">
		<label><spring:message code="network.proxy.username" /></label>
		<form:input path="proxyUsername" data-clear-btn="true" />
	</div>

	<div class="new-line">
		<label><spring:message code="network.proxy.password" /></label>
		<form:password path="proxyPassword" data-clear-btn="true" />
	</div>

	<div class="new-line">
		<input class="button" type="submit"
			value="<spring:message code="action.save"/>" />
	</div>

</form:form>

