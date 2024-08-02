package org.mashupmedia.controller.rest.authenticated.meta;

import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.dto.media.music.GenrePayload;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.mapper.RoleMapper;
import org.mashupmedia.mapper.media.music.GenreMapper;
import org.mashupmedia.service.AdminManager;
import org.mashupmedia.service.MusicManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/private/meta")
@RequiredArgsConstructor
public class MetaController {

    private final AdminManager adminManager;
    private final RoleMapper roleMapper;
    private final MusicManager musicManager;
    private final GenreMapper genreMapper;

    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NameValuePayload<String>> getRoles() {
        return adminManager.getRoles()
                .stream()
                .map(roleMapper::toPayload)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GenrePayload>> getGenres() {
        return ResponseEntity.ok(
                musicManager.getGenres()
                        .stream()
                        .map(genreMapper::toPayload)
                        .collect(Collectors.toList())
                        );
    }
}
