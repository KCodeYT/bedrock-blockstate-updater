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
import com.nukkitx.nbt.NbtMapBuilder;
import lombok.Value;
import ms.kevi.mcbe.blockstateupdater.updaters.Updater;

import java.util.Set;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
public class BlockStateUpdater {

    int version;
    Set<Updater> updaters;

    public static BlockStateUpdater of(JsonObject jsonObject) {
        final int maxVersionMajor = jsonObject.get("maxVersionMajor").getAsInt();
        final int maxVersionMinor = jsonObject.get("maxVersionMinor").getAsInt();
        final int maxVersionPatch = jsonObject.get("maxVersionPatch").getAsInt();
        final int maxVersionRevision = jsonObject.get("maxVersionRevision").getAsInt();

        final int version = BlockStateUpdaterUtil.createVersion(maxVersionMajor, maxVersionMinor, maxVersionPatch, maxVersionRevision);
        final Set<Updater> updaters = BlockStateUpdaterUtil.createUpdaters(jsonObject);

        return new BlockStateUpdater(version, updaters);
    }

    public void update(NbtMapBuilder blockState) {
        for(Updater updater : this.updaters) updater.update(blockState);
    }

}
