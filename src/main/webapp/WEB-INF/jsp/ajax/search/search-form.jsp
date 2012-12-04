<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    var pageNumber = 0;
    var searchLetter = "";
    $(document).ready(function() {

    });
</script>


<h1 class="content-title">
	<spring:message code="music.search.form.title" />
</h1>

<form>
	<label class="new-line"><spring:message code="music.search.form.label.title" /></label>
	<input type="text" name="title"/>
	<br />
	<label><spring:message code="music.search.form.label.artist" /></label>
	<input type="text" name="artist"/>
	<br />
	<label><spring:message code="music.search.form.label.album" /></label>
	<input type="text" name="album"/>
	<br />

	<div class="button-panel">
		<input class="button" type="submit" value="<spring:message code="action.search"/>" />
	</div>

</form>
