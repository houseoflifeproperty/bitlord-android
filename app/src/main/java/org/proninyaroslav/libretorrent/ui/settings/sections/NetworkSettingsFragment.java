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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import org.proninyaroslav.libretorrent.R;
import org.proninyaroslav.libretorrent.core.InputFilterRange;
import org.proninyaroslav.libretorrent.core.RepositoryHelper;
import org.proninyaroslav.libretorrent.core.settings.SessionSettings;
import org.proninyaroslav.libretorrent.core.settings.SettingsRepository;
import org.proninyaroslav.libretorrent.core.system.FileSystemFacade;
import org.proninyaroslav.libretorrent.core.system.SystemFacadeHelper;
import org.proninyaroslav.libretorrent.core.utils.Utils;
import org.proninyaroslav.libretorrent.ui.filemanager.FileManagerConfig;
import org.proninyaroslav.libretorrent.ui.filemanager.FileManagerDialog;
import org.proninyaroslav.libretorrent.ui.settings.PreferenceActivity;
import org.proninyaroslav.libretorrent.ui.settings.PreferenceActivityConfig;
import org.proninyaroslav.libretorrent.ui.settings.SettingsViewModel;

import java.util.ArrayList;
import java.util.List;

/*
 * TODO: add PeX enable/disable feature
 */

