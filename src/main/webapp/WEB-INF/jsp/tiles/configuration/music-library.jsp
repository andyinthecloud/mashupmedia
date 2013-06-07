<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
    $(document).ready(function() {
	var locationType = $("#location input:radio[name=locationType]:checked").val();

	$("#location input[name='locationType']").click(function() {
	    var locationType = $(this).attr("value");
	    showLocation(locationType);
	});
	
	$("#remote-share-panel").on("click", "input.link", function() {
		$(this).select();
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

	showRemoteShares();

	$("#musicLibraryPage input:submit").click(function() {
	    var action = $(this).attr("name");
	    $("#action").val(action);
	});
	
	$("#remote-share-panel").hide();
	
	$("#remote-share").click(function() {
	    $("#remote-share-panel").toggle();
	});
	
	$("#create-remote-link").click(function() {
		$.ajax({
			url : "<c:url value="/app/ajax/library/add-remote-share"/>",
			type : "post",
			data : {
			    libraryId : ${musicLibraryPage.musicLibrary.id}
			},
			success : function(data) {
			    showRemoteShares();
			}
	    });	    
	});
	
	$("#save-library-remote-connections").click(function() {
		var remoteShareIds = new Array();
		$("#remote-share-panel > table > tbody > tr").each(function() {
		    if ($(this).find("input[type='checkbox']").is(":checked")) {
				var remoteShareId = $(this).attr("id");
				remoteShareId = parseId(remoteShareId, "remote-share-");
				remoteShareIds.push(remoteShareId);					    
			}		    
		});
		
		if (remoteShareIds.length == 0) {
			return;
		}
		
		var remoteShareStatus = $("#library-remote-connection-action").val();
		if (remoteShareStatus == "") {
			return;
		}
		
		
		$.ajax({
			url : "<c:url value="/app/ajax/library/update-remote-shares"/>",
			type : "post",
			data : {
				remoteShareIds : remoteShareIds,
			    libraryId : ${musicLibraryPage.musicLibrary.id},
			    remoteShareStatus : remoteShareStatus
			},
			success : function(data) {
			    showRemoteShares();
			}
	    });
		
		
		
	})
	
	
    });
    
    function showRemoteShares() {
   	<c:if test="${musicLibraryPage.isExists}">
   	$.ajax({
   		url : "<c:url value="/app/ajax/library/get-remote-shares"/>",
   		type : "get",
   		data : {
   		    libraryId : ${musicLibraryPage.musicLibrary.id}
   		},
   		success : function(data) {
   		       		    
   		    if (data.length == 0) {
   			 	$("#remote-share-panel table tbody").html("");
   				return;
   		    }
   		    
   		    if ($("#remote-share-panel").is(":hidden")) {
   	   		    $("#remote-share").click();   		    	
   		    }
   		    
   		    var remoteShareHtml = "";
   		    
   		    $.each(data, function(i, item) {
   		    	var remoteShare = item.remoteShare;
   			    remoteShareHtml += "<tr id=\"remote-share-" + remoteShare.id + "\">";
   			    remoteShareHtml += "<td><input type=\"checkbox\" /></td>";
   			    var link = getHostUrl() + "app/remote-library/" + remoteShare.uniqueName;
   			    remoteShareHtml += "<td><input class=\"link\" type=\"text\" value=\"" + link + "\" /></td>";
   			    remoteShareHtml += "<td>" + remoteShare.remoteUrl + "</td>";
   			    remoteShareHtml += "<td>" + remoteShare.createdOn + "</td>";
   			    remoteShareHtml += "<td>" + remoteShare.lastAccessed + "</td>";
   			    remoteShareHtml += "<td>" + remoteShare.totalPlayedMediaItems + "</td>";
   			    remoteShareHtml += "<td>" + remoteShare.status + "</td>";			    
   			    remoteShareHtml += "</tr>";			    
   		    });
   		    
   		    $("#remote-share-panel table tbody").html(remoteShareHtml);
   		    
   		}
       });
   	</c:if>	
    }
    
</script>


<form:form commandName="musicLibraryPage">
	<form:errors path="*" cssClass="error-box" />
	<form:hidden path="action" />
	<form:hidden path="musicLibrary.id" />
	<form:hidden path="musicLibrary.scanMinutesInterval" />

	<label for="musicLibrary-name"><spring:message code="musiclibrary.name" /></label>
	<form:input path="musicLibrary.name" id="musicLibrary-name" cssStyle="margin-bottom: 10px;" />
	<br />

	<form:checkbox path="musicLibrary.enabled" id="musicLibrary-enabled" cssStyle="vertical-align: middle;" />
	<label for="musicLibrary-enabled"><spring:message code="musiclibrary.enabled" /></label>
	<br />

	<fieldset id="location">
		<legend>
			<spring:message code="musiclibrary.location" />
		</legend>


		<div class="folder">
			<label class="new-line" for="folderLocation-path"><spring:message code="musiclibrary.location.path" /></label>
			<form:input path="musicLibrary.location.path" id="folderLocation-path" />
		</div>


		<br />

		<div class="check-location">
			<a class="button" href="javascript:void(0);"><spring:message code="path.check" /></a> <span class="message horizontal-gap"></span>
		</div>
	</fieldset>



	<label class="new-line" for="groups"><spring:message code="musiclibrary.groups" /></label>
	<form:checkboxes path="musicLibrary.groups" items="${groups}" itemLabel="name" itemValue="id" cssClass="checkboxes" delimiter="<br/>" />
	<br />



	<br />
	<c:if test="${musicLibraryPage.isExists}">
		<div class="new-line">

			<input type="checkbox" id="remote-share" value="1" /> <label for="remote-share"><spring:message code="library.remote.enable" /></label> <br />
			<fieldset id="remote-share-panel">
				<legend>
					<spring:message code="library.remote.title" />
				</legend>

				<div>
					<spring:message code="library.remote.description" />
				</div>
				<div>
					<a id="create-remote-link" class="button" href="javascript:;"><spring:message code="library.remote.button.create-link" /></a>
				</div>

				<div>
					<spring:message code="library.remote.connections.title" />
				</div>

				<table>
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th><spring:message code="library.remote.connection.url" /></th>
							<th><spring:message code="library.remote.connection.server" /></th>
							<th><spring:message code="library.remote.connection.created" /></th>
							<th><spring:message code="library.remote.connection.last-connected" /></th>
							<th><spring:message code="library.remote.connection.played-items" /></th>
							<th><spring:message code="library.remote.connection.status" /></th>
						</tr>
					</thead>

					<tbody>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>

					</tbody>

				</table>

				<div>
					<select id="library-remote-connection-action">
						<option value="">
							<spring:message code="library.remote.connection.select-status" />
						</option>
						<option value="enabled">
							<spring:message code="library.remote.connection.status.enable" />
						</option>
						<option value="disabled">
							<spring:message code="library.remote.connection.status.disable" />
						</option>
						<option value="delete">
							<spring:message code="library.remote.connection.status.delete" />
						</option>

					</select> <input id="save-library-remote-connections" type="button" class="button" value="<spring:message code="library.remote.connection.button.save" />" />
				</div>


			</fieldset>
		</div>
		<br />
	</c:if>


	<div class="button-panel">
		<input class="button" name="save" type="submit" value="<spring:message code="action.save" />" /> <input class="button" name="delete" type="submit"
			value="<spring:message code="action.delete" />" />
	</div>
</form:form>

