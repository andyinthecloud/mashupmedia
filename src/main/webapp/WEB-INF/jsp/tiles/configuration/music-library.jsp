<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
    $(document).ready(function() {
	var locationType = $("#location input:radio[name=locationType]:checked").val();

	$("#location input[name='locationType']").click(function() {
	    var locationType = $(this).attr("value");
	    showLocation(locationType);
	});

	$("#location div.check-location a").click(function() {
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
			$("#location div.check-location .message").addClass(classStatus);
			$("#location div.check-location .message").html(data.response.message);
		    }
		});	    
	    
	   

	});

	$("#musicLibraryPage input:submit").click(function() {
	    var action = $(this).attr("name");
	    $("#action").val(action);
	});
    });

   


</script>


<form:form commandName="musicLibraryPage">
	<form:errors path="*" cssClass="error-box" />
	<form:hidden path="action" />
	<form:hidden path="musicLibrary.id" />

	<label for="musicLibrary-name"><spring:message code="musiclibrary.name" /></label>
	<form:input path="musicLibrary.name" id="musicLibrary-name" cssStyle="margin-bottom: 10px;" />
	<br />

	<form:checkbox path="musicLibrary.enabled" id="musicLibrary-enabled" cssStyle="vertical-align: middle;" />
	<label for="musicLibrary-enabled"><spring:message code="musiclibrary.enabled" /></label>
	<br />



	<fieldset id="location" style="">
		<legend>
			<spring:message code="musiclibrary.location" />
		</legend>


		<div class="folder">
			<label class="new-line" for="folderLocation-path"><spring:message code="musiclibrary.location.path" /></label>
			<form:input path="folderLocation.path" id="folderLocation-path" />
		</div>


		<br />

		<div class="check-location">
			<a class="button" href="javascript:void(0);"><spring:message code="path.check" /></a> <span class="message horizontal-gap"></span>
		</div>
	</fieldset>



	<label class="new-line" for="groups"><spring:message code="musiclibrary.groups" /></label>
	<form:checkboxes path="musicLibrary.groups" items="${groups}" itemLabel="name" itemValue="id" cssClass="checkboxes" delimiter="<br/>" />
	<br />

	<label class="new-line" for="musicLibrary-scanMinutesInterval"><spring:message code="musiclibrary.scanminutesinterval" /></label>
	<form:input path="musicLibrary.scanMinutesInterval" />
	<br />

	<div class="button-panel">
		<input class="button" name="save" type="submit" value="<spring:message code="action.save" />" /> <input class="button" name="delete" type="submit" value="<spring:message code="action.delete" />" />
	</div>
</form:form>

