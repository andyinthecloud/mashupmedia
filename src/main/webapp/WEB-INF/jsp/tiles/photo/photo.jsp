<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("#meta-section a").click(function() {
			$("#meta-section p.hide").toggle("slow", function() {
				// Animation complete.
			});
		});
	});
</script>

<div class="sub-panel">

	<h1>${photo.album.name}-${photo.displayTitle}</h1>

	<table class="photo-container">
		<tr>
			<td class="photo-previous"><a href="javascript:;"
				class="arrow image-previous">&nbsp;</a></td>
			<td class="photo"><img class="original-photo"
				alt="${photo.displayTitle}" title="${photo.displayTitle}"
				src="<c:url value="/app/photo/original/${photo.id}" />" /></td>
			<td class="photo-next"><a href="javascript:;"
				class="arrow image-next">&nbsp;</a></td>
		</tr>
	</table>

	<ul class="items">
		<li><a href="<c:url value="/app/photo/original/${photo.id}" />"><spring:message
					code="photo.original" /></a></li>
		<li id="meta-section"><a href="javascript:;"><spring:message
					code="photo.meta" /></a> <%
 	pageContext.setAttribute("newLine", "\n");
 %>
			<p class="hide">${fn:replace(photo.metadata, newLine, "<br />")}</p>
		</li>
	</ul>

</div>