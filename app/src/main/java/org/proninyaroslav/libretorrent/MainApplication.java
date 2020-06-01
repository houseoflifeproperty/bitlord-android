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

package org.proninyaroslav.libretorrent;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraDialog;
import org.acra.annotation.AcraMailSender;
import org.libtorrent4j.swig.libtorrent;
import org.proninyaroslav.libretorrent.core.system.LibTorrentSafAdapter;
import org.proninyaroslav.libretorrent.core.utils.Utils;
import org.proninyaroslav.libretorrent.ui.TorrentNotifier;
import org.proninyaroslav.libretorrent.ui.errorreport.ErrorReportActivity;

@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "support@bitlord.com")
@AcraDialog(reportDialogClass = ErrorReportActivity.class)

public class MainApplication extends MultiDexApplication
{
    static MainApplication mainAppInstance;

    @SuppressWarnings("unused")
    private static final String TAG = MainApplication.class.getSimpleName();

    static {
        /* Vector Drawable support in ImageView for API < 21 */
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public MainApplication()
    {
        mainAppInstance = this;
    }

    public static MainApplication getInstance()
    {
        return mainAppInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Utils.migrateTray2SharedPreferences(this);
        ACRA.init(this);

        LibTorrentSafAdapter adapter = new LibTorrentSafAdapter(this);
        adapter.swigReleaseOwnership();
        libtorrent.set_posix_wrapper(adapter);

        TorrentNotifier.getInstance(this).makeNotifyChans();
    }
}