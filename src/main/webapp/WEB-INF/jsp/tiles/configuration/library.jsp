<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
    $(document).ready(function() {
		var locationType = $("#location input:radio[name=locationType]:checked").val();
	
		
		$("div.ui-content form").on("click", "#location input[name='locationType']", function() {
		    var locationType = $(this).attr("value");
		    showLocation(locationType);
		});
		
		$("div.ui-content form").on("click", "#remote-share-panel", function() {
			$(this).select();
		});		
	
		$("div.ui-content form").on("click", "#location div.check-location a", function() {
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
				$("#location div.check-location .status-message").html(data.response.message);
			}
		    });
	
		});
	
		showRemoteShares();
	
		$("#libraryPage input:submit").click(function() {
		    var action = $(this).attr("name");
		    $("#action").val(action);
		});
		
		$("#remote-share-panel").hide();
		
		$("div.ui-content form").on("click", "#remote-share", function() {
		    $("#remote-share-panel").toggle();
		});
		
		$("div.ui-content form").on("click", "#create-remote-link", function() {
			$.ajax({
				url : "<c:url value="/app/ajax/library/add-remote-share"/>",
				type : "post",
				data : {
				    libraryId : <c:out value="${libraryPage.library.id}" />
				},
				success : function(data) {
				    showRemoteShares();
				}
		    });	    
		});
		
		$("div.ui-content form").on("click", "#save-library-remote-connections", function() {
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
				    libraryId : <c:out value="${libraryPage.library.id}" />,
				    remoteShareStatus : remoteShareStatus
				},
				success : function(data) {
				    showRemoteShares();
				}
		    });
			
		})
	
	
    });
    
    function showRemoteShares() {
   	<c:if test="${libraryPage.isExists}">
   	$.ajax({
   		url : "<c:url value="/app/ajax/library/get-remote-shares"/>",
   		type : "get",
   		data : {
   		    libraryId : <c:out value="${libraryPage.library.id}" />
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
   			    var link = getHostUrl() + "app/remote/connect/${libraryPage.library.libraryTypeValue}/" + remoteShare.uniqueName;
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


<form:form commandName="libraryPage">
	<form:errors path="*" cssClass="error-box" />
	<form:hidden path="action" />
	<form:hidden path="library.id" />
	<form:hidden path="library.scanMinutesInterval" />

	<div class="new-line">
		<label for="library-name"><spring:message code="${pageTitle}" /></label>
		<form:input path="library.name" id="library-name"
			data-clear-btn="true" />
	</div>

	<div class="new-line">
		<form:checkbox path="library.enabled" id="library-enabled" />
		<label for="library-enabled"><spring:message
				code="library.enabled" /></label>
	</div>

	<div class="new-line">
		<fieldset id="location">
			<legend>
				<spring:message code="library.location" />
			</legend>

			<div class="folder">
				<label class="new-line" for="folderLocation-path"><spring:message
						code="library.location.path" /></label>
				<form:input path="library.location.path" id="folderLocation-path"
					data-clear-btn="true" />
			</div>

			<div class="new-line check-location">
				<a class="button" href="javascript:void(0);"><spring:message
						code="path.check" /></a> <span class="status-message horizontal-gap"></span>
			</div>

		</fieldset>
	</div>

	<tiles:insertAttribute name="additionalConfiguration" />

	<div class="new-line">
		<fieldset data-role="controlgroup">
			<legend>
				<spring:message code="library.groups" />
			</legend>
			<form:checkboxes path="library.groups" items="${groups}"
				itemLabel="name" itemValue="id" />
		</fieldset>
	</div>

	<c:if test="${libraryPage.isShowRemoteConfiguration}">
		<div class="new-line">

			<input type="checkbox" id="remote-share" value="1" /> <label
				for="remote-share"><spring:message
					code="library.remote.enable" /></label> <br />
			<fieldset id="remote-share-panel">
				<legend>
					<spring:message code="library.remote.title" />
				</legend>

				<div>
					<spring:message code="library.remote.description" />
				</div>
				<div>
					<a id="create-remote-link" class="button" href="javascript:;"><spring:message
							code="library.remote.button.create-link" /></a>
				</div>

				<table>
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th><spring:message code="library.remote.connection.url" /></th>
							<th><spring:message code="library.remote.connection.server" /></th>
							<th><spring:message code="library.remote.connection.created" /></th>
							<th><spring:message
									code="library.remote.connection.last-connected" /></th>
							<th><spring:message
									code="library.remote.connection.played-items" /></th>
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

					</select> <input id="save-library-remote-connections" type="button"
						class="button"
						value="<spring:message code="library.remote.connection.button.save" />" />
				</div>


			</fieldset>
		</div>
		<br />
	</c:if>


	<div class="button-panel">
		<input class="button" name="save" type="submit"
			value="<spring:message code="action.save" />" /> <input
			class="button" name="delete" type="submit"
			value="<spring:message code="action.delete" />" />
	</div>
</form:form>

