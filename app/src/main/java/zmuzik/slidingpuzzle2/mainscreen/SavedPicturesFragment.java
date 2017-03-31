package zmuzik.slidingpuzzle2.mainscreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;

public class SavedPicturesFragment extends Fragment {

    final String[] originalPictures = {
            Utils.ASSET_PREFIX + "game_pic_00.jpg",
            Utils.ASSET_PREFIX + "game_pic_01.jpg",
            Utils.ASSET_PREFIX + "game_pic_07.jpg",
            Utils.ASSET_PREFIX + "game_pic_02.jpg",
            Utils.ASSET_PREFIX + "game_pic_03.jpg",
            Utils.ASSET_PREFIX + "game_pic_04.jpg",
            Utils.ASSET_PREFIX + "game_pic_05.jpg",
            Utils.ASSET_PREFIX + "game_pic_06.jpg",
            Utils.ASSET_PREFIX + "game_pic_08.jpg",
            Utils.ASSET_PREFIX + "game_pic_09.jpg",
            Utils.ASSET_PREFIX + "game_pic_10.jpg",
            Utils.ASSET_PREFIX + "game_pic_11.jpg"};

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    PicturesGridAdapter mAdapter;

    public SavedPicturesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), getColumnsNumber());
        mRecyclerView.setLayoutManager(mLayoutManager);

        initData();

        return rootView;
    }

    public void initData() {
        mAdapter = getAdapter(getColumnsNumber());
        mRecyclerView.setAdapter(mAdapter);
    }

    public PicturesGridAdapter getAdapter(int columns) {
        return new PicturesGridAdapter(getActivity(), getPictures(), columns);
    }

    boolean isHorizontal() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    int getColumnsNumber() {
        if (Utils.isTablet(getContext())) {
            return isHorizontal() ? Conf.GRID_COLUMNS_LANDSCAPE_TABLET : Conf.GRID_COLUMNS_PORTRAIT_TABLET;
        } else {
            return isHorizontal() ? Conf.GRID_COLUMNS_LANDSCAPE_HANDHELD : Conf.GRID_COLUMNS_PORTRAIT_HANDHELD;
        }
    }

    public List<String> getPictures() {
        return Arrays.asList(originalPictures);
    }

    public int getLayoutId() {
        return R.layout.fragment_pictures_grid;
    }
}