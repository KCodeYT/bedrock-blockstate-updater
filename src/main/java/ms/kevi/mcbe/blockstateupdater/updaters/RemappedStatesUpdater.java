/*
 * Copyright 2022 KCodeYT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ms.kevi.mcbe.blockstateupdater.updaters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ms.kevi.mcbe.blockstateupdater.property.BlockProperty;
import ms.kevi.mcbe.blockstateupdater.property.BlockPropertyType;

import java.util.*;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RemappedStatesUpdater implements Updater {

    Map<String, List<RemappedState>> remappedStates;

    public static RemappedStatesUpdater create(JsonObject jsonObject) {
        final Map<String, List<RemappedState>> remappedStates = new LinkedHashMap<>();

        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            final List<RemappedState> remappedStatesList = new ArrayList<>();

            for(JsonElement remappedState : entry.getValue().getAsJsonArray()) {
                final JsonObject oldState = remappedState.getAsJsonObject().getAsJsonObject("oldState");
                final String newName = remappedState.getAsJsonObject().get("newName").getAsString();
                final JsonObject newState = remappedState.getAsJsonObject().getAsJsonObject("newState");

                final List<BlockProperty> oldStates = new ArrayList<>();

                for(Map.Entry<String, JsonElement> oldStateEntry : oldState.entrySet()) {
                    final String name = oldStateEntry.getKey();
                    final JsonObject property = oldStateEntry.getValue().getAsJsonObject();
                    final BlockPropertyType type = BlockPropertyType.find(property);

                    oldStates.add(switch(type) {
                        case INT -> new BlockProperty(name, type, property.get(type.getName()).getAsInt());
                        case STRING -> new BlockProperty(name, type, property.get(type.getName()).getAsString());
                        case BYTE -> new BlockProperty(name, type, property.get(type.getName()).getAsByte());
                    });
                }

                final List<BlockProperty> newStates = new ArrayList<>();

                for(Map.Entry<String, JsonElement> newStateEntry : newState.entrySet()) {
                    final String name = newStateEntry.getKey();
                    final JsonObject property = newStateEntry.getValue().getAsJsonObject();
                    final BlockPropertyType type = BlockPropertyType.find(property);

                    newStates.add(switch(type) {
                        case INT -> new BlockProperty(name, type, property.get(type.getName()).getAsInt());
                        case STRING -> new BlockProperty(name, type, property.get(type.getName()).getAsString());
                        case BYTE -> new BlockProperty(name, type, property.get(type.getName()).getAsByte());
                    });
                }

                remappedStatesList.add(new RemappedState(Collections.unmodifiableList(oldStates), newName, Collections.unmodifiableList(newStates)));
            }

            remappedStates.put(entry.getKey(), Collections.unmodifiableList(remappedStatesList));
        }

        return new RemappedStatesUpdater(Collections.unmodifiableMap(remappedStates));
    }

    @Override
    public void update(NbtMapBuilder blockState) {
        final String name = (String) blockState.get("name");

        final List<RemappedState> remappedStates = this.remappedStates.get(name);
        if(remappedStates == null) return;

        final NbtMap currentStates = (NbtMap) blockState.get("states");

        for(RemappedState remappedState : remappedStates) {

            boolean correct = true;
            loop:
            for(BlockProperty oldState : remappedState.oldStates) {
                if(!currentStates.containsKey(oldState.getName())) {
                    correct = false;
                    break;
                }

                switch(oldState.getType()) {
                    case INT -> {
                        if((int) currentStates.get(oldState.getName()) != oldState.getIntValue()) {
                            correct = false;
                            break loop;
                        }
                    }
                    case STRING -> {
                        if(!currentStates.get(oldState.getName()).equals(oldState.getStringValue())) {
                            correct = false;
                            break loop;
                        }
                    }
                    case BYTE -> {
                        if((byte) currentStates.get(oldState.getName()) != oldState.getByteValue()) {
                            correct = false;
                            break loop;
                        }
                    }
                }
            }

            if(correct) {
                blockState.putString("name", remappedState.newName);

                final NbtMapBuilder states = NbtMap.builder();

                for(BlockProperty newState : remappedState.newStates) {
                    switch(newState.getType()) {
                        case INT -> states.putInt(newState.getName(), newState.getIntValue());
                        case STRING -> states.putString(newState.getName(), newState.getStringValue());
                        case BYTE -> states.putByte(newState.getName(), newState.getByteValue());
                    }
                }

                blockState.putCompound("states", states.build());
                return;
            }
        }
    }

    @Value
    private static class RemappedState {
        List<BlockProperty> oldStates;
        String newName;
        List<BlockProperty> newStates;
    }

}
