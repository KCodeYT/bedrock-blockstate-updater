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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import ms.kevi.mcbe.blockstateupdater.updaters.LegacyUpdater;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class BlockStateUpdaters {

    private static final String BINARY_MAPPING_DIR = "BedrockBlockUpgradeSchema";
    private static final String SCHEMAS_DIR = "BedrockBlockUpgradeSchema/nbt_upgrade_schema";
    private static final Gson GSON = new Gson();
    private static final List<BlockStateUpdater> UPDATERS;

    static {
        try {
            UPDATERS = Arrays.asList(
                    createLegacy(),
                    create("0001_1.9.0_to_1.10.0.json"),
                    create("0011_1.10.0_to_1.12.0.json"),
                    create("0021_1.12.0_to_1.13.0.json"),
                    create("0031_1.13.0_to_1.14.0.json"),
                    create("0041_1.14.0_to_1.16.0.57_beta.json"),
                    create("0051_1.16.0.57_beta_to_1.16.0.59_beta.json"),
                    create("0061_1.16.0.59_beta_to_1.16.0.68_beta.json"),
                    create("0071_1.16.0_to_1.16.100.json"),
                    create("0081_1.16.200_to_1.16.210.json"),
                    create("0091_1.17.10_to_1.17.30.json"),
                    create("0101_1.17.30_to_1.17.40.json"),
                    create("0111_1.18.0_to_1.18.10.json"),
                    create("0121_1.18.10_to_1.18.20.27_beta.json"),
                    create("0131_1.18.20.27_beta_to_1.18.30.json")
            );
        } catch(IOException e) {
            throw new RuntimeException("Could not create updaters", e);
        }
    }

    private static BlockStateUpdater createLegacy() throws IOException {
        final String resourceName = "1.12.0_to_1.18.10_blockstate_map.bin";

        try(final InputStream inputStream = BlockStateUpdaters.class.getClassLoader().getResourceAsStream(BINARY_MAPPING_DIR + "/" + resourceName)) {
            if(inputStream == null) throw new IOException("Could not find resource " + resourceName);

            return new BlockStateUpdater(
                    BlockStateUpdaterUtil.createVersion(1, 18, 10, 1),
                    Collections.singleton(LegacyUpdater.createLegacy(inputStream))
            );
        }
    }

    private static BlockStateUpdater create(String resourceName) throws IOException {
        try(final InputStream inputStream = BlockStateUpdaters.class.getClassLoader().getResourceAsStream(SCHEMAS_DIR + "/" + resourceName)) {
            if(inputStream == null) throw new IOException("Could not find resource " + resourceName);

            try(final Reader reader = new InputStreamReader(inputStream)) {
                return BlockStateUpdater.of(GSON.fromJson(reader, JsonObject.class));
            }
        }
    }

    public static NbtMap update(NbtMap blockState) {
        final NbtMapBuilder blockStateBuilder = blockState.toBuilder();

        BlockStateUpdater lastUpdater = null;
        for(BlockStateUpdater updater : UPDATERS) {
            if((int) blockStateBuilder.getOrDefault("version", 0) > updater.getVersion()) continue;

            updater.update(blockStateBuilder);
            lastUpdater = updater;
        }

        if(lastUpdater != null)
            blockStateBuilder.putInt("version", lastUpdater.getVersion());

        return blockStateBuilder.build();
    }

}
