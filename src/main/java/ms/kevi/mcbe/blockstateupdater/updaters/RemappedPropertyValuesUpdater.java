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
import ms.kevi.mcbe.blockstateupdater.property.BlockPropertyType;

import java.util.*;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RemappedPropertyValuesUpdater implements Updater {

    Map<String, Map<String, List<RemappedProperty>>> remappedProperties;

    public static RemappedPropertyValuesUpdater create(JsonObject jsonObjectValues, JsonObject jsonObjectIndex) {
        final Map<String, Map<String, List<RemappedProperty>>> remappedProperties = new LinkedHashMap<>();

        for(Map.Entry<String, JsonElement> entry : jsonObjectValues.entrySet()) {
            final Map<String, List<RemappedProperty>> remappedPropertiesForBlock = new LinkedHashMap<>();

            for(Map.Entry<String, JsonElement> remappedProperty : entry.getValue().getAsJsonObject().entrySet()) {
                final List<RemappedProperty> currentRemappedProperties = new ArrayList<>();

                for(JsonElement remappedEntry : jsonObjectIndex.getAsJsonArray(remappedProperty.getValue().getAsString())) {
                    final JsonObject oldValue = remappedEntry.getAsJsonObject().getAsJsonObject("old");
                    final JsonObject newValue = remappedEntry.getAsJsonObject().getAsJsonObject("new");

                    final BlockPropertyType oldType = BlockPropertyType.find(oldValue);
                    final BlockPropertyType newType = BlockPropertyType.find(newValue);

                    currentRemappedProperties.add(new RemappedProperty(oldType, switch(oldType) {
                        case INT -> oldValue.get(oldType.getName()).getAsInt();
                        case STRING -> oldValue.get(oldType.getName()).getAsString();
                        case BYTE -> oldValue.get(oldType.getName()).getAsByte();
                    }, newType, switch(newType) {
                        case INT -> newValue.get(newType.getName()).getAsInt();
                        case STRING -> newValue.get(newType.getName()).getAsString();
                        case BYTE -> newValue.get(newType.getName()).getAsByte();
                    }));
                }

                remappedPropertiesForBlock.put(remappedProperty.getKey(), Collections.unmodifiableList(currentRemappedProperties));
            }

            remappedProperties.put(entry.getKey(), Collections.unmodifiableMap(remappedPropertiesForBlock));
        }

        return new RemappedPropertyValuesUpdater(Collections.unmodifiableMap(remappedProperties));
    }

    @Override
    public void update(NbtMapBuilder blockState) {
        final String name = (String) blockState.get("name");

        final Map<String, List<RemappedProperty>> remappedProperties = this.remappedProperties.get(name);
        if(remappedProperties == null) return;

        final NbtMapBuilder states = ((NbtMap) blockState.get("states")).toBuilder();

        boolean modified = false;
        for(Map.Entry<String, Object> entry : states.entrySet()) {
            final List<RemappedProperty> remappedPropertyValuesIndex = remappedProperties.get(entry.getKey());
            if(remappedPropertyValuesIndex == null) continue;

            boolean currentModified = false;
            for(RemappedProperty propertyValuesIndex : remappedPropertyValuesIndex) {
                switch(propertyValuesIndex.oldType) {
                    case INT -> {
                        if((int) entry.getValue() == (int) propertyValuesIndex.oldValue) {
                            entry.setValue(propertyValuesIndex.newValue);
                            currentModified = true;
                        }
                    }
                    case STRING -> {
                        if(entry.getValue().equals(propertyValuesIndex.oldValue)) {
                            entry.setValue(propertyValuesIndex.newValue);
                            currentModified = true;
                        }
                    }
                    case BYTE -> {
                        if((byte) entry.getValue() == (byte) propertyValuesIndex.oldValue) {
                            entry.setValue(propertyValuesIndex.newValue);
                            currentModified = true;
                        }
                    }
                }

                if(currentModified) break;
            }

            if(currentModified) modified = true;
        }

        if(modified) blockState.putCompound("states", states.build());
    }

    @Value
    private static class RemappedProperty {
        BlockPropertyType oldType;
        Object oldValue;
        BlockPropertyType newType;
        Object newValue;
    }

}
