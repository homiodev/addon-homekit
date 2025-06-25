package org.homio.addon.homekit;

import org.homio.api.AddonConfiguration;
import org.homio.api.AddonEntrypoint;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@AddonConfiguration
public class HomekitEntrypoint implements AddonEntrypoint {

    @Override
    public void init() {

    }

    @Override
    public URL getAddonImageURL() {
        return getResource("images/HomekitEntity.png");
    }
}
