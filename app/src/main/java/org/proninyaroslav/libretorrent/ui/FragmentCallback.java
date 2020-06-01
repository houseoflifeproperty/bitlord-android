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

package org.proninyaroslav.libretorrent.ui;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/*
 * The basic callback interface with codes and functions, returned by fragments.
 */

public interface FragmentCallback
{
    @SuppressWarnings("unused")
    String TAG = FragmentCallback.class.getSimpleName();

    enum ResultCode {
        OK, CANCEL, BACK
    }

    void onFragmentFinished(@NonNull Fragment f, Intent intent, @NonNull ResultCode code);
}