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

package ms.kevi.mcbe.blockstateupdater.property;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Getter
@RequiredArgsConstructor
public enum BlockPropertyType {

    INT("int"),
    STRING("string"),
    BYTE("byte");

    private static final BlockPropertyType[] VALUES = values();

    public static BlockPropertyType find(JsonObject jsonObject) {
        for(BlockPropertyType type : VALUES) {
            if(jsonObject.has(type.name)) return type;
        }

        throw new IllegalArgumentException("No BlockPropertyType found for " + jsonObject);
    }

    private final String name;

}
