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

package org.proninyaroslav.libretorrent.ui.main;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import org.proninyaroslav.libretorrent.core.FileInTorrent;
import org.proninyaroslav.libretorrent.core.filter.TorrentFilter;
import org.proninyaroslav.libretorrent.core.filter.TorrentFilterCollection;
import org.proninyaroslav.libretorrent.core.model.TorrentEngine;
import org.proninyaroslav.libretorrent.core.model.TorrentEngineListener;
import org.proninyaroslav.libretorrent.core.model.TorrentInfoProvider;
import org.proninyaroslav.libretorrent.core.model.data.TorrentInfo;
import org.proninyaroslav.libretorrent.core.sorting.TorrentSorting;
import org.proninyaroslav.libretorrent.core.sorting.TorrentSortingComparator;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public class MainViewModel extends AndroidViewModel
{
    private TorrentInfoProvider stateProvider;
    private TorrentEngine engine;
    private TorrentSortingComparator sorting = new TorrentSortingComparator(
            new TorrentSorting(TorrentSorting.SortingColumns.none, TorrentSorting.Direction.ASC));
    private TorrentFilter statusFilter = TorrentFilterCollection.all();
    private TorrentFilter dateAddedFilter = TorrentFilterCollection.all();
    private PublishSubject<Boolean> forceSortAndFilter = PublishSubject.create();

    private String searchQuery;
    private TorrentFilter searchFilter = (state) -> {
        if (TextUtils.isEmpty(searchQuery))
            return true;

        String filterPattern = searchQuery.toLowerCase().trim();

        return state.name.toLowerCase().contains(filterPattern);
    };

    public MainViewModel(@NonNull Application application)
    {
        super(application);

        stateProvider = TorrentInfoProvider.getInstance(application);
        engine = TorrentEngine.getInstance(application);
    }

    public Flowable<List<TorrentInfo>> observeAllTorrentsInfo()
    {
        return stateProvider.observeInfoList();
    }

    public Single<List<TorrentInfo>> getAllTorrentsInfoSingle()
    {
        return stateProvider.getInfoListSingle();
    }

    public Flowable<String> observeTorrentsDeleted()
    {
        return stateProvider.observeTorrentsDeleted();
    }

    public void setSort(@NonNull TorrentSortingComparator sorting, boolean force)
    {
        this.sorting = sorting;
        if (force && !sorting.getSorting().getColumnName().equals(TorrentSorting.SortingColumns.none.name()))
            forceSortAndFilter.onNext(true);
    }

    public TorrentSortingComparator getSorting()
    {
        return sorting;
    }

    public void setStatusFilter(@NonNull TorrentFilter statusFilter, boolean force)
    {
        this.statusFilter = statusFilter;
        if (force)
            forceSortAndFilter.onNext(true);
    }

    public void setDateAddedFilter(@NonNull TorrentFilter dateAddedFilter, boolean force)
    {
        this.dateAddedFilter = dateAddedFilter;
        if (force)
            forceSortAndFilter.onNext(true);
    }

    public TorrentFilter getFilter()
    {
        return (state) -> statusFilter.test(state) &&
                dateAddedFilter.test(state) &&
                searchFilter.test(state);
    }

    public void setSearchQuery(@Nullable String searchQuery)
    {
        this.searchQuery = searchQuery;
        forceSortAndFilter.onNext(true);
    }

    public void resetSearch()
    {
        setSearchQuery(null);
    }

    public Observable<Boolean> observeForceSortAndFilter()
    {
        return forceSortAndFilter;
    }

    public void pauseResumeTorrent(@NonNull String id)
    {
        engine.pauseResumeTorrent(id);
    }

    public void deleteTorrents(@NonNull List<String> ids, boolean withFiles)
    {
        engine.deleteTorrents(ids, withFiles);
    }

    public void forceRecheckTorrents(@NonNull List<String> ids)
    {
        engine.forceRecheckTorrents(ids);
    }

    public void forceAnnounceTorrents(@NonNull List<String> ids)
    {
        engine.forceAnnounceTorrents(ids);
    }

    public Flowable<Boolean> observeNeedStartEngine()
    {
        return engine.observeNeedStartEngine();
    }

    public void startEngine()
    {
        engine.start();
    }

    public void requestStopEngine()
    {
        engine.requestStop();
    }

    public void stopEngine()
    {
        engine.forceStop();
    }

    public void pauseAll()
    {
        engine.pauseAll();
    }

    public void resumeAll()
    {
        engine.resumeAll();
    }

    public void resumeIfPausedTorrent(@NonNull String id)
    {
        engine.resumeIfPausedTorrent(id);
    }

    public FileInTorrent getFirstMediaFile(String id)
    {
        return engine.getFirstMediaFile(id);
    }

    public boolean torrentHasMediaFile(String id)
    {
        return engine.torrentHasMediaFile(id);
    }

    public boolean isTorrentNeedStartStreamOnAdded(String id)
    {
        return engine.isTorrentNeedStartStreamOnAdded(id);
    }

    public void addListener(TorrentEngineListener listener)
    {
        engine.addListener(listener);
    }

    public void removeListener(TorrentEngineListener listener)
    {
        engine.removeListener(listener);
    }
}
