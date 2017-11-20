# VoxelVert
**Latest Version:** `G-0.1`

## Introduction
VoxelVert is a converter for many voxel-, 3D-, or Minecraft (file) formats.

## Custom File Formats
VoxelVert uses two custom file formats:

### Block Color Extractor
**Media Type**: `application/x.voxelvert-bce+json`  
**File Suffix**: `.json`

The block color extractor format is a subset of `JSON` which specifies how to extract block colors out of a default
resource pack.

### Block Color Table
**Media Type**: `application/x.voxelvert-bct`  
**File Suffix**: `.bct`

Block color tables are simple binary files which store the color of each block and id. The specification is as follows:
````EBNF
table = header, {entry};
header = "B", "C", "T", version, entryCount; (*ASCII characters*)
entry = id, data, color, volume;

version = uint32;
entryCount = uint32;

id = uint8;
data = ? uint8 in range 0 - 15 ?
color = uint8, uint8, uint8, uint8; (* bytes represent alpha, red, green, blue channels *)
volume = ? uint16 in range 0 - 4096 ?
````
On Linux, you can inspect the data of a BCT file without the header using `xxd -b -s 11 -c 8 my_color_table.bct`

## Plugin.yml
````yaml
name: VoxelVert
main: org.eisenwave.vv.bukkit.VoxelVertPlugin
version: 0.1
author: Eisenwave
depend: [WorldEdit]
softdepend: [BulktEdit]

permissions:
  vv.cmd.convert:
    default: op
  vv.cmd.voxelvert:
    default: op
  vv.cmd.voxelvert-list:
    default: op
  vv.cmd.voxelvert-remove:
    default: op
  vv.cmd.voxelvert-move:
    default: op
  vv.cmd.vvadmin:
    default: op

commands:
  convert:
    permission: vv.cmd.convert
    aliases: [c]
    description: convert files
  voxelvert:
    permission: vv.cmd.voxelvert
    aliases: [vv]
    description: configuration/stats command

  voxelvert-remove:
    permission: vv.cmd.voxelvert-remove
    aliases: [vv-rm]
    description: remove a file
  voxelvert-move:
    permission: vv.cmd.voxelvert-move
    aliases: [vv-mv]
    description: move a file
  voxelvert-copy:
    permission: vv.cmd.voxelvert-copy
    aliases: [vv-cp]
    description: copy a file
  voxelvert-list:
    permission: vv.cmd.voxelvert-list
    aliases: [vv-ls]
    description: list files
````
