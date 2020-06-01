package org.proninyaroslav.libretorrent.core;

import java.io.File;

public class FileInTorrent
{
    private File file;
    private String torrentID;
    private int index;
    private boolean isDownloaded;

    public FileInTorrent(File file, String torrentID, int index, boolean isDownloaded)
    {
        this.file = file;
        this.torrentID = torrentID;
        this.index = index;
        this.isDownloaded = isDownloaded;
    }

    public File getFile()
    {
        return this.file;
    }

    public String getTorrentID()
    {
        return this.torrentID;
    }

    public int getIndex()
    {
        return this.index;
    }

    public boolean getIsDownloaded()
    {
        return this.isDownloaded;
    }
}
