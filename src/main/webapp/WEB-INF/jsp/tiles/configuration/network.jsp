<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="panel">
	<form:form commandName="networkPage">

		<form:errors path="*" cssClass="error-box" />

		<fieldset>
			<legend>
				<spring:message code="network.proxy" />
			</legend>

			<form:radiobutton path="proxyEnabled" value="true" />
			<label for="proxyEnabled1"><spring:message code="network.proxy.enabled" /></label>
			<form:radiobutton path="proxyEnabled" value="false" />
			<label for="proxyEnabled2"><spring:message code="network.proxy.disabled" /></label><br /> <label><spring:message
					code="network.proxy.url" /></label>
			<form:input path="proxyUrl" />
			<br /> <label><spring:message code="network.proxy.port" /></label>
			<form:input path="proxyPort" />
			<br /> <label><spring:message code="network.proxy.username" /></label>
			<form:input path="proxyUsername" />
			<br /> <label><spring:message code="network.proxy.password" /></label>
			<form:password path="proxyPassword" />
			<br />
		</fieldset>

		<input type="submit" value="<spring:message code="action.save"/>" />


	</form:form>
</div>
