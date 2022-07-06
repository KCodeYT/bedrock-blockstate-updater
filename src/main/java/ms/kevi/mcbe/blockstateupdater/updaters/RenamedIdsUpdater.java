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
public class RenamedIdsUpdater implements Updater {

    Map<String, String> renamedIds;

    public static RenamedIdsUpdater create(JsonObject jsonObject) {
        final Map<String, String> renamedIds = new LinkedHashMap<>();

        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
            renamedIds.put(entry.getKey(), entry.getValue().getAsString());

        return new RenamedIdsUpdater(Collections.unmodifiableMap(renamedIds));
    }

    @Override
    public void update(NbtMapBuilder blockState) {
        final String name = (String) blockState.get("name");

        final String renamedId = this.renamedIds.get(name);
        if(renamedId == null) return;

        blockState.putString("name", renamedId);
    }

}
