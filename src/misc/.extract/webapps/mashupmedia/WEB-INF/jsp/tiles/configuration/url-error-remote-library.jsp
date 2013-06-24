<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    var remoteLibraryMessage = "<spring:message code="configuration.list-remote-libraries.text" />";

    $(document).ready(function() {

	$("#new-remote-library-container input[type=text]").val(remoteLibraryMessage);

	$("#new-remote-library-container input[type=text]").focus(function() {
	    var remoteLibraryUrl = $(this).val();
	    if (remoteLibraryUrl == remoteLibraryMessage) {
		$(this).val("");
	    }
	});

	$("#new-remote-library-container input[type=text]").blur(function() {
	    var remoteLibraryUrl = $(this).val();
	    if (remoteLibraryUrl == "") {
		$(this).val(remoteLibraryMessage);
	    }
	});

	$("#new-remote-library-container input[type=button]").click(function() {
	    var remoteLibraryUrl = $("#new-remote-library-container input[type=text]").val();
	    remoteLibraryUrl = encodeURIComponent(remoteLibraryUrl);
	    window.location.href = "<c:url value="/app/configuration/new-remote-library" />?remoteLibraryUrl=" + remoteLibraryUrl;
	});
    });
</script>

<p>
	<spring:message code="configuration.url-error-remote-library.information" />
</p>

<div class="textfield-with-button-panel" id="new-remote-library-container">
	<input type="text" /> <input type="button" class="button" value="<spring:message code="configuration.url-error-remote-library.add-remote-library" />" />
</div>
