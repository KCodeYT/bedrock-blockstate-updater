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

import lombok.Value;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
public class BlockProperty {

    String name;
    BlockPropertyType type;
    Object value;

    public int getIntValue() {
        return (int) this.value;
    }

    public String getStringValue() {
        return (String) this.value;
    }

    public byte getByteValue() {
        return (byte) this.value;
    }

}
