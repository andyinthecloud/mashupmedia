<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    var playlistSelectName = $("#playlist input[name=playlistSelectName]").val();

    $(document).ready(function() {
        window.scrollTo(0, 0);
        showFooterTabs("music");
        $("ul.playlist-items").sortable();
        $("h1.edit").editable("<c:url value="/app/restful/music-playlist/edit-playlist-name" />");
        
        $("div.dynamic-content").on("sortstop", "ul.playlist-items", function( event, ui) {
            var playlistId = $("#playlist input[name=playlistId]").val();
            var mediaItemIds = [];
            savePlaylist();
        });
        
        /*
        $("ul.playlist-items").on("sortstop", function( event, ui) {
            var playlistId = $("#playlist input[name=playlistId]").val();
            var mediaItemIds = [];
            savePlaylist();
        });
        */
        
        //$("ul.playlist-items").disableSelection();
        

        /*
        $("#playlist table.songs tbody").sortable({
            stop: function(event, ui) {
            }
        });

        var playlistTable = $("#playlist table.songs").dataTable({
            "bFilter": false,
            "oLanguage": {
                "sEmptyTable": "<spring:message code="music.playlist.empty" />"
            },
            "aoColumnDefs": [{
                "bSortable": false,
                "aTargets": [0]
            }],
            "bPaginate": false,
            "bAutoWidth": false,
            "bInfo": false
        });

        $("#playlist table.songs td.controls a.delete").click(function() {
            var songRow = $(this).closest("tr");
            if ($(songRow).hasClass(mashupMedia.playingClass)) {
                mashupMedia.destroyPlayer();
            }
            $(this).closest("tr").remove();
        });

        $("#playlist table.songs td.controls a.play").click(function() {
            $("table.songs tbody tr").removeClass(mashupMedia.playingClass);
            var songRow = $(this).closest("tr");
            $(songRow).addClass(mashupMedia.playingClass);
            var mediaItemId = $(songRow).attr("id");
            var mediaItemId = parseId(mediaItemId, "playlist-media-id");
            var playlistId = $("#playlist input[name=playlistId]").val();
            mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, true);
        });
         */
         
         
 		$("div.dynamic-content").on("click", "#playlist ul.playlist-items a.delete", function() {
            var songRow = $(this).closest("li");
            $(songRow).remove();
            savePlaylist();
 		});
         

        $("div.dynamic-content").on("change", "#playlist-actions", function() {
            var action = $(this).val();
            if (action == "") { return; }

            var isHidePlaylistFeatures = false;

            var playlistName = "${playlist.name}";
            var playlistAction = "save";
            if (action == "clear") {
                $("#playlist ul.playlist-items li").remove();
                savePlaylist();
            } else if (action == "change-name") {
                $("#playlist h1").hide();
                $("#playlist div.change-name").show();
                playlistAction = "save";
            } else if (action == "new") {
                $("#playlist ul.playlist-items li").remove();                
                newPlaylist();
                /*
                $("#playlist h1").hide();
                $("#playlist div.change-name").show();
                $("#playlist table.songs tbody tr").remove();
                playlistName = playlistSelectName;
                isHidePlaylistFeatures = true;
                playlistAction = "new";
                */
            } else if (action == "copy") {
                copyPlaylist()
            } else if (action == "delete") {
                playlistAction = "delete";
            }

            $("#playlist input[name=playlistName]").val(playlistName);
            $("#playlist input[name=playlistAction]").val(playlistAction);

            if (isHidePlaylistFeatures) {
                $("#playlist table.songs").hide();
                $("#play-playlist").hide();
            } else {
                $("#playlist table.songs").show();
                $("#play-playlist").show();
            }

        });

        /*
        $("#save-current-playlist").click(function() {
            var action = $("#playlist-actions").val();
            if (action == "clear") {
                mashupMedia.clearPlayer();
            }

            savePlaylist();
        });
        */

        $("#play-playlist").click(function() {
            var playlistId = $("#playlist input[name=playlistId]").val();
            mashupMedia.playPlaylist(playlistId);

            $("table.songs tbody tr").removeClass(mashupMedia.playingClass);
            $("table.songs tbody tr:first").addClass(mashupMedia.playingClass);
        });

        $("#playlist input[name=playlistName]").blur(function() {
            var playlistName = $.trim($(this).val());
            if (playlistName.length == 0) {
                $(this).val(playlistSelectName);
            }
        });

        $("#playlist input[name=playlistName]").focus(function() {
            var playlistName = $.trim($(this).val());
            if (playlistName == playlistSelectName) {
                $(this).val("");
            }
        });

        var playlistId = "${playlist.id}";
        if (playlistId.length == 0 || isNaN(playlistId)) {
            $("#playlist-actions").val("new")
            $("#playlist-actions").trigger("change");
        }

    });
    
    function newPlaylist() {
        var mediaItemIds = getMediaItemIds();
        $.post( "<c:url value="/app/restful/music-playlist/new-playlist" />", {              
            mediaItemIds: mediaItemIds 
        }, function(data) {
         	var url = "<c:url value="/app/playlist/music" />?playlist=" + data;
         	loadInternalPage("<spring:message code="music.playlist.title" />", url)
        });
    }
    
    function copyPlaylist() {
        var name = $("#playlist h1").text() + " <spring:message code="playlist.copy.name.suffix" />";
        var mediaItemIds = getMediaItemIds();
        $.post( "<c:url value="/app/restful/music-playlist/new-playlist" />", {
            name: name,
            mediaItemIds: mediaItemIds 
        }, function(data) {
         	var url = "<c:url value="/app/playlist/music" />?playlist=" + data;
         	loadInternalPage("<spring:message code="music.playlist.title" />", url)
        });
    }
    

    function savePlaylist() {

        //var playlistName = $.trim($("#playlist input[name=playlistName]").val());

        var playlistAction = $("#playlist input[name=playlistAction]").val();
        
        
        /*
        var isNewPlaylistId = false;
        if (playlistAction == "save-as" || playlistAction == "new") {
            isNewPlaylistId = true;
        }

        
        if (isNewPlaylistId) {
            if (playlistName.length == 0 || playlistName == playlistSelectName) {
                alert("<spring:message code="playlist.error.empty-name" />");
                return;
            }
        }
        */

            
	        

            
        
        
	        var playlistId = $("#playlist input[name=playlistId]").val();
        	var mediaItemIds = getMediaItemIds();
        
	        $.post( "<c:url value="/app/restful/music-playlist/save-playlist" />", { 
	            playlistId: playlistId, 
	            mediaItemIds: mediaItemIds 
	        });
        
        /*
        var playlistId = $("#playlist input[name=playlistId]").val();
        var mediaItemIds = new Array();

        $("#playlist table.songs tbody tr").each(function(index) {
            var rowId = $(this).attr("id");
            if (rowId != undefined) {
                var mediaItemId = parseId(rowId, "playlist-media-id");
                mediaItemIds[index] = mediaItemId;
            }
        });

        $.post(mashupMedia.contextUrl + "app/ajax/playlist/" + playlistAction, {
            "playlistId": playlistId,
            "playlistName": playlistName,
            "mediaItemIds": mediaItemIds
        }, function(data) {
            
            // $("#playlist input[type=playlistAction]").val("save");
            // $("#playlist-actions").val("");
            
            if (playlistAction == "delete") {
                loadPlaylists();
                return;
            }

            if (isNewPlaylistId) {
                playlistId = data.response.message;
            }

            loadPlaylist(playlistId);
        });
        */

    }
    
    function getMediaItemIds() {
        var mediaItemIds = [];
        
        $("#playlist ul.playlist-items li").each(function(index) {
            var mediaItemId = parseId($(this).attr("id"), "playlist-media-id");
            mediaItemIds.push(mediaItemId);                
        });
        
        return mediaItemIds;                
    }
    
    
