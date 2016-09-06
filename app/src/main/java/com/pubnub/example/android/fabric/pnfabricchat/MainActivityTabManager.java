package com.pubnub.example.android.fabric.pnfabricchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.common.collect.ImmutableList;
import com.pubnub.example.android.fabric.pnfabricchat.chat.ChatListAdapter;
import com.pubnub.example.android.fabric.pnfabricchat.chat.ChatTabFragment;
import com.pubnub.example.android.fabric.pnfabricchat.mapbox.PresenceMapAdapter;
import com.pubnub.example.android.fabric.pnfabricchat.mapbox.PresenceMapTabFragment;
import com.pubnub.example.android.fabric.pnfabricchat.presence.PresenceListAdapter;
import com.pubnub.example.android.fabric.pnfabricchat.presence.PresenceTabFragment;

import java.util.List;

public class MainActivityTabManager extends FragmentStatePagerAdapter {
    private final ChatTabFragment chatTabFragment = new ChatTabFragment();
    private final PresenceTabFragment presenceTabFragment = new PresenceTabFragment();
    private final PresenceMapTabFragment presenceMapTabFragment = new PresenceMapTabFragment();

    private List<Fragment> items = ImmutableList.of(chatTabFragment, presenceTabFragment, presenceMapTabFragment);

    public MainActivityTabManager(FragmentManager fm, int NumOfTabs) {
        super(fm);
    }

    public void setChatListAdapter(ChatListAdapter chatListAdapter) {
        this.chatTabFragment.setAdapter(chatListAdapter);
    }

    public void setPresenceAdapter(PresenceListAdapter presenceListAdapter) {
        this.presenceTabFragment.setAdapter(presenceListAdapter);
    }

    public void setPresenceMapAdapter(PresenceMapAdapter presenceMapAdapter) {
        this.presenceMapTabFragment.setAdapter(presenceMapAdapter);
    }

    @Override
    public Fragment getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public int getCount() {
        return this.items.size();
    }
}
