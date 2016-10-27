<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="ui-field-contain">
	<fieldset data-role="controlgroup" data-type="horizontal"
		id="photo-albums-view">
		<input name="photo-albums-sort" id="photo-albums-sort-latest"
			value="latest" type="radio"
			<c:if test="${photoListType == 'LATEST'}">checked="checked"</c:if> />
		<label for="photo-albums-sort-latest"><spring:message
				code="photo-albums.sort.latest" /></label> <input name="photo-albums-sort"
			id="photo-albums-sort-alphabetical" value="alphabetical" type="radio"
			<c:if test="${photoListType == 'ALPHABETICAL'}">checked="checked"</c:if> />
		<label for="photo-albums-sort-alphabetical"><spring:message
				code="photo-albums.sort.alphabetical" /></label>
	</fieldset>
</div>


<ul class="main-menu ui-listview-inset ui-corner-all ui-shadow"
	data-role="listview">
	<c:forEach items="${albums}" var="album">
		<li><a rel="internal"
			title="<spring:message code="photo-album.title" /> - ${album.name}"
			href="<c:url value="/app/photo/album/${album.id}"/>">${album.name}</a></li>
	</c:forEach>
</ul>

