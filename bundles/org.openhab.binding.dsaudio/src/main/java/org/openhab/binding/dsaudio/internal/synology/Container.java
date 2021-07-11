package org.openhab.binding.dsaudio.internal.synology;

import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public class Container {
    private String type;
    private String id;

    public Container(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
