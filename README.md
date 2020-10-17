# VoxelVert

## About
VoxelVert is a converter for many voxel-, 3D-, or Minecraft (file) formats.

### Project Structure
The project consists of three modules:

- `voxelvert-core` which handles the conversion between various internal formats and I/O for file formats
- `voxelvert-ui` is a command line front-end for `voxelvert-core`
- `voxelvert-bukkit` exposes the CLI as a Minecraft command and adds a file manager using the `eisen-inventories` API

### Dependencies
VoxelVert requires WorldEdit and the Spigot API to compile. The exact dependencies can be found in the modules `voxelvert-bukkit`, `voxelvert-ui` and `voxelvert-code`.

There are also dependencies on my own projects, which are:

- [`eisen-inventories`](https://github.com/Eisenwave/eisen-inventories): inventory API for Bukkit
- [`spatium`](https://github.com/Eisenwave/spatium): various Java utilities, data structures
- [`torrens`](https://github.com/Eisenwave/torrens): Java I/O library for image, 3D and other file formats

All projects are written in Java 1.8.

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

version = uint32 (* '2' if flags are present, '1' if not *);
entryCount = uint32;

id = uint8;
data = ? uint8 in range 0 - 15 ?
color = uint8, uint8, uint8, uint8; (* bytes represent alpha, red, green, blue channels *)
flags ? 16-bit bitfield ?
volume = ? uint16 in range 0 - 4096 ?
````
On Linux, you can inspect the data of a BCT file without the header using `xxd -b -s 11 -c 8 my_color_table.bct`
