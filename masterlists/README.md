# Master List Json Format
## Mod Master List Entry
The primary mod list entry. This entry format should be used whenever possible.

```json
{
  "modId": "examplemod",
  "version": "1.0.0",
  "isMinVersion": false,
  "isMaxVersion": true,
  "isVersionExclusive": false,
  "status": "DEPRECATED",
  "action": "REMOVE",
  "replacementModName": "Example Mod Fork",
  "replacementModLink": "https://www.curseforge.com/minecraft/mc-mods/packcompanion",
  "replacementModVersion": "2.0.0",
  "message": "Example Mod.",
  "patchList": [],
  "isCleanroom": false
}
```


| Key                     | Required | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|:------------------------|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `modId`                 | Required | The mod id to search for.                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `version`               | Optional | The version string of this mod entry.                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `isMinVersion`          | Optional | If the version string is the minimum version allowed.                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `isMaxVersion`          | Optional | If the version string is the maximum version allowed.                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `isVersionExclusive`    | Optional | Inverts the version check logic, causing any version of this mod loaded outside the designated version range to generate companion output.                                                                                                                                                                                                                                                                                                                                               |
| `status`                | Required | <li>`DEPRECATED` - This mod has been superseded by a fork or merged into another project.<li>`PROBLEMATIC` - This mod causes issues when included in modpacks.                                                                                                                                                                                                                                                                                                                           |
| `action`                | Required | <ul><li>`REMOVE` - Use when the mod causes should be removed without replacement.<li>`DOWNGRADE` - Use when a mod should be downgraded to a previous version.<li>`UPGRADE` - Use when a mod should be upgraded to a new version.<li>`REPLACE` - Use when the mod has been forked or merged into another project.<li>`INCLUDE` - Used to generate a list of recommended patches.<li>`INFO` - Used to provide info. This should only be used if all other action types are not applicable. |
| `replacementModName`    | Optional | The name of the replacement mod. Used to generate hyperlink text.                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `replacementModLink`    | Optional | The hyperlink of the replacement mod. Used to generate hyperlink text.                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `replacementModVersion` | Optional | The replacement version of the mod. Used only when `UPGRADE` or `DOWNGRADE` action is used.                                                                                                                                                                                                                                                                                                                                                                                              |
| `message`               | Required | A short message describing why this mod is listed.                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `patchList`             | Optional | A list of necessary patches required for this mod to function. See #Patch List Entry for more information.                                                                                                                                                                                                                                                                                                                                                                               |
| `isCleanroom`           | Optional | If this mod entry is specific to the Cleanroom Mod Loader.                                                                                                                                                                                                                                                                                                                                                                                                                               |

---

## Class Master List Entry
The class check or problematic version check entry format. This should only be used when mods use incorrect version strings or are core mods without mod containers.
```json
{
  "modId": "examplemod",
  "verification": "CLASSLOADED",
  "className": "com.examplemod.ExampleMod",
  "versionHash": "710d64342d41042b7d6ced891448e727",
  "status": "DEPRECATED",
  "action": "REMOVE",
  "replacementModName": "Example Mod Fork",
  "replacementModLink": "https://www.curseforge.com/minecraft/mc-mods/packcompanion",
  "replacementModVersion": "2.0.0",
  "message": "Example Mod.",
  "patchList": [],
  "isCleanroom": false
}
```

| Key                     | Required | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|:------------------------|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `modId`                 | Required | The mod id to search for.                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `verification`          | Optional | <ul><li>`HASHMATCH` - Used to match `versionHash` with the MD5 Hash of the source mod's Jar file.<li>`CLASSLOADED` - Used to check for a loaded class with `className`                                                                                                                                                                                                                                                                                                                   |
| `className`             | Optional | The full class name to search. Used with `CLASSLOADED`.                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `versionHash`           | Optional | The version string hash value. Used with `HASHMATCH`.                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `status`                | Required | <ul><li>`DEPRECATED` - This mod has been superseded by a fork or merged into another project.<li>`PROBLEMATIC` - This mod causes issues when included in modpacks.                                                                                                                                                                                                                                                                                                                       |
| `action`                | Required | <ul><li>`REMOVE` - Use when the mod causes should be removed without replacement.<li>`DOWNGRADE` - Use when a mod should be downgraded to a previous version.<li>`UPGRADE` - Use when a mod should be upgraded to a new version.<li>`REPLACE` - Use when the mod has been forked or merged into another project.<li>`INCLUDE` - Used to generate a list of recommended patches.<li>`INFO` - Used to provide info. This should only be used if all other action types are not applicable. |
| `replacementModName`    | Optional | The name of the replacement mod. Used to generate hyperlink text.                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `replacementModLink`    | Optional | The hyperlink of the replacement mod. Used to generate hyperlink text.                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `replacementModVersion` | Optional | The replacement version of the mod. Used only when `UPGRADE` or `DOWNGRADE` action is used.                                                                                                                                                                                                                                                                                                                                                                                              |
| `message`               | Required | A short message describing why this mod is listed.                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `patchList`             | Optional | A list of necessary patches required for this mod to function. See #Patch List Entry for more information.                                                                                                                                                                                                                                                                                                                                                                               |
| `isCleanroom`           | Optional | If this mod entry is specific to the Cleanroom Mod Loader.                                                                                                                                                                                                                                                                                                                                                                                                                               |

