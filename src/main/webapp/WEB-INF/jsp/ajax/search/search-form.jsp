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
	<label><spring:message code="music.search.form.label.song-title" /></label>
	<input type="text" name="title"/>
	<br />
	<label class="new-line"><spring:message code="music.search.form.label.artist" /></label>
	<input type="text" name="artist"/>
	<br />
	<label class="new-line"><spring:message code="music.search.form.label.album" /></label>
	<input type="text" name="album"/>
	<br />
	<label class="new-line"><spring:message code="music.search.form.label.genre" /></label>
	<select name="genres" multiple="multiple" size="10">
		<c:forEach items="${genres}" var="genre">
			<option value="${genre.id}">${genre.name}</option>
		</c:forEach>	
	</select>
	<br />
	<label class="new-line"><spring:message code="music.search.form.label.year" /></label>
	<label class="date-from" for="date-from"><spring:message code="from" /></label><input class="date" id="date-from" type="text" name="dateFrom"/>
	<label class="date-to" for="date-to"><spring:message code="to" /></label><input class="date" id="date-to" type="text" name="dateTo"/>
	<br />
	<label class="new-line"><spring:message code="music.search.form.label.order" /></label>
	<select name="genres" multiple="multiple" size="10">
		<c:forEach items="${genres}" var="genre">
			<option value="${genre.id}">${genre.name}</option>
		</c:forEach>	
	</select>	
	
	
	
	

	<div class="button-panel">
		<input class="button" type="submit" value="<spring:message code="action.search"/>" />
	</div>

</form>
