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
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.NextPreviousType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openhab.binding.dsaudio.internal.DSAudioBindingConstants.CHANNEL_1;
import static org.openhab.binding.dsaudio.internal.DSAudioBindingConstants.COVERART_CHANNEL;

/**
 * The {@link DSAudioHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Wim Wilms - Initial contribution
 */
@NonNullByDefault
public class DSAudioHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(DSAudioHandler.class);

    private @Nullable DSAudioConfiguration config;
    private DSAudio dsAudio;

    public DSAudioHandler(Thing thing, DSAudio dsAudio) {
        super(thing);
        this.dsAudio = dsAudio;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            } else if (command instanceof PlayPauseType) {
                if (command == PlayPauseType.PLAY) {
                    dsAudio.startPlaying(getConfigAs(DSAudioConfiguration.class));
                }
                if (command == PlayPauseType.PAUSE) {
                    dsAudio.stopPlaying(getConfigAs(DSAudioConfiguration.class));
                }
            } else if (command instanceof NextPreviousType) {
                if (command == NextPreviousType.NEXT) {
                    dsAudio.next(getConfigAs(DSAudioConfiguration.class));
                } else {
                    dsAudio.previous(getConfigAs(DSAudioConfiguration.class));
                }
            }
        }

        if (COVERART_CHANNEL.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                this.updateState(channelUID, dsAudio.getCoverArt(getConfigAs(DSAudioConfiguration.class)).map(rawType -> (State) rawType).orElse(UnDefType.UNDEF));
            }
        }
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing!");
        config = getConfigAs(DSAudioConfiguration.class);

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.ONLINE);

        // Example for background initialization:
       /* scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });*/
        scheduler.submit(this::connect);

        logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    private void connect() {
    }
}
