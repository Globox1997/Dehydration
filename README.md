# Dehydration
Dehydration is an thirst mod.

### Installation
Dehydration is a mod built for the [Fabric Loader](https://fabricmc.net/). It requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately; all other dependencies are installed with the mod.

### License
Dehydration is licensed under GPLv3.

### Datapacks (Since version 1.3.3)
You can set hydration for items via a datapack. Custom mod items might need to use the dehydration api, check out the DehydrationAPI.class\
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.fandom.com/wiki/Data_Pack)
website and try to create your first one for the vanilla game.\
If you know how to create one, the folder path has to be ```data\dehydration\hydration_items\YOURFILE.json```\
An example for giving an item hydration can be found below:

```json
{
    "1": {
        "replace": false,
        "items": [
            "minecraft:melon_slice"
        ]
    },
    "2": {
        "replace": false,
        "items": [
            "minecraft:potion"
        ]
}
```
