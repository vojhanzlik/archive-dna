# Archive DNA

A tool for parsing archives and comparing them using Jaccard Index.


- Parses ZIP archives and extracts metadata from `.java` and `.kt` source files
- Generates hashes for each file entry based on name, size, CRC
- Compares two archives using Jaccard Index

## Usage

### Parse an archive

```bash
./gradlew run --args="parse plugin.zip output.json"
```

### Compare two archives

```bash
./gradlew run --args="compare output1.json output2.json"
```

## Build

```bash
./gradlew build
```

## Output

Parsing output:
- Archive name and size
- File entries with metadata
- Total entry count

Comparison output:
- Jaccard Index (0.0 - 1.0)
- Structural overlap percentage
