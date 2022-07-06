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

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.util.VarInts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LegacyUpdater implements Updater {

    Map<String, List<R12ToR18Block>> r12ToR18Blocks;

    public static LegacyUpdater createLegacy(InputStream binaryMapping) throws IOException {
        final Map<String, List<R12ToR18Block>> r12ToR18Blocks = new HashMap<>();

        try(final DataInputStream dataInputStream = new DataInputStream(binaryMapping);
            final NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(binaryMapping)) {

            while(true) {
                try {
                    final byte[] r12BlockNameBytes = new byte[VarInts.readUnsignedInt(dataInputStream)];
                    final String r12BlockName = new String(Arrays.copyOf(r12BlockNameBytes, dataInputStream.read(r12BlockNameBytes)));
                    final short r12Metadata = Short.reverseBytes(dataInputStream.readShort());
                    final NbtMap r18BlockState = (NbtMap) nbtInputStream.readTag();

                    r12ToR18Blocks.computeIfAbsent(r12BlockName, k -> new ArrayList<>()).add(new R12ToR18Block(r12BlockName, r12Metadata, r18BlockState));
                } catch(EOFException e) {
                    break;
                }
            }
        }

        r12ToR18Blocks.replaceAll((k, v) -> Collections.unmodifiableList(v));

        return new LegacyUpdater(Collections.unmodifiableMap(r12ToR18Blocks));
    }

    @Override
    public void update(NbtMapBuilder blockState) {
        final String name = (String) blockState.get("name");

        if(blockState.containsKey("states")) return;

        final Short metadata = (Short) blockState.remove("val");
        if(metadata == null) return;

        final List<R12ToR18Block> r12ToR18Blocks = this.r12ToR18Blocks.get(name);
        if(r12ToR18Blocks == null) return;

        for(R12ToR18Block r12ToR18Block : r12ToR18Blocks) {
            if(r12ToR18Block.r12Metadata == metadata) {
                blockState.putCompound("states", r12ToR18Block.getR18BlockState().getCompound("states").toBuilder().build());
                return;
            }
        }
    }

    @Value
    private static class R12ToR18Block {
        String r12BlockName;
        short r12Metadata;
        NbtMap r18BlockState;
    }

}
