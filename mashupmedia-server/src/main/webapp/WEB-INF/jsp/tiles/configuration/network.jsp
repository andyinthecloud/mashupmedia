<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
        processForm();

        $("div.ui-content form").on("change", "#proxyEnabled", function() {
            processForm($(this));
        });

        $("#networkPage").submit(function(event) {
            event.preventDefault();
            submitAjaxForm($(this), "<spring:message code ="configuration.title" />", "<c:url value="/configuration" />");
        });

    });

    function processForm(checkbox) {
        var isDisabled = false;

        if ($(checkbox).val() == "false") {
            isDisabled = true;
        }

        var fieldContainerElement = $("input[type='text'], input[type='password']").closest("div");

        if (isDisabled) {
            $(fieldContainerElement).addClass("ui-state-disabled");
        } else {
            $(fieldContainerElement).removeClass("ui-state-disabled");
        }
    }
</script>


<form:form modelAttribute="networkPage">

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

