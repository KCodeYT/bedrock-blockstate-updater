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
public class AddedPropertiesUpdater implements Updater {

    Map<String, List<BlockProperty>> addedProperties;

    public static AddedPropertiesUpdater create(JsonObject jsonObject) {
        final Map<String, List<BlockProperty>> addedProperties = new LinkedHashMap<>();

        for(Map.Entry<String, JsonElement> addedPropertiesEntry : jsonObject.entrySet()) {
            final List<BlockProperty> currentAddedProperties = new ArrayList<>();

            for(Map.Entry<String, JsonElement> entry : addedPropertiesEntry.getValue().getAsJsonObject().entrySet()) {
                final String name = entry.getKey();
                final JsonObject property = entry.getValue().getAsJsonObject();
                final BlockPropertyType type = BlockPropertyType.find(property);

                currentAddedProperties.add(switch(type) {
                    case INT -> new BlockProperty(name, type, property.get(type.getName()).getAsInt());
                    case STRING -> new BlockProperty(name, type, property.get(type.getName()).getAsString());
                    case BYTE -> new BlockProperty(name, type, property.get(type.getName()).getAsByte());
                });
            }

            addedProperties.put(addedPropertiesEntry.getKey(), Collections.unmodifiableList(currentAddedProperties));
        }

        return new AddedPropertiesUpdater(Collections.unmodifiableMap(addedProperties));
    }

    @Override
    public void update(NbtMapBuilder blockState) {
        final String name = (String) blockState.get("name");

        final List<BlockProperty> addedProperties = this.addedProperties.get(name);
        if(addedProperties == null) return;

        final NbtMapBuilder states = ((NbtMap) blockState.get("states")).toBuilder();

        for(BlockProperty addedProperty : addedProperties) {
            switch(addedProperty.getType()) {
                case INT -> states.putInt(addedProperty.getName(), addedProperty.getIntValue());
                case STRING -> states.putString(addedProperty.getName(), addedProperty.getStringValue());
                case BYTE -> states.putByte(addedProperty.getName(), addedProperty.getByteValue());
            }
        }

        blockState.putCompound("states", states.build());
    }

}