public class NetworkSettingsFragment extends PreferenceFragmentCompat
        implements
        Preference.OnPreferenceChangeListener
{
    @SuppressWarnings("unused")
    private static final String TAG = NetworkSettingsFragment.class.getSimpleName();

    private static final int FILE_CHOOSE_REQUEST = 1;
    private static final int ANONYMOUS_MODE = 2;

    private AppCompatActivity activity;
    private SettingsViewModel viewModel;
    private SettingsRepository pref;
    private FileSystemFacade fs;

    public static NetworkSettingsFragment newInstance()
    {
        NetworkSettingsFragment fragment = new NetworkSettingsFragment();

        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        if (context instanceof AppCompatActivity)
            activity = (AppCompatActivity)context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (activity == null)
            activity = (AppCompatActivity)getActivity();

        viewModel = new ViewModelProvider(activity).get(SettingsViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Context context = getActivity().getApplicationContext();
        pref = RepositoryHelper.getSettingsRepository(context);
        fs = SystemFacadeHelper.getFileSystemFacade(context);

        String keyEnableDht = getString(R.string.pref_key_enable_dht);
        SwitchPreferenceCompat enableDht = findPreference(keyEnableDht);
        if (enableDht != null) {
            enableDht.setChecked(pref.enableDht());
            bindOnPreferenceChangeListener(enableDht);
        }

        String keyEnableLsd = getString(R.string.pref_key_enable_lsd);
        SwitchPreferenceCompat enableLsd = findPreference(keyEnableLsd);
        if (enableLsd != null) {
            enableLsd.setChecked(pref.enableLsd());
            bindOnPreferenceChangeListener(enableLsd);
        }

        String keyEnableUtp = getString(R.string.pref_key_enable_utp);
        SwitchPreferenceCompat enableUtp = findPreference(keyEnableUtp);
        if (enableUtp != null) {
            enableUtp.setChecked(pref.enableUtp());
            bindOnPreferenceChangeListener(enableUtp);
        }

        String keyEnableUpnp = getString(R.string.pref_key_enable_upnp);
        SwitchPreferenceCompat enableUpnp = findPreference(keyEnableUpnp);
        if (enableUpnp != null) {
            enableUpnp.setChecked(pref.enableUpnp());
            bindOnPreferenceChangeListener(enableUpnp);
        }

        String keyEnableNatpmp = getString(R.string.pref_key_enable_natpmp);
        SwitchPreferenceCompat enableNatpmp = findPreference(keyEnableNatpmp);
        if (enableNatpmp != null) {
            enableNatpmp.setChecked(pref.enableNatPmp());
            bindOnPreferenceChangeListener(enableNatpmp);
        }

        String keyRandomPort = getString(R.string.pref_key_use_random_port);
        SwitchPreferenceCompat randomPort = findPreference(keyRandomPort);
        if (randomPort != null) {
            randomPort.setSummary(getString(R.string.pref_use_random_port_summarty,
                    SessionSettings.DEFAULT_PORT_RANGE_FIRST,
                    SessionSettings.DEFAULT_PORT_RANGE_SECOND - 10));
            randomPort.setDisableDependentsState(true);
            randomPort.setChecked(pref.useRandomPort());
            bindOnPreferenceChangeListener(randomPort);
        }

        InputFilter[] portFilter = new InputFilter[] { InputFilterRange.PORT_FILTER };

        String keyPortStart = getString(R.string.pref_key_port_range_first);
        EditTextPreference portStart = findPreference(keyPortStart);
        if (portStart != null) {
            String value = Integer.toString(pref.portRangeFirst());
            portStart.setOnBindEditTextListener((editText) -> editText.setFilters(portFilter));
            portStart.setSummary(value);
            portStart.setText(value);
            bindOnPreferenceChangeListener(portStart);
        }

        String keyPortEnd = getString(R.string.pref_key_port_range_second);
        EditTextPreference portEnd = findPreference(keyPortEnd);
        if (portEnd != null) {
            String value = Integer.toString(pref.portRangeSecond());
            portEnd.setOnBindEditTextListener((editText) -> editText.setFilters(portFilter));
            portEnd.setSummary(value);
            portEnd.setText(value);
            bindOnPreferenceChangeListener(portEnd);
        }

        boolean enableAdvancedEncryptSettings;

        String keyEncryptMode = getString(R.string.pref_key_enc_mode);
        ListPreference encryptMode = findPreference(keyEncryptMode);
        int type = pref.encryptMode();
        if (encryptMode != null) {
            encryptMode.setValueIndex(type);
            String[] typesName = getResources().getStringArray(R.array.pref_enc_mode_entries);
            encryptMode.setSummary(typesName[type]);
            enableAdvancedEncryptSettings = type != Integer.parseInt(getString(R.string.pref_enc_mode_disable_value));
            bindOnPreferenceChangeListener(encryptMode);

            String keyEncryptInConnections = getString(R.string.pref_key_enc_in_connections);
            SwitchPreferenceCompat encryptInConnections = findPreference(keyEncryptInConnections);
            if (encryptInConnections != null) {
                encryptInConnections.setEnabled(enableAdvancedEncryptSettings);
                encryptInConnections.setChecked(pref.encryptInConnections());
                bindOnPreferenceChangeListener(encryptInConnections);
            }

            String keyEncryptOutConnections = getString(R.string.pref_key_enc_out_connections);
            SwitchPreferenceCompat encryptOutConnections = findPreference(keyEncryptOutConnections);
            if (encryptOutConnections != null) {
                encryptOutConnections.setEnabled(enableAdvancedEncryptSettings);
                encryptOutConnections.setChecked(pref.encryptOutConnections());
                bindOnPreferenceChangeListener(encryptOutConnections);
            }
        }

        String keyIpFilter = getString(R.string.pref_key_enable_ip_filtering);
        SwitchPreferenceCompat ipFilter = findPreference(keyIpFilter);
        if (ipFilter != null) {
            ipFilter.setChecked(pref.enableIpFiltering());
            bindOnPreferenceChangeListener(ipFilter);
        }

        String keyIpFilterFile = getString(R.string.pref_key_ip_filtering_file);
        Preference ipFilterFile = findPreference(keyIpFilterFile);
        if (ipFilterFile != null) {
            String path = pref.ipFilteringFile();
            if (path != null)
                ipFilterFile.setSummary(fs.getFilePath(Uri.parse(path)));
            ipFilterFile.setOnPreferenceClickListener((Preference preference) -> {
                fileChooseDialog();

                return true;
            });
        }

        String keyShowNatErrors = getString(R.string.pref_key_show_nat_errors);
        SwitchPreferenceCompat showNatErrors = findPreference(keyShowNatErrors);
        if (showNatErrors != null) {
            showNatErrors.setChecked(pref.showNatErrors());
            bindOnPreferenceChangeListener(showNatErrors);
        }

        Preference proxy = findPreference(getString(R.string.pref_key_proxy_settings));
        if (proxy != null) {
            proxy.setOnPreferenceClickListener((preference) -> {
                if (Utils.isLargeScreenDevice(activity)) {
                    setFragment(ProxySettingsFragment.newInstance(),
                            getString(R.string.pref_proxy_settings_title));
                } else {
                    startActivity(ProxySettingsFragment.class,
                            getString(R.string.pref_proxy_settings_title));
                }

                return true;
            });
        }

        String keyAnonymousMode = getString(R.string.pref_key_anonymous_mode);
        Preference anonymousMode = findPreference(keyAnonymousMode);
        if (anonymousMode != null) {
            anonymousMode.setSummary(pref.anonymousMode() ? R.string.switch_on : R.string.switch_off);
            anonymousMode.setOnPreferenceClickListener((preference) -> {
                if (Utils.isLargeScreenDevice(activity)) {
                    setFragment(AnonymousModeSettingsFragment.newInstance(),
                            getString(R.string.pref_anonymous_mode_title));
                } else {
                    startActivityForResult(AnonymousModeSettingsFragment.class,
                            getString(R.string.pref_anonymous_mode_title), ANONYMOUS_MODE);
                }

                return true;
            });
        }

        String keySeedingOutgoingConn = getString(R.string.pref_key_seeding_outgoing_connections);
        SwitchPreferenceCompat seedingOutgoingConn = findPreference(keySeedingOutgoingConn);
        if (seedingOutgoingConn != null) {
            seedingOutgoingConn.setChecked(pref.seedingOutgoingConnections());
            bindOnPreferenceChangeListener(seedingOutgoingConn);
        }
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.pref_network, rootKey);
    }

    private void fileChooseDialog()
    {
        Intent i = new Intent(getActivity(), FileManagerDialog.class);
        FileManagerConfig config = new FileManagerConfig(
                null,
                null,
                FileManagerConfig.FILE_CHOOSER_MODE);
        List<String> fileTypes = new ArrayList<>();
        fileTypes.add("dat");
        fileTypes.add("p2p");
        config.highlightFileTypes = fileTypes;

        i.putExtra(FileManagerDialog.TAG_CONFIG, config);
        startActivityForResult(i, FILE_CHOOSE_REQUEST);
    }

    private void bindOnPreferenceChangeListener(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        if (preference.getKey().equals(getString(R.string.pref_key_port_range_first))) {
            int value = SessionSettings.DEFAULT_PORT_RANGE_FIRST;
            if (!TextUtils.isEmpty((String)newValue))
                value = Integer.parseInt((String)newValue);
            pref.portRangeFirst(value);
            preference.setSummary(Integer.toString(value));

        } else if (preference.getKey().equals(getString(R.string.pref_key_port_range_second))) {
            int value = SessionSettings.DEFAULT_PORT_RANGE_SECOND;
            if (!TextUtils.isEmpty((String)newValue))
                value = Integer.parseInt((String)newValue);
            pref.portRangeSecond(value);
            preference.setSummary(Integer.toString(value));

        } else if (preference.getKey().equals(getString(R.string.pref_key_enc_mode))) {
            int type = Integer.parseInt((String) newValue);
            pref.encryptMode(type);
            String[] typesName = getResources().getStringArray(R.array.pref_enc_mode_entries);
            preference.setSummary(typesName[type]);

            boolean enableAdvancedEncryptSettings = type != Integer.parseInt(getString(R.string.pref_enc_mode_disable_value));

            String keyEncryptInConnections = getString(R.string.pref_key_enc_in_connections);
            SwitchPreferenceCompat encryptInConnections = findPreference(keyEncryptInConnections);
            if (encryptInConnections != null) {
                encryptInConnections.setEnabled(enableAdvancedEncryptSettings);
                encryptInConnections.setChecked(enableAdvancedEncryptSettings);
            }

            String keyEncryptOutConnections = getString(R.string.pref_key_enc_out_connections);
            SwitchPreferenceCompat encryptOutConnections = findPreference(keyEncryptOutConnections);
            if (encryptOutConnections != null) {
                encryptOutConnections.setEnabled(enableAdvancedEncryptSettings);
                encryptOutConnections.setChecked(enableAdvancedEncryptSettings);
            }

        } else if (preference.getKey().equals(getString(R.string.pref_key_enable_dht))) {
            pref.enableDht((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_enable_lsd))) {
            pref.enableLsd((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_enable_utp))) {
            pref.enableUtp((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_enable_upnp))) {
            pref.enableUpnp((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_enable_natpmp))) {
            pref.enableNatPmp((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_show_nat_errors))) {
            pref.showNatErrors((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_use_random_port))) {
            pref.useRandomPort((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_enc_in_connections))) {
            pref.encryptInConnections((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_enc_out_connections))) {
            pref.encryptOutConnections((boolean)newValue);

        } else if (preference.getKey().equals(getString(R.string.pref_key_enable_ip_filtering))) {
            pref.enableIpFiltering((boolean)newValue);

        }  else if (preference.getKey().equals(getString(R.string.pref_key_seeding_outgoing_connections))) {
            pref.seedingOutgoingConnections((boolean)newValue);
        }

        return true;
    }

    private <F extends PreferenceFragmentCompat> void setFragment(F fragment, String title)
    {
        if (Utils.isLargeScreenDevice(getActivity().getApplicationContext())) {
            viewModel.detailTitleChanged.setValue(title);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    private <F extends PreferenceFragmentCompat> void startActivity(Class<F> fragment, String title)
    {
        Intent i = new Intent(getActivity(), PreferenceActivity.class);
        PreferenceActivityConfig config = new PreferenceActivityConfig(
                fragment.getSimpleName(),
                title);

        i.putExtra(PreferenceActivity.TAG_CONFIG, config);
        startActivity(i);
    }

    private <F extends PreferenceFragmentCompat> void startActivityForResult(Class<F> fragment, String title, int requestCode)
    {
        Intent i = new Intent(getActivity(), PreferenceActivity.class);
        PreferenceActivityConfig config = new PreferenceActivityConfig(
                fragment.getSimpleName(),
                title);

        i.putExtra(PreferenceActivity.TAG_CONFIG, config);
        startActivityForResult(i, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == FILE_CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri path = data.getData();
            if (path == null)
                return;

            pref.ipFilteringFile(path.toString());

            String keyIpFilterFile = getString(R.string.pref_key_ip_filtering_file);
            Preference ipFilterFile = findPreference(keyIpFilterFile);
            if (ipFilterFile != null)
                ipFilterFile.setSummary(fs.getFilePath(path));

        } else if (requestCode == ANONYMOUS_MODE) {
            String keyAnonymousMode = getString(R.string.pref_key_anonymous_mode);
            Preference anonymousMode = findPreference(keyAnonymousMode);
            if (anonymousMode != null)
                anonymousMode.setSummary(pref.anonymousMode() ? R.string.switch_on : R.string.switch_off);
        }
    }
}
