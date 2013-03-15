<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

	$("div.check-location a").click(function() {
	    var path = $("#folderLocation-path").val();
	    if (path.length == 0) {
		return;
	    }

	    $.ajax({
		url : "<c:url value="/app/ajax/check-folder-location"/>",
		type : "post",
		data : {
		    path : path
		},
		success : function(data) {
		    var classStatus = "error";
		    if (data.response.isValid == 'true') {
			classStatus = "ok";
		    }
		    $("div.check-location .message").addClass(classStatus);
		    $("div.check-location .message").html(data.response.message);
		}
	    });
	});

    });
</script>

<form:form commandName="encodingPage">

	<div>
		<label class="new-line"><spring:message code="encoding.ffmpeg.path" /></label>
		<form:input path="ffmpegPath" />
	</div>
	
	<br />

	<div class="check-location">
		<a class="button" href="javascript:void(0);"><spring:message code="path.check" /></a> <span class="message horizontal-gap"></span>
	</div>


	<div class="button-panel">
		<input class="button" type="submit" value="<spring:message code="action.save"/>" />
	</div>

</form:form>
