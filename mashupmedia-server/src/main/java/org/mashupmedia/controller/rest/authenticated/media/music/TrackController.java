package org.mashupmedia.controller.rest.authenticated.media.music;

import org.mashupmedia.service.MusicManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private/music/tracks")
public class TrackController {
    private final MusicManager musicManager;

    @DeleteMapping(value = "/{trackIds}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deletTrack(@PathVariable long[] trackIds) {
        if (trackIds == null || trackIds.length < 1) {
            return ResponseEntity.badRequest()
            .body(false);
        }

        for (long trackId : trackIds) {
            musicManager.deleteTrack(trackId);            
        }

        return ResponseEntity.ok()
                .body(true);
    }

}
