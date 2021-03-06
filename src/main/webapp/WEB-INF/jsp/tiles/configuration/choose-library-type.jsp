<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
	$(document).ready(function() {
		
		$("#choose-library-type a.ui-btn").click(function() {
			var checkedLibraryTypeElement = $("#choose-library-type input[name=libraryType]:checked");
			
			if (checkedLibraryTypeElement) {
				var libraryType = $("#choose-library-type input[name=libraryType]:checked").val();
				var libraryUrl = "<c:url value="/configuration/library/" />" + libraryType;
				var title = checkedLibraryTypeElement.attr("title");				
				loadInternalPage(title, libraryUrl);
			}
		});
	});
</script>


<fieldset id="choose-library-type" data-role="controlgroup">
	<legend>
		<spring:message code="chooselibrary.title" />
	</legend>

	<input type="radio" value="music" name="libraryType"
		id="libraryTypeMusic" checked="checked"
		title="<spring:message code="library.music.title"/>"> <label
		for="libraryTypeMusic"><spring:message
			code="chooselibrary.type.music" /></label> <input type="radio" value="video"
		name="libraryType" id="libraryTypeVideo" title="<spring:message code="library.video.title"/>"> <label
		for="libraryTypeVideo"><spring:message
			code="chooselibrary.type.video" /></label> <input type="radio" value="photo"
		name="libraryType" id="libraryTypePhoto" title="<spring:message code="library.photo.title"/>"> <label
		for="libraryTypePhoto"><spring:message
			code="chooselibrary.type.photo" /></label> <br /> <a
		class="ui-btn ui-btn-inline" href="javascript:;"><spring:message
			code="next" /></a>
</fieldset>



