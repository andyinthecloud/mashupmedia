<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
	$("#play-all").click(function() {
	    var artistId = $(this).closest("ul").attr("id");
	    artistId = parseId(artistId, "artist-id");
	    mashupMedia.playArtist(artistId);
	});

	$("#add-all").click(function() {
	    var artistId = $(this).closest("ul").attr("id");
	    artistId = parseId(artistId, "artist-id");
	    mashupMedia.appendArtist(artistId);
	});

	$("ul.items ul.control-menu li.play-album a").click(function() {
	    var albumId = $(this).closest("li.item").attr("id");
	    albumId = parseId(albumId, "album-id");
	    mashupMedia.playAlbum(albumId);
	});

	$("ul.items ul.control-menu li.append-album a").click(function() {
	    var albumId = $(this).closest("li.item").attr("id");
	    albumId = parseId(albumId, "album-id");
	    mashupMedia.appendAlbum(albumId);
	});

	$("#albums li div.album a").click(function() {
	    fireRelLink(this);
	});

	$("#albums li").hover(function() {
	    $(this).find("a.album-cover").addClass("highlight");
	}, function() {
	    $(this).find("a.album-cover").removeClass("highlight");
	});

    });
</script>

<h1>${artistPage.artist.name}</h1>
<ul class="control-menu" id="artist-id-<c:out value="${artistPage.artist.id}" />">
	<li class="first"><a href="javascript:;" id="play-all"><spring:message code="action.play-all" /></a></li>
	<c:if test="${isPlaylistOwner}">
		<li><a href="javascript:;" id="add-all"><spring:message code="action.add-all" /></a></li>
	</c:if>
</ul>

<div class="information">
	<div class="profile">
		<c:out value="${artistPage.remoteMediaMeta.profile}" escapeXml="false" />
	</div>

	<div class="images">
		<c:forEach items="${artistPage.remoteMediaMeta.remoteImages}" var="remoteImage">
			<img src="${remoteImage.thumbUrl}" width="150" height="auto"/>
		</c:forEach>
	</div>

</div>


<ul id="albums" class="items">
	<c:forEach items="${artistPage.artist.albums}" var="album">
		<li id="album-id-${album.id}" class="item"><a class="album-cover" href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art-thumbnail/${album.id}" />" title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />"
				alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
		</a>
			<div class="album">
				<a href="javascript:;" rel="address:/address-load-album-${album.id}"><c:out value="${album.name}" /></a>
			</div>
			<div class="artist">
				<a href="javascript:;" rel="address:/address-artist-${album.artist.id}"><c:out value="${album.artist.name}" /></a>
			</div>

			<ul class="control-menu">
				<li class="first play-album"><a href="javascript:;"><spring:message code="action.play-all" /></a></li>
				<li class="append-album"><a href="javascript:;"><spring:message code="action.add-all" /></a></li>
			</ul></li>
	</c:forEach>
</ul>
