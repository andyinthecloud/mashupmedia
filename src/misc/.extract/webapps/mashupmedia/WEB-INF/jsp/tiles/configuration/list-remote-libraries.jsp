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
	    if (remoteLibraryUrl == remoteLibraryMessage) {
		remoteLibraryUrl = "";
	    }
	    remoteLibraryUrl = encodeURIComponent(remoteLibraryUrl);
	    window.location.href = "<c:url value="/app/configuration/new-remote-library" />?remoteLibraryUrl=" + remoteLibraryUrl;
	});
    });
</script>


<div class="sub-panel">

	<ul class="main-menu">
		<c:forEach items="${listRemoteLibrariesPage.remoteLibraries}" var="library">
			<li><a href="<c:url value="/app/configuration/edit-remote-library?libraryId=${library.id}" />"><c:out value="${library.name}" /></a></li>
		</c:forEach>
	</ul>

</div>

<div class="textfield-with-button-panel" id="new-remote-library-container">
	<input type="text" class="inline" /> <input class="button" type="button" value="<spring:message code="configuration.list-remote-libraries.add-remote-library" />" />
</div>