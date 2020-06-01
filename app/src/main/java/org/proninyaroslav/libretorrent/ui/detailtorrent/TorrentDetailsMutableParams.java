/*
 * 
 *
 * This file is part of BitLord.
 *
 * BitLord is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BitLord is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BitLord.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.libretorrent.ui.detailtorrent;

import android.net.Uri;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class TorrentDetailsMutableParams extends BaseObservable
{
    private String name;
    private Uri dirPath;
    private boolean sequentialDownload = false;
    private boolean prioritiesChanged = false;

    @Bindable
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isSequentialDownload()
    {
        return sequentialDownload;
    }

    public void setSequentialDownload(boolean sequentialDownload)
    {
        this.sequentialDownload = sequentialDownload;
        notifyPropertyChanged(BR.sequentialDownload);
    }

    @Bindable
    public Uri getDirPath()
    {
        return dirPath;
    }

    public void setDirPath(Uri dirPath)
    {
        this.dirPath = dirPath;
        notifyPropertyChanged(BR.dirPath);
    }

    @Bindable
    public boolean isPrioritiesChanged()
    {
        return prioritiesChanged;
    }

    public void setPrioritiesChanged(boolean prioritiesChanged)
    {
        this.prioritiesChanged = prioritiesChanged;
        notifyPropertyChanged(BR.prioritiesChanged);
    }

    @Override
    public String toString()
    {
        return "TorrentDetailsMutableParams{" +
                "name='" + name + '\'' +
                ", dirPath=" + dirPath +
                ", sequentialDownload=" + sequentialDownload +
                ", prioritiesChanged=" + prioritiesChanged +
                '}';
    }
}