</script>


<div id="playlist">

	<input type="hidden" name="playlistAction" value="save" /> <input
		type="hidden" name="playlistSelectName"
		value="<spring:message code="playlist.select.name" />" /> <input
		type="hidden" name="playlistId"
		value="<c:out value="${playlist.id}" />" />



	<h1 class="edit" id="playlist-id-${playlist.id}">${playlist.name}</h1>

	<div class="hide change-name">
		<input type="text" name="playlistName" value="${playlist.name}" />
	</div>



	<div class="controls clear">

		<select id="playlist-actions" data-native-menu="false">
			<option value="">
				<spring:message code="music.playlist.actions" />
			</option>

			<option value="play">
				<spring:message code="action.play" />
			</option>


			<c:if test="${canSavePlaylist}">
				<option value="clear">
					<spring:message code="music.playlist.action.clear" />
				</option>
			</c:if>

			<option value="new">
				<spring:message code="music.playlist.action.new" />
			</option>

			<c:if test="${canSavePlaylist}">
				<option value="change-name">
					<spring:message code="music.playlist.action.change-name" />
				</option>
			</c:if>

			<option value="copy-to">
				<spring:message code="music.playlist.action.copy-to" />
			</option>

			<c:if test="${!playlist.userDefault}">
				<option value="delete">
					<spring:message code="music.playlist.action.delete" />
				</option>
			</c:if>

			<option value="playlists">
				<spring:message code="music.playlist.action.playlists" />
			</option>

		</select>

		<!-- 
		<c:if test="${canSavePlaylist}">
			<input type="button" class="button" id="save-current-playlist"
				value="<spring:message code="action.save" />" />
		</c:if>

		<input type="button" class="button play" id="play-playlist"
			value="<spring:message code="action.play" />" />
			 -->

	</div>

	<ul class="playlist-items">


		<c:forEach items="${playlist.accessiblePlaylistMediaItems}"
			var="playlistMediaItem">
			<c:set var="song" value="${playlistMediaItem.mediaItem}" />
			<c:set var="playingClass" value="" />
			<c:if test="${playlistMediaItem.playing }">
				<c:set var="playingClass" value="playing" />
			</c:if>

			<li
				id="playlist-media-id-<c:out value="${song.id}"/>-album-id-${song.album.id}"
				class="cursor-move <c:out value="${playingClass}"/>"><a href="javascript:;"
				class="delete"><img
					alt="<spring:message code="action.playlist.item.remove"/>"
					title="<spring:message code="action.playlist.item.remove"/>"
					src="<c:url value="${themePath}/images/controls/delete.png"/>" /></a>




				<div class="item">
					<div class="title">${song.displayTitle}</div>
					<div class="meta">${song.artist.name}-${song.album.name}</div>
				</div>
				
				
<img class="up-down"
				alt="<spring:message code="action.reorder"/>"
				title="<spring:message code="action.reorder"/>"
				src="<c:url value="${themePath}/images/controls/up-down.png"/>" />
				
				
				</li>





		</c:forEach>


	</ul>

</div>
