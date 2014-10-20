/*
 * Copyright (C) 2014 Simple Explorer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.lovejoy777sarootool.rootool;

import android.app.Application;

import com.lovejoy777sarootool.rootool.settings.Settings;

public final class SimpleExplorer extends Application {

    public static final int THEME_ID_LIGHT = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        // get default preferences at start
        Settings.updatePreferences(this);
    }
}
