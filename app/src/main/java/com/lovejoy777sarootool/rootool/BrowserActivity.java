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

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lovejoy777sarootool.rootool.adapters.BookmarksAdapter;
import com.lovejoy777sarootool.rootool.adapters.BrowserTabsAdapter;
import com.lovejoy777sarootool.rootool.adapters.DrawerListAdapter;
import com.lovejoy777sarootool.rootool.adapters.MergeAdapter;
import com.lovejoy777sarootool.rootool.fragments.BrowserFragment;
import com.lovejoy777sarootool.rootool.preview.IconPreview;
import com.lovejoy777sarootool.rootool.settings.Settings;
import com.lovejoy777sarootool.rootool.settings.SettingsActivity;
import com.lovejoy777sarootool.rootool.utils.Bookmarks;
import com.lovejoy777sarootool.rootool.utils.NavigationView;
import com.lovejoy777sarootool.rootool.utils.NavigationView.OnNavigateListener;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.io.File;

public final class BrowserActivity extends ThemableActivity implements
        OnNavigateListener, BrowserFragment.onUpdatePathListener {

    public static final String EXTRA_SHORTCUT = "shortcut_path";
    public static final String TAG_DIALOG = "dialog";

    private ActionBar mActionBar;
    private static MergeAdapter mMergeAdapter;
    private static BookmarksAdapter mBookmarksAdapter;
    private static DrawerListAdapter mMenuAdapter;
    private static NavigationView mNavigation;

    private static ListView mDrawer;
    private static DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Cursor mBookmarksCursor;

    private FragmentManager fm;
    private BrowserTabsAdapter mPagerAdapter;
    private static BrowserFragment mBrowserFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.updatePreferences(this);

        invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        final Fragment f = fm.findFragmentByTag(TAG_DIALOG);

        if (f != null) {
            fm.beginTransaction().remove(f).commit();
            fm.executePendingTransactions();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mNavigation != null)
            mNavigation.removeOnNavigateListener(this);
    }

    public void setCurrentlyDisplayedFragment(final BrowserFragment fragment) {
        mBrowserFragment = fragment;
    }

    public static BrowserFragment getCurrentlyDisplayedFragment() {
        return mBrowserFragment;
    }

    private void init() {
        fm = getFragmentManager();
        mNavigation = new NavigationView(this);

        mActionBar = this.getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.show();

        setupDrawer();
        initDrawerList();

        // add listener for navigation view
        if (mNavigation.listeners.isEmpty())
            mNavigation.addonNavigateListener(this);

        // start IconPreview class to get thumbnails if BrowserListAdapter
        // request them
        new IconPreview(this);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new BrowserTabsAdapter(fm);
        mPager.setAdapter(mPagerAdapter);

        UnderlinePageIndicator mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setFades(false);
    }

    private void setupDrawer() {
        final TypedArray array = obtainStyledAttributes(new int[]{R.attr.themeId});
        final int themeId = array.getInteger(0, SimpleExplorer.THEME_ID_LIGHT);
        array.recycle();

        mDrawer = (ListView) findViewById(R.id.left_drawer);

        // Set shadow of navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        int icon = themeId == SimpleExplorer.THEME_ID_LIGHT ? R.drawable.holo_light_ic_drawer
                : R.drawable.holo_dark_ic_drawer;

        // Add Navigation Drawer to ActionBar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, icon,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initDrawerList() {
        mBookmarksCursor = getBookmarksCursor();
        mBookmarksAdapter = new BookmarksAdapter(this, mBookmarksCursor);
        mMenuAdapter = new DrawerListAdapter(this);

        // create MergeAdapter to combine multiple adapter
        mMergeAdapter = new MergeAdapter();
        mMergeAdapter.addAdapter(mBookmarksAdapter);
        mMergeAdapter.addAdapter(mMenuAdapter);

        mDrawer.setAdapter(mMergeAdapter);
        mDrawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mMergeAdapter.getAdapter(position)
                        .equals(mBookmarksAdapter)) {

                    // handle bookmark items
                    if (mDrawerLayout.isDrawerOpen(mDrawer))
                        mDrawerLayout.closeDrawer(mDrawer);

                    if (mBookmarksCursor.moveToPosition(position)) {
                        File file = new File(mBookmarksCursor
                                .getString(mBookmarksCursor
                                        .getColumnIndex(Bookmarks.PATH)));

                        mBrowserFragment.onBookmarkClick(file);
                    }
                } else if (mMergeAdapter.getAdapter(position).equals(
                        mMenuAdapter)) {
                    // handle menu items
                    switch ((int) mMergeAdapter.getItemId(position)) {
                        case 0:
                            Intent intent2 = new Intent(BrowserActivity.this,
                                    SettingsActivity.class);
                            startActivity(intent2);
                            break;
                        case 1:
                            finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawer)) {
                    mDrawerLayout.closeDrawer(mDrawer);
                } else {
                    mDrawerLayout.openDrawer(mDrawer);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mDrawer);
    }

    public static BookmarksAdapter getBookmarksAdapter() {
        return mBookmarksAdapter;
    }

    private Cursor getBookmarksCursor() {
        return getContentResolver().query(
                Bookmarks.CONTENT_URI,
                new String[]{Bookmarks._ID, Bookmarks.NAME, Bookmarks.PATH,
                        Bookmarks.CHECKED}, null, null, null);
    }

    @Override
    public boolean onKeyDown(int keycode, @NonNull KeyEvent event) {
        if (isDrawerOpen())
            mDrawerLayout.closeDrawer(mDrawer);
        return mBrowserFragment.onBackPressed(keycode);
    }

    @Override
    public void onNavigate(String path) {
        mBrowserFragment.onNavigate(path);
    }

    @Override
    public void onUpdatePath(String path) {
        mNavigation.setDirectoryButtons(path);
    }
}
