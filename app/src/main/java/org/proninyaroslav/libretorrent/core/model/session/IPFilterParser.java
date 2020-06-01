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

package org.proninyaroslav.libretorrent.core.model.session;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.proninyaroslav.libretorrent.core.system.FileDescriptorWrapper;
import org.proninyaroslav.libretorrent.core.system.FileSystemFacade;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * Parser of blacklist IP addresses in DAT and P2P formats.
 */

class IPFilterParser
{
    @SuppressWarnings("unused")
    private static final String TAG = IPFilterParser.class.getSimpleName();

    private static final int MAX_LOGGED_ERRORS = 5;

    private boolean logEnabled;

    public IPFilterParser()
    {
       logEnabled = true;
    }

    public IPFilterParser(boolean logEnabled)
    {
        this.logEnabled = logEnabled;
    }

    public int parseFile(@NonNull Uri path, @NonNull FileSystemFacade fs, @NonNull IPFilter filter)
    {
        int ruleCount = 0;
        if (!fs.fileExists(path))
            return ruleCount;

        Log.d(TAG, "Start parsing IP filter file");

        try (FileDescriptorWrapper w = fs.getFD(path);
             FileInputStream is = new FileInputStream(w.open("r"))) {

            String pathStr = path.toString().toLowerCase();
            if (pathStr.contains("dat"))
                ruleCount = parseDAT(is, filter);
            else if (pathStr.contains("p2p"))
                ruleCount = parseP2P(is, filter);

        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));

            return ruleCount;

        } finally {
            Log.d(TAG, "Completed parsing IP filter file, is success = " + ruleCount);
        }

        return ruleCount;
    }

    /*
     * Parser for eMule ip filter in DAT format
     */

    public int parseDAT(@NonNull InputStream is, @NonNull IPFilter filter)
    {
        int ruleCount = 0;
        long lineNum = 0;
        int parseErrorCount = 0;
        LineIterator it;

        try {
            it = IOUtils.lineIterator(is, "UTF-8");

        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));

            return ruleCount;
        }

        while (it.hasNext()) {
            lineNum++;

            String line = it.nextLine();
            line = (line == null ? null : line.trim());
            if (line == null || line.isEmpty())
                continue;

            /* Ignoring commented lines */
            if (line.startsWith("#") || line.startsWith("//"))
                continue;

            /* Line should be split by commas */
            String[] parts = line.split(",");
            /* Check if there is at least one item (ip range) */
            if (parts.length == 0)
                continue;

            /* Check if there is an access value (apparently not mandatory) */
            if (parts.length > 1) {
                int accessNum = Integer.parseInt(parts[1].trim());
                /* Ignoring this rule because access value is too high */
                if (accessNum > 127)
                    continue;
            }

            /* IP Range should be split by a dash */
            String[] ips = parts[0].split("-");
            if (ips.length != 2) {
                parseErrorCount++;
                errLog(parseErrorCount, "DAT", "line " + lineNum + " is malformed. Line was " + line);
                continue;
            }

            String startAddr = parseIpAddress(ips[0]);
            if (startAddr == null) {
                parseErrorCount++;
                errLog(parseErrorCount, "DAT", "line " + lineNum +
                        " is malformed. Start IP of the range is invalid: " + ips[0]);
                continue;
            }

            String endAddr = parseIpAddress(ips[1]);
            if (endAddr == null) {
                parseErrorCount++;
                errLog(parseErrorCount, "DAT", "line " + lineNum +
                        " is malformed. End IP of the range is invalid: " + ips[1]);
                continue;
            }

            try {
                filter.addRange(startAddr, endAddr);
                ruleCount++;

            } catch (Exception e) {
                parseErrorCount++;
                errLog(parseErrorCount, "DAT", "line " + lineNum +
                        " is malformed. Line was " + line + ": " + e.getMessage());
            }
        }

        return ruleCount;
    }

    /*
     * Parser for PeerGuardian ip filter in p2p format
     */

    public int parseP2P(@NonNull InputStream is, @NonNull IPFilter filter)
    {
        int ruleCount = 0;
        long lineNum = 0;
        int parseErrorCount = 0;
        LineIterator it;

        try {
            it = IOUtils.lineIterator(is, "UTF-8");

        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));

            return ruleCount;
        }

        while (it.hasNext()) {
            lineNum++;

            String line = it.nextLine();
            line = (line == null ? null : line.trim());
            if (line == null || line.isEmpty())
                continue;

            /* Ignoring commented lines */
            if (line.startsWith("#") || line.startsWith("//"))
                continue;

            /* Line should be split by ':' */
            String[] parts = line.split(":");
            if (parts.length < 2) {
                parseErrorCount++;
                errLog(parseErrorCount, "P2P", "line " + lineNum + " is malformed");
                continue;
            }

            /* IP Range should be split by a dash */
            String[] ips = parts[1].split("-");
            if (ips.length != 2) {
                parseErrorCount++;
                errLog(parseErrorCount, "P2P", "line " + lineNum + " is malformed. Line was" + line);
                continue;
            }

            String startAddr = parseIpAddress(ips[0]);
            if (startAddr == null) {
                parseErrorCount++;
                errLog(parseErrorCount, "P2P", "line " + lineNum +
                        " is malformed. Start IP of the range is invalid: " + ips[0]);
                continue;
            }

            String endAddr = parseIpAddress(ips[1]);
            if (endAddr == null) {
                parseErrorCount++;
                errLog(parseErrorCount, "P2P", "line " + lineNum +
                        " is malformed. End IP of the range is invalid: " + ips[1]);
                continue;
            }

            try {
                filter.addRange(startAddr, endAddr);
                ruleCount++;

            } catch (Exception e) {
                parseErrorCount++;
                errLog(parseErrorCount, "P2P", "line " + lineNum +
                        " is malformed. Line was " + line + ": " + e.getMessage());
            }
        }

        return ruleCount;
    }

    private String parseIpAddress(String ip)
    {
        if (ip == null || ip.isEmpty())
            return null;

        String ipStr = ip.trim();
        /*
         * Emule .DAT files contain leading zeroes in IPv4 addresses eg 001.009.106.186.
         * We need to remove them because Boost.Asio fail to parse them.
         */
        String[] octets = ipStr.split("\\.");
        if (octets.length == 4) {
            StringBuilder sb = new StringBuilder(octets[0].length());

            for (int i = 0; i < octets.length; i++) {
                String octet = octets[i];

                if (octet.charAt(0) == '0' && octet.length() > 1) {
                    sb.insert(0, octet);
                    if (octet.charAt(1) == '0' && octet.length() > 2)
                        sb.delete(0, 2);
                    else
                        sb.delete(0, 1);

                    octets[i] = sb.toString();
                    sb.setLength(0);
                }
            }

            ipStr = octets[0] + "." + octets[1] + "." + octets[2] + "." + octets[3];
        }

        return ipStr;
    }

    private void errLog(int parseErrorCount, String prefix, String msg)
    {
        if (!logEnabled || parseErrorCount > MAX_LOGGED_ERRORS)
            return;

        Log.e(TAG, prefix + ": " + msg);
    }
}
