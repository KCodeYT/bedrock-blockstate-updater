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

import java.util.*;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RemovedPropertiesUpdater implements Updater {

    Map<String, List<String>> removedProperties;

    public static RemovedPropertiesUpdater create(JsonObject jsonObject) {
        final Map<String, List<String>> removedProperties = new LinkedHashMap<>();

        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            final List<String> removedPropertiesList = new ArrayList<>();

            for(JsonElement removedPropertyEntry : entry.getValue().getAsJsonArray())
                removedPropertiesList.add(removedPropertyEntry.getAsString());

            removedProperties.put(entry.getKey(), Collections.unmodifiableList(removedPropertiesList));
        }

        return new RemovedPropertiesUpdater(Collections.unmodifiableMap(removedProperties));
    }

    @Override
    public void update(NbtMapBuilder blockState) {
        final String name = (String) blockState.get("name");

        final List<String> removedProperties = this.removedProperties.get(name);
        if(removedProperties == null) return;

        final NbtMapBuilder states = ((NbtMap) blockState.get("states")).toBuilder();

        for(String removedProperty : removedProperties) states.remove(removedProperty);

        blockState.putCompound("states", states.build());
    }

}
