/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.dsaudio.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link DSAudioBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Wim Wilms - Initial contribution
 */
@NonNullByDefault
public class DSAudioBindingConstants {

    private static final String BINDING_ID = "dsaudio";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_TEST = new ThingTypeUID(BINDING_ID, "dsaudio_thing");

    // List of all Channel ids
    public static final String CHANNEL_1 = "dsaudio_channel_1";
    public static final String ARTIST_CHANNEL = "artist";
    public static final String TITLE_CHANNEL = "title";
    public static final String COVERART_CHANNEL = "coverartdata";
}
