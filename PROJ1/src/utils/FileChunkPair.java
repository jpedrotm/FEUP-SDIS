package utils;

import filesystem.Chunk;
import filesystem.FileChunk;

public class FileChunkPair {
    public FileChunk file;
    public Chunk chunk;

    public FileChunkPair(FileChunk file, Chunk chunk)  {
        this.file = file;
        this.chunk = chunk;
    }
}
