#### FlameAPI - FlameLoader API to create mods

FlameAPI is what lets you make mods with FlameLoader, without spending weeks just finding the classes, and years porting to every individual version of the game, a small and simple Mod Loader which aims to not modify any Minecraft code, making it available for all versions (kinda)

FlameMC lacks pretty much everything that other mod loaders have, so that's where the FlameAPI comes in. It automates cross version compatability by scanning classes at runtime to try to find registries, then using reflection allows you to access and use those.

Usage:
```
repositories {
    [...]
    maven { url 'https://jitpack.io' }
}

dependencies {
    [...]
    implementation 'com.github.GiantLuigi4:FlameAPI:[commit id]'
    implementation 'com.github.GiantLuigi4:FlameMC:79e69be'
}
```

Current commit id:`45ba4e9`

