<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
	$(document)
			.ready(
					function() {
						var locationType = $(
								"#location input:radio[name=locationType]:checked").val();
						showLocation(locationType);

						$("#location input[name='locationType']").click(
								function() {
									var locationType = $(this).attr("value");
									showLocation(locationType);
								});

						$("#location div.check-location a")
								.click(
										function() {
											var locationType = $(
													"#location input[name='locationType']:checked")
													.val();
											if (locationType == 'folder') {
												checkFolderLocationPath();
											} else if (locationType == 'ftp') {
												checkFtpLocationPath();
											}

										});
						
						$("#musicLibraryPage input:submit").click(
								function() {
									var action = $(this).attr("name");
									$("#action").val(action);
								});						
					});

	function checkFolderLocationPath() {
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
				$("#location div.check-location .message")
						.addClass(classStatus);
				$("#location div.check-location .message").html(
						data.response.message);
			}
		});
	}

	function checkFtpLocationPath() {
		var host = $("#ftpLocation-host").val();
		var port = $("#ftpLocation-port").val();
		var path = $("#ftpLocation-path").val();
		var username = $("#ftpLocation-username").val();
		var password = $("#ftpLocation-password").val();

		if (host.length == 0) {
			return;
		}

		$.ajax({
			url : "<c:url value="/app/ajax/check-ftp-location"/>",
			type : "post",
			data : {
				host : host,
				port : port,
				location : path,
				username : username,
				password : password
			},
			success : function(data) {
				var classStatus = "error";
				if (data.response.isValid == 'true') {
					classStatus = "ok";
				}
				$("#location div.check-location .message")
						.addClass(classStatus);
				$("#location div.check-location .message").html(
						data.response.message);
			}
		});

	}

	function showLocation(locationType) {
		if (locationType == 'ftp') {
			$("#location div.folder").hide();
			$("#location div.ftp").show();

		} else {
			$("#location div.ftp").hide();
			$("#location div.folder").show();
		}
	}
</script>

<div class="panel">

	<form:form commandName="musicLibraryPage">
		<form:errors path="*" cssClass="error-box" />
		<form:hidden path="action" />
		<form:hidden path="musicLibrary.id" />
		<form:checkbox path="musicLibrary.enabled" id="musicLibrary-enabled" />
		<label for="musicLibrary-enabled"><spring:message code="musiclibrary.enabled" /></label>
		<br />

		<label for="musicLibrary-name"><spring:message code="musiclibrary.name" /></label>
		<form:input path="musicLibrary.name" id="musicLibrary-name" />
		<br />



		<fieldset id="location">
			<legend>
				<spring:message code="musiclibrary.location" />
			</legend>

			<form:radiobutton path="locationType" value="folder" />
			<label for="locationType1"><spring:message code="musiclibrary.location.type.folder" /></label>
			<form:radiobutton path="locationType" value="ftp" />
			<label for="locationType2"><spring:message code="musiclibrary.location.type.ftp" /></label> <br />

			<div class="folder">
				<label for="folderLocation-path"><spring:message code="musiclibrary.location.path" /></label>
				<form:input path="folderLocation.path" id="folderLocation-path" />
			</div>

			<div class="ftp">
				<label for="ftpLocation-host"><spring:message code="musiclibrary.location.host" /></label>
				<form:input path="ftpLocation.host" id="ftpLocation-host" />
				<br /> <label for="ftpLocation-port"><spring:message code="musiclibrary.location.port" /></label>
				<form:input path="ftpLocation.port" id="ftpLocation-port" />
				<br /> <label for="ftpLocation-path"><spring:message code="musiclibrary.location.path" /></label>
				<form:input path="ftpLocation.path" id="ftpLocation-path" />
				<br /> <label for="ftpLocation-username"><spring:message
						code="musiclibrary.location.username" /></label>
				<form:input path="ftpLocation.username" id="ftpLocation-username" />
				<br /> <label for="ftpLocation-password"><spring:message
						code="musiclibrary.location.password" /></label>
				<form:password path="ftpLocation.password" id="ftpLocation-password" />
			</div>
			<div class="check-location">
				<span class="message"></span> <a href="javascript:void(0);"><spring:message
						code="musiclibrary.location.path.check" /></a>
			</div>
		</fieldset>

		<label for="musicLibrary-groups"><spring:message code="musiclibrary.groups" /></label>
		<form:select path="musicLibrary.groups" id="musicLibrary-groups" multiple="true">
			<form:options items="${groups}" itemLabel="translatedName" itemValue="idName" />
		</form:select>
		<br />


		<label for="musicLibrary-scanMinutesInterval"><spring:message
				code="musiclibrary.scanminutesinterval" /></label>
		<form:input path="musicLibrary.scanMinutesInterval" />
		<br />

		<div class="buttons">
		<input name="save" type="submit" value="<spring:message code="action.save" />" />
		<input name="delete" type="submit" value="<spring:message code="action.delete" />" />
		</div>
	</form:form>

</div>