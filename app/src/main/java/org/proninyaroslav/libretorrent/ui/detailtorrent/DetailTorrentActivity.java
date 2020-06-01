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

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.proninyaroslav.libretorrent.R;
import org.proninyaroslav.libretorrent.core.model.TorrentInfoProvider;
import org.proninyaroslav.libretorrent.core.utils.Utils;
import org.proninyaroslav.libretorrent.ui.FragmentCallback;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DetailTorrentActivity extends AppCompatActivity
        implements FragmentCallback
{
    @SuppressWarnings("unused")
    private static final String TAG = DetailTorrentActivity.class.getSimpleName();

    public static final String TAG_TORRENT_ID = "torrent_id";

    private DetailTorrentFragment detailTorrentFragment;
    private TorrentInfoProvider infoProvider;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        setTheme(Utils.getAppTheme(getApplicationContext()));
        super.onCreate(savedInstanceState);

        if (Utils.isTwoPane(this)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_detail_torrent);

        infoProvider = TorrentInfoProvider.getInstance(getApplicationContext());

        detailTorrentFragment = (DetailTorrentFragment)getSupportFragmentManager()
                .findFragmentById(R.id.detail_torrent_fragmentContainer);

        if (detailTorrentFragment != null)
            detailTorrentFragment.setTorrentId(getIntent().getStringExtra(TAG_TORRENT_ID));
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        disposables.clear();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        subscribeTorrentDeleted();
    }

    private void subscribeTorrentDeleted()
    {
        disposables.add(infoProvider.observeTorrentsDeleted()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((id) -> {
                    if (detailTorrentFragment != null &&
                        id.equals(detailTorrentFragment.getTorrentId()))
                        finish();
                }));
    }

    @Override
    public void onFragmentFinished(@NonNull Fragment f, Intent intent,
                                   @NonNull ResultCode code)
    {
        finish();
    }
}
