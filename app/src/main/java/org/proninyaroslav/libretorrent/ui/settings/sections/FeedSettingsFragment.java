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

package org.proninyaroslav.libretorrent.ui.settings.sections;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import org.proninyaroslav.libretorrent.R;
import org.proninyaroslav.libretorrent.core.RepositoryHelper;
import org.proninyaroslav.libretorrent.core.settings.SettingsRepository;
import org.proninyaroslav.libretorrent.service.Scheduler;

public class FeedSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener
{
    @SuppressWarnings("unused")
    private static final String TAG = FeedSettingsFragment.class.getSimpleName();

    private SettingsRepository pref;

    public static FeedSettingsFragment newInstance()
    {
        FeedSettingsFragment fragment = new FeedSettingsFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        pref = RepositoryHelper.getSettingsRepository(getActivity().getApplicationContext());

        String keyAutoRefresh = getString(R.string.pref_key_feed_auto_refresh);
        SwitchPreferenceCompat autoRefresh = findPreference(keyAutoRefresh);
        if (autoRefresh != null) {
            autoRefresh.setChecked(pref.autoRefreshFeeds());
            bindOnPreferenceChangeListener(autoRefresh);
        }

        String keyRefreshInterval = getString(R.string.pref_key_feed_refresh_interval);
        ListPreference refreshInterval = findPreference(keyRefreshInterval);
        if (refreshInterval != null) {
            String interval = Long.toString(pref.refreshFeedsInterval());
            int intervalIndex = refreshInterval.findIndexOfValue(interval);
            if (intervalIndex >= 0) {
                refreshInterval.setValueIndex(intervalIndex);
                refreshInterval.setSummary(refreshInterval.getEntries()[intervalIndex]);
            }
            bindOnPreferenceChangeListener(refreshInterval);
        }

        String keyUnmeteredOnly = getString(R.string.pref_key_feed_auto_refresh_unmetered_connections_only);
        SwitchPreferenceCompat unmeteredOnly = findPreference(keyUnmeteredOnly);
        if (unmeteredOnly != null) {
            unmeteredOnly.setChecked(pref.autoRefreshFeedsUnmeteredConnectionsOnly());
            bindOnPreferenceChangeListener(unmeteredOnly);
        }

        String keyRoaming = getString(R.string.pref_key_feed_auto_refresh_enable_roaming);
        SwitchPreferenceCompat roaming = findPreference(keyRoaming);
        if (roaming != null) {
            roaming.setChecked(pref.autoRefreshFeedsEnableRoaming());
            bindOnPreferenceChangeListener(roaming);
        }

        String keyKeepTime = getString(R.string.pref_key_feed_keep_items_time);
        ListPreference keepTime = findPreference(keyKeepTime);
        if (keepTime != null) {
            String time = Long.toString(pref.feedItemKeepTime());
            int timeIndex = keepTime.findIndexOfValue(time);
            if (timeIndex >= 0) {
                keepTime.setValueIndex(timeIndex);
                keepTime.setSummary(keepTime.getEntries()[timeIndex]);
            }
            bindOnPreferenceChangeListener(keepTime);
        }

        String keyStartTorrents = getString(R.string.pref_key_feed_start_torrents);
        SwitchPreferenceCompat startTorrents = findPreference(keyStartTorrents);
        if (startTorrents != null) {
            startTorrents.setChecked(pref.feedStartTorrents());
            bindOnPreferenceChangeListener(startTorrents);
        }

        String keyRemoveDuplicates = getString(R.string.pref_key_feed_remove_duplicates);
        SwitchPreferenceCompat removeDuplicates = findPreference(keyRemoveDuplicates);
        if (removeDuplicates != null) {
            removeDuplicates.setChecked(pref.feedRemoveDuplicates());
            bindOnPreferenceChangeListener(removeDuplicates);
        }
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.pref_feed, rootKey);
    }

    private void bindOnPreferenceChangeListener(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        Context context = getActivity().getApplicationContext();

        if (preference.getKey().equals(getString(R.string.pref_key_feed_auto_refresh))) {
            pref.autoRefreshFeeds((boolean)newValue);

            if ((boolean)newValue) {
                long interval = pref.refreshFeedsInterval();
                Scheduler.runPeriodicalRefreshFeeds(context, interval);
            } else {
                Scheduler.cancelPeriodicalRefreshFeeds(context);
            }

        } else if (preference.getKey().equals(getString(R.string.pref_key_feed_refresh_interval))) {
            ListPreference refreshPreference = (ListPreference)preference;
            long interval = Long.parseLong((String)newValue);
            pref.refreshFeedsInterval(interval);
            int index = refreshPreference.findIndexOfValue((String)newValue);
            if (index >= 0)
                refreshPreference.setSummary(refreshPreference.getEntries()[index]);
            Scheduler.runPeriodicalRefreshFeeds(context, interval);

        } else if (preference.getKey().equals(getString(R.string.pref_key_feed_keep_items_time))) {
            ListPreference keepTimePreference = (ListPreference)preference;
            long keepTime = Long.parseLong((String)newValue);
            pref.feedItemKeepTime(keepTime);
            int index = keepTimePreference.findIndexOfValue((String)newValue);
            if (index >= 0)
                keepTimePreference.setSummary(keepTimePreference.getEntries()[index]);

        } else if (preference.getKey().equals(getString(R.string.pref_key_feed_auto_refresh_unmetered_connections_only))) {
            pref.autoRefreshFeedsUnmeteredConnectionsOnly((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_feed_auto_refresh_enable_roaming))) {
            pref.autoRefreshFeedsEnableRoaming((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_feed_start_torrents))) {
            pref.feedStartTorrents((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_feed_remove_duplicates))) {
            pref.feedRemoveDuplicates((boolean)newValue);
        }

        return true;
    }
}
