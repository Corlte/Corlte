package com.lzyp.video.interfaces;

import com.lzyp.video.adapter.MusicAdapter;
import com.lzyp.video.bean.MusicBean;

/**
 * Created by cxf on 2018/12/7.
 */

public interface VideoMusicActionListener {
    void onPlayMusic(MusicAdapter adapter, MusicBean bean, int position);

    void onStopMusic();

    void onUseClick(MusicBean bean);

    void onCollect(MusicAdapter adapter, int musicId, int isCollect);
}
