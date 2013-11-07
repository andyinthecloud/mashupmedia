<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
	$(document).ready(function() {
		$("#choose-library-type a.button" ).click(function() {
			if ($("#choose-library-type input[name=libraryType]").is(":checked") ) {
				var libraryType = $("#choose-library-type input[name=libraryType]:checked").val();
				window.location = "<c:url value="/app/configuration/library/" />" + libraryType;
			}
		});
	});
</script>


<fieldset id="choose-library-type">
	<legend>
		<spring:message code="chooselibrary.title" />
	</legend>

	<div>
		<input type="radio" value="music" name="libraryType" id="libraryTypeMusic" checked="checked"> <label for="libraryTypeMusic"><spring:message code="chooselibrary.type.music" /></label> <br />
		<input type="radio" value="video" name="libraryType" id="libraryTypeVideo"> <label for="libraryTypeVideo"><spring:message code="chooselibrary.type.video" /></label>
	</div>
	
	<br />

	<div>
		<a class="button" href="javascript:;"><spring:message code="next" /></a>
	</div>
</fieldset>



