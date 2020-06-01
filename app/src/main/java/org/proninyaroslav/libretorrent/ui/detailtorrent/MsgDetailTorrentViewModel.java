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

import androidx.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class MsgDetailTorrentViewModel extends ViewModel
{
    private BehaviorSubject<Boolean> fragmentInActionModeEvents = BehaviorSubject.create();

    public void fragmentInActionMode(boolean inActionMode)
    {
        fragmentInActionModeEvents.onNext(inActionMode);
    }

    public Observable<Boolean> observeFragmentInActionMode()
    {
        return fragmentInActionModeEvents;
    }
}