---

## Config Master List Entry
Entry format used to locate problematic configuration values.
```json
{
  "modId": "examplemod",
  "version": "1.0.0",
  "isMinVersion": false,
  "isMaxVersion": true,
  "settings": []
}
```

| Key            | Required | Description                                                                                               |
|:---------------|:--------:|:----------------------------------------------------------------------------------------------------------|
| `modId`        | Required | The mod id to search for.                                                                                 |
| `version`      | Optional | The version string of this mod entry.                                                                     |
| `isMinVersion` | Optional | If the version string is the minimum version allowed.                                                     |
| `isMaxVersion` | Optional | If the version string is the maximum version allowed.                                                     |
| `settings`     | Required | A list of problematic configuration settings in this mod. See #Config Setting Entry for more information. |

### Config Setting Entry
```json
{
  "name": "Problematic Setting",
  "field": "com.examplemod.ConfigHandler#problematicSetting",
  "type": "",
  "value": "",
  "shouldMatch": true,
  "message": "",
  "dependencies": []
}
```

| Key              | Required | Description                                                                                                                   |
|:-----------------|:--------:|:------------------------------------------------------------------------------------------------------------------------------|
| `name`           | Required | The name of the configuration setting found in the config file.                                                               |
| `field`          | Required | The field used for this configuration value. Format:<ul><li>`classpath#staticField`<li>`classpath#staticField#instancedField` |
| `type`           | Required | The configuration field object type.<ul><li>`boolean`<li>`int`<li>`double`<li>`float`<li>`string`                             |
| `value`          | Required | The expected value that will cause problematic interactions.                                                                  |
| `shouldMatch`    | Required | If the field value should match the `value` exactly.                                                                          |
| `message`        | Required | A short message describing issues caused by this config setting.                                                              |
| `dependencyMode` | Optional | If the dependency list should be treated with `OR` or `AND` logic                                                             |
| `dependencies`   | Optional | A list of required mod dependencies for this setting to cause issues.                                                         |
| `dependencyMode` | Optional | If the dependency list should be treated with `OR` or `AND` logic                                                             |



### Config Setting Dependency Entry
```json
{
  "modId": "examplemod",
  "version": "1.0.0",
  "isMinVersion": true,
  "isMaxVersion": false,
  "className": "com.examplemod.ExampleMod",
  "classLoaded": false
}
```

| Key            | Required | Description                                                                                        |
|:---------------|:--------:|:---------------------------------------------------------------------------------------------------|
| `modId`        | Required | The mod id to search for. Either `modId` or `className` are required for each config entry.        |
| `version`      | Optional | The version string of this mod entry.                                                              |
| `isMinVersion` | Optional | If the version string is the minimum version allowed.                                              |
| `isMaxVersion` | Optional | If the version string is the maximum version allowed.                                              |
| `className`    | Optional | The full class name to scan for. Either `modId` or `className` are required for each config entry. |
| `classLoaded`  | Optional |                                                                                                    |


## Sub Entries
### Patch List Entry
```json
{
  "modName": "Example Mod",
  "modId": "examplemod",
  "version": "1.0.0",
  "modLink": "https://www.curseforge.com/minecraft/mc-mods/packcompanion",
  "modDescription": ""
}
```

| Key              | Required | Description                                         |
|:-----------------|:--------:|:----------------------------------------------------|
| `modName`        | Required | The patch mod name.                                 |
| `modId`          | Required | The mod id to search for.                           |
| `version`        | Optional | The version string of this mod entry.               |
| `modLink`        | Required | The patch mod website url.                          |
| `modDescription` | Required | A short description explaining what the patch does. |
