# VoxelVert
**Latest Version:** `G-0.1`

## Introduction
VoxelVert is a converter for many voxel, 3d, or Minecraft formats developed

## File Formats
VoxelVert uses two custom file formats:

### Resource Pack Extractor (`.json`)
The resource pack extractor format is a subset of `JSON` which specifies how to extract block colors out of a default
resource pack.

### Block Color Table (`.colors`)
Block color tables are simple binary files which store the color of each block and id. The specification is as follows:
````EBNF
table = header, {entry};
header = "B", "C", "T", version, entryCount; (*ASCII characters*)
version = int;
entryCount = int;
entry = id, data, color, volume;

id = byte;
data = ? byte in range 0 - 15 ?
color = byte, byte, byte, byte; (* bytes represent alpha, red, green, blue channels *)
volume = ? short in range 0 - 4096 ?
````
