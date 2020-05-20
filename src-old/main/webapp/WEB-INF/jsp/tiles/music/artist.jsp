<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
        
        
        $(window).unbind("scroll");
		window.scrollTo(0, 0);
		showFooterTabs("music-artists");
		
        $("h1.edit").editable("<c:url value="/app/restful/media/music/save-artist-name" />", {
            tooltip: "<spring:message code="action.click.edit" />"
        });
		
        $("div.albums div.album").mouseover(function() {
            $(this).addClass("highlight");
        });

        $("div.dynamic-content div.albums div.album").mouseout(function() {
            $(this).removeClass("highlight");
        });
	
        $("#play-all").click(function() {
		    var artistId = $(this).closest("div.control-menu").attr("id");
		    artistId = parseId(artistId, "artist-id");
		    mashupMedia.playArtist(artistId);
        });        
        
        $("#add-all").click(function() {
		    var artistId = $(this).closest("div.control-menu").attr("id");
		    artistId = parseId(artistId, "artist-id");
		    mashupMedia.appendArtist(artistId);
		});
		
		$.getJSON("<c:url value="/app/remote/music/artist/${artistPage.artist.id}" />", function( data ) {
			displayRemoteArtistInformation(data);			
		});	

        $("div.dynamic-content div.title-with-player-control div.re-encode a").click(function() {
            $.post("<c:url value="/app/restful/encode/music-artist" />", { id: <c:out value="${artistPage.artist.id}" /> })
            .done(function( data ) {
                mashupMedia.showMessage(data);          
            });            
        });		
        
		$("div.dynamic-content div.albums div.album-control a.play").click(function() {
			playAlbum(this);
		});
		
		$("div.dynamic-content div.albums div.album-control a.add").click(function() {
			appendAlbum(this);
		});        

    });
    
    function playAlbum(element) {
        var albumId = $(element).closest("div.album").attr("id");
        albumId = parseId(albumId, "album-id");
        mashupMedia.playAlbum(albumId);
    }
    
    function appendAlbum(element) {
        var albumId = $(element).closest("div.album").attr("id");
        albumId = parseId(albumId, "album-id");
        mashupMedia.appendAlbum(albumId);
    }    


</script>

<jsp:include page="/WEB-INF/jsp/inc/remote-music-info-js.jsp" />

<input type="hidden" id="artist-id" name="artist-id" value="${artistPage.artist.id}" />


<div class="title-with-player-control">

	<h1 class="edit" id="album-id-${artistPage.artist.id}">${artistPage.artist.name}</h1>

	<div class="control-menu"
		id="artist-id-<c:out value="${artistPage.artist.id}" />">

		<a href="javascript:;" id="play-all"
			title="<spring:message code="action.play" />"><img
			src="<c:url value="${themePath}/images/controls/play.png"/>" /></a>

		<c:if test="${isPlaylistOwner}">
			<a href="javascript:;" id="add-all"
				title="<spring:message code="action.add" />"><img
				src="<c:url value="${themePath}/images/controls/add.png"/>" /></a>

		</c:if>
		
		<a href="javascript:;"
			title="<spring:message code="action.re-encode.tip" />"><spring:message
				code="action.re-encode" /></a>
		
	</div>

</div>



<div id="remote" class="teaser-on">
	<a class="arrow-show-hide" href="javascript:;"> <img
		src="<c:url value="/images/arrow-down.png" />" /></a>
	<div class="profile"></div>
	<ul class="images"></ul>

	<div class="disclaimer">
		<spring:message code="music.artists.remote" />
		<a href="http://www.last.fm" target="_blank" title=""><img
			title="last.fm" src="<c:url value="/images/lastfm.png" />" /></a>.
	</div>
</div>


<div class="albums">
	<c:forEach items="${artistPage.artist.albums}" var="album">
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
					<a rel="internal"
						href="<c:url value="/app/music/album/${album.id}" />">${album.name}</a>
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


