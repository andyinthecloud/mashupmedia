package org.mashupmedia.mapper.playlist;

import org.mashupmedia.dto.media.playlist.PlaylistPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.User;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.util.AdminHelper;
import org.springframework.stereotype.Component;

@Component
public class PlaylistMapper implements DomainMapper<Playlist, PlaylistPayload> {

    @Override
    public PlaylistPayload toPayload(Playlist domain) {
        return PlaylistPayload.builder()
                .id(domain.getId())
                .name(domain.getName())
                .edit(isEdit(domain))
                .delete(isDelete(domain))
                .privatePlaylist(domain.isPrivatePlaylist())
                .mashupMediaType(domain.getMashupMediaType())
                .build();
    }

    private boolean isEdit(Playlist domain) {
        User user = AdminHelper.getLoggedInUser();
        if (user.isAdministrator()) {
            return true;
        }

        return domain.getCreatedBy().equals(user) ? true : false;
    }

    private boolean isDelete(Playlist domain) {
        if (domain.isUserDefault()) {
            return false;
        }

        return isEdit(domain);
    }

    @Override
    public Playlist toDomain(PlaylistPayload payload) {
        return Playlist.builder()
                .name(payload.getName())
                .id(payload.getId())
                .privatePlaylist(payload.isPrivatePlaylist())
                .build();
    }

}
