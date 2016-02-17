<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:if test="${!isAppend}">
	<script type="text/javascript">
        $(document).ready(function() {

            // Unbind declared event handlers
            $("div.dynamic-content").off("mouseover", "div.albums div.album");
            $("div.dynamic-content").off("mouseout", "div.albums div.album");
            $("div.dynamic-content").off("change", "input[name=albums-view]");
            $("div.dynamic-content").off("click", "ul.index-letters a");

            mashupMedia.filterAlbumsSearchLetter = "";

            $(window).unbind("scroll");

            mashupMedia.reinitialiseInfinitePage();

            showFooterTabs("music", "music-albums");

            $(window).scroll(function() {
                if ($("div.albums div.album").length == 0) { return; }
                appendContentsOnScroll("${musicAlbumListType.className}");
            });

            $("div.dynamic-content").on("click", "ul.index-letters a", function() {
                mashupMedia.filterAlbumsSearchLetter = $(this).text();
            });

            $("div.dynamic-content").on("mouseover", "div.albums div.album", function() {
                $(this).addClass("highlight");
            });

            $("div.dynamic-content").on("mouseout", "div.albums div.album", function() {
                $(this).removeClass("highlight");
            });

            $("div.dynamic-content").on("change", "input[name=albums-view]", function() {
                var albumView = $(this).val();
                var url = "<c:url value="/app/music/latest-albums" />";
                var title = "<spring:message code="music.title.latest" />";

                if (albumView == "random") {
                    url = "<c:url value="/app/music/random-albums" />";
                    title = "<spring:message code="music.title.random" />";
                } else if (albumView == "alphabetical") {
                    url = "<c:url value="/app/music/albums" />";
                    title = "<spring:message code="music.title.alphabetical" />";
                }

                loadInternalPage(title, url);
            });
            
            //$("div.dynamic-content").on("mouseout", "div.albums div.album", function() {
                
            

        });
    </script>

	<div class="ui-field-contain">
		<fieldset data-role="controlgroup" data-type="horizontal"
			id="albums-view">
			<input name="albums-view" id="albums-view-random" value="random"
				type="radio"
				<c:if test="${musicAlbumListType == 'RANDOM'}">checked="checked"</c:if> />
			<label for="albums-view-random"><spring:message
					code="music.show.random" /></label> <input name="albums-view"
				id="albums-view-latest" value="latest" type="radio"
				<c:if test="${musicAlbumListType == 'LATEST'}">checked="checked"</c:if> />
			<label for="albums-view-latest"><spring:message
					code="music.show.latest" /></label> <input name="albums-view"
				id="albums-view-alphabetical" value="alphabetical" type="radio"
				<c:if test="${musicAlbumListType == 'ALPHABETICAL'}">checked="checked"</c:if> />
			<label for="albums-view-alphabetical"><spring:message
					code="music.show.alphabetical" /></label>
		</fieldset>
	</div>



</c:if>

<c:if test="${not empty albumIndexLetters}">

	<ul class="index-letters">
		<c:forEach items="${albumIndexLetters}" var="letter">
			<c:set var="urlLetter" value="${letter}" />
			<c:if test="${urlLetter == '#'}">
				<c:set var="urlLetter" value="." />
			</c:if>

			<li><a rel="internal"
				title="<spring:message code="music.title" /> - ${letter}"
				href="<c:url value="/app/music/albums" />?searchLetter=${urlLetter}">${letter}</a></li>
		</c:forEach>
	</ul>

</c:if>


<div class="albums">
	<c:forEach items="${albums}" var="album">
		<div class="album" id="album-id-${album.id}">
			<a rel="internal"
				title="<spring:message code="music.title" /> - ${album.name}"
				href="<c:url value="/app/music/album/${album.id}" />"> <img
				src="<c:url value="/app/music/album-art/thumbnail/${album.id}" />"
				title="${album.artist.name} - ${album.name}"
				alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>


			<div class="album-title">
				<div class="artist-name">
					<a href="javascript:;"
						rel="address:/address-artist-${album.artist.id}">${album.artist.name}</a>
				</div>
				<div class="album-name">
					<a href="javascript:;"
						rel="address:/address-load-album-${album.id}">${album.name}</a>
				</div>
			</div>

			<div class="album-control">
				<a class="play" href="javascript:;"
					title="<spring:message code="action.play" />"><img
					alt="<spring:message code="action.play"/>"
					title="<spring:message code="action.play"/>"
					src="<c:url value="${themePath}/images/controls/play.png"/>" /></a>
				<c:if test="${isPlaylistOwner}">
					<a class="add" href="javascript:;"
						title="<spring:message code="action.add" />"><img
						alt="<spring:message code="action.add"/>"
						title="<spring:message code="action.add"/>"
						src="<c:url value="${themePath}/images/controls/add.png"/>" /></a>
				</c:if>
			</div>
		</div>
	</c:forEach>
</div>


