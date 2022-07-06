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

package ms.kevi.mcbe.blockstateupdater;

import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import ms.kevi.mcbe.blockstateupdater.updaters.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
public class BlockStateUpdaterUtil {

    public int createVersion(int major, int minor, int patch, int revision) {
        return (major << 24) | (minor << 16) | (patch << 8) | revision;
    }

    public Set<Updater> createUpdaters(JsonObject jsonObject) {
        final Set<Updater> updaters = new LinkedHashSet<>();

        if(jsonObject.has("addedProperties"))
            updaters.add(AddedPropertiesUpdater.create(
                    jsonObject.getAsJsonObject("addedProperties")
            ));
        if(jsonObject.has("remappedPropertyValues") && jsonObject.has("remappedPropertyValuesIndex"))
            updaters.add(RemappedPropertyValuesUpdater.create(
                    jsonObject.getAsJsonObject("remappedPropertyValues"),
                    jsonObject.getAsJsonObject("remappedPropertyValuesIndex")
            ));
        if(jsonObject.has("remappedStates"))
            updaters.add(RemappedStatesUpdater.create(
                    jsonObject.getAsJsonObject("remappedStates")
            ));
        if(jsonObject.has("removedProperties"))
            updaters.add(RemovedPropertiesUpdater.create(
                    jsonObject.getAsJsonObject("removedProperties")
            ));
        if(jsonObject.has("renamedIds"))
            updaters.add(RenamedIdsUpdater.create(
                    jsonObject.getAsJsonObject("renamedIds")
            ));
        if(jsonObject.has("renamedProperties"))
            updaters.add(RenamedPropertiesUpdater.create(
                    jsonObject.getAsJsonObject("renamedProperties")
            ));

        return updaters.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(updaters);
    }

}
