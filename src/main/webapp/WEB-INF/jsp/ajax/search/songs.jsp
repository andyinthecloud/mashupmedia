<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    var pageNumber = 0;
    var searchLetter = "";
    $(document).ready(function() {
	$("ul.items div.controls a.play").click(function() {
	    var songId = $(this).closest("li.item").attr("id");
	    songId = parseId(songId, "song-id");
	    mashupMedia.playSong(songId);
	});

	$("ul.items div.controls a.add").click(function() {
	    var songId = $(this).closest("li.item").attr("id");
	    songId = parseId(songId, "song-id");
	    mashupMedia.appendSong(songId);
	});

	$("ul.items li").hover(function() {
	    $(this).find("a.album-cover").addClass("highlight");
	}, function() {
	    $(this).find("a.album-cover").removeClass("highlight");
	});

	$(window).scroll(function() {
	    if ($("ul.items li").length == 0) {
		return;
	    }
	    appendContentsOnScroll();
	});

	
	$("#order-by").change(function() {
	   var orderBy = $(this).val();
	   $("input[name='orderBy']").val(orderBy);
	   
	   var isAscending = $("#order-ascending").val();
	   $("input[name='isAscending']").val(isAscending);

	   loadSongSearchResults(false);
	});

	$("#order-ascending").change(function() {
	   var orderBy = $("#order-by").val();
	   $("input[name='orderBy']").val(orderBy);

	   var isAscending = $(this).val();
	   $("input[name='isAscending']").val(isAscending);

	   loadSongSearchResults(false);
	});
	
	var orderBy = "${orderBy}";
	$("#order-by").val(orderBy);
	
	var isAscending = "${isAscending}";
	$("#order-ascending").val(isAscending);
		
	
    });
</script>

<div class="action-buttons">
	<select id="order-by">
		<option value="song_title"><spring:message code="music.search-results.sort.category.song-title" /></option>
		<option value="last_played"><spring:message code="music.search-results.sort.category.last-played" /></option>
		<option value="favourites"><spring:message code="music.search-results.sort.category.favourites" /></option>
		<option value="album_name"><spring:message code="music.search-results.sort.category.album" /></option>
		<option value="artist_name"><spring:message code="music.search-results.sort.category.artist" /></option>
	</select>
	<select id="order-ascending">
		<option value="true"><spring:message code="music.search-results.sort.order.ascending" /></option>
		<option value="false"><spring:message code="music.search-results.sort.order.descending" /></option>
	</select>
</div>

<c:if test="${pageNumber == 0}">
	<h1>
		<spring:message code="music.search-results.title" />
	</h1>
</c:if>

<ul class="items">

	<c:forEach items="${songs}" var="song" varStatus="status">
		<li id="song-id-${song.id}" class="item"><a class="album-cover" href="javascript:;" rel="address:/address-load-album-${song.album.id}"> <img src="<c:url value="/app/music/album-art-thumbnail/${song.album.id}" />"
				title="<c:out value="${song.album.artist.name}" /> - <c:out value="${song.album.name}" />" alt="<c:out value="${song.album.artist.name}" /> - <c:out value="${song.album.name}" />" />
		</a>

			<div class="song">
				<c:out value="${song.displayTitle}" />
			</div>

			<div class="artist-and-album">
				<a href="javascript:;" rel="address:/address-artist-${song.artist.id}"><c:out value="${song.artist.name}" /></a> - <a href="javascript:;" rel="address:/address-load-album-${song.album.id}"><c:out value="${song.album.name}" /></a>
			</div>

			<div class="controls">
				<a class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a> <a class="add" href="javascript:;" title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus"></span></a>
			</div></li>
	</c:forEach>

</ul>