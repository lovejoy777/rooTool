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

package com.lovejoy777sarootool.rootool.tasks;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;

import com.lovejoy777sarootool.rootool.R;
import com.lovejoy777sarootool.rootool.dialogs.FileExistsDialog;
import com.lovejoy777sarootool.rootool.utils.ClipBoard;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;

public final class PasteTaskExecutor implements OnClickListener {

    private final WeakReference<Activity> mActivityReference;

    private final String mLocation;
    private final LinkedList<String> mToProcess;
    private final HashMap<String, String> mExisting;

    private String current;

    public PasteTaskExecutor(final Activity activity, final String location) {
        this.mActivityReference = new WeakReference<Activity>(activity);
        this.mLocation = location;
        this.mToProcess = new LinkedList<String>();
        this.mExisting = new HashMap<String, String>();
    }

    public void start() {
        final String[] contents = ClipBoard.getClipBoardContents();
        if (contents == null) {
            return;
        }

        for (final String ab : contents) {
            File file = new File(ab);

            if (file.exists()) {
                final File testTarget = new File(mLocation, file.getName());

                if (testTarget.exists()) {
                    mExisting.put(testTarget.getPath(), file.getPath());
                } else {
                    mToProcess.add(file.getPath());
                }
            }
        }

        next();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                // replace
                mToProcess.add(current);
                break;

            case android.R.id.button2:
                // replace all;
                mToProcess.add(current);
                for (String f : mExisting.keySet()) {
                    mToProcess.add(mExisting.get(f));
                }
                mExisting.clear();
                break;

            case R.id.button4:
                // skip all
                mExisting.clear();
                break;

            case R.id.button5:
                // abort
                mExisting.clear();
                mToProcess.clear();
                return;
        }

        next();
    }

    private void next() {
        final Activity a = this.mActivityReference.get();
        if (a != null) {
            if (mExisting.isEmpty()) {
                if (mToProcess.isEmpty()) {
                    ClipBoard.clear();
                } else {
                    String[] array = new String[mToProcess.size()];
                    for (int i = 0; i < mToProcess.size(); i++) {
                        array[i] = mToProcess.get(i);
                    }

                    mToProcess.toArray(array);

                    final PasteTask task = new PasteTask(a, mLocation);
                    task.execute(array);
                }
            } else {
                final String key = mExisting.keySet().iterator().next();
                this.current = mExisting.get(key);
                mExisting.remove(key);

                final Dialog dialog = new FileExistsDialog(a, current, key,
                        this, this, this, this, this);
                if (!a.isFinishing()) {
                    dialog.show();
                }
            }

            a.invalidateOptionsMenu();
        }
    }
}
