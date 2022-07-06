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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RenamedPropertiesUpdater implements Updater {

    Map<String, Map<String, String>> renamedProperties;

    public static RenamedPropertiesUpdater create(JsonObject jsonObject) {
        final Map<String, Map<String, String>> renamedProperties = new LinkedHashMap<>();

        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            final Map<String, String> renamedPropertiesForBlock = new LinkedHashMap<>();

            for(Map.Entry<String, JsonElement> renamedProperty : entry.getValue().getAsJsonObject().entrySet())
                renamedPropertiesForBlock.put(renamedProperty.getKey(), renamedProperty.getValue().getAsString());

            renamedProperties.put(entry.getKey(), Collections.unmodifiableMap(renamedPropertiesForBlock));
        }

        return new RenamedPropertiesUpdater(Collections.unmodifiableMap(renamedProperties));
    }

    @Override
    public void update(NbtMapBuilder blockState) {
        final String name = (String) blockState.get("name");

        final Map<String, String> renamedProperties = this.renamedProperties.get(name);
        if(renamedProperties == null) return;

        final NbtMapBuilder states = ((NbtMap) blockState.get("states")).toBuilder();

        for(Map.Entry<String, String> entry : renamedProperties.entrySet())
            states.rename(entry.getKey(), entry.getValue());

        blockState.putCompound("states", states.build());
    }

}
