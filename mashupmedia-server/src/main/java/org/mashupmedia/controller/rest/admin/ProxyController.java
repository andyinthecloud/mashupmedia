package org.mashupmedia.controller.rest.admin;

import org.apache.commons.lang3.BooleanUtils;
import org.mashupmedia.constants.MashUpMediaConstants;
import org.mashupmedia.dto.admin.ProxyPayload;
import org.mashupmedia.service.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/proxy")
public class ProxyController {

    @Autowired
    private ConfigurationManager configurationManager;

    @GetMapping("/")
    public ProxyPayload getProxy() {
        boolean enabled = BooleanUtils.toBoolean(configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_ENABLED));
        String url = configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_URL);
        String port = configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_PORT);
        String username = configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_USERNAME);
        String password = configurationManager.getConfigurationValue(MashUpMediaConstants.PROXY_PASSWORD);

        return ProxyPayload.builder()
                .enabled(enabled)
                .url(url)
                .port(port)
                .username(username)
                .password(password)
                .build();

    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public ProxyPayload saveProxy(@RequestBody ProxyPayload proxyDTO) {
        configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_URL, proxyDTO.getUrl());
        configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_PORT, proxyDTO.getPort());
        configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_USERNAME, proxyDTO.getUsername());
        configurationManager.saveConfiguration(MashUpMediaConstants.PROXY_PASSWORD, proxyDTO.getPassword());
        return proxyDTO;
    }


}
