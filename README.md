# bedrock-blockstate-updater

A java api, for Minecraft: Bedrock Edition, which allows updating nbt blockstates to the latest version using the
[bedrock block upgrade schema](https://github.com/pmmp/BedrockBlockUpgradeSchema) provided by pmmp.

### Usage

``` java
NbtMap updatedBlockState = BlockStateUpdaters.update(NbtMap.builder().
        putString("name", "minecraft:stone").
        putCompound("states", NbtMap.builder().
                putString("stone_type", "granite").
                build()).
        build());
```

### Maven

1. Clone this repository for the version you want to use.
2. Run `mvn clean install` to build the jar.
3. Add the maven dependency to your projects pom.xml.

``` xml
<dependencies>
    <dependency>
        <groupId>ms.kevi</groupId>
        <artifactId>bedrock-blockstate-updater</artifactId>
        <version>YOUR_VERSION</version>
    </dependency>
<dependencies>
```

### Versions

- 1.10.0
- 1.12.0
- 1.13.0
- 1.14.0
- 1.16.0.57_beta
- 1.16.0.59_beta