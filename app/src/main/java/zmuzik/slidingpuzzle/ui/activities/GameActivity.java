package zmuzik.slidingpuzzle.ui.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.flickr.Photo;
import zmuzik.slidingpuzzle.flickr.Size;
import zmuzik.slidingpuzzle.gfx.PuzzleBoardView;
import zmuzik.slidingpuzzle.helpers.PrefsHelper;

public class GameActivity extends Activity {

    final String TAG = this.getClass().getSimpleName();

    PuzzleBoardView board;
    ProgressBar progressBar;

    int mScreenWidth;
    int mScreenHeight;
    int mBoardWidth;
    int mBoardHeight;

    public String mFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        resolveScreenDimensions();
        board = (PuzzleBoardView) findViewById(R.id.board);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        resolvePictureUri(new Callback() {
            @Override public void onFinished() {
                Picasso.with(GameActivity.this).load(mFileUri).into(mTarget);
            }
        });
    }

    void resolvePictureUri(Callback callback) {
        progressBar.setVisibility(View.VISIBLE);
        board.setVisibility(View.GONE);
        if (getIntent().getExtras() == null) {
            Toast.makeText(this, getString(R.string.picture_not_supplied), Toast.LENGTH_LONG).show();
            finish();
        }
        mFileUri = getIntent().getExtras().getString("FILE_URI");
        if (mFileUri != null) {
            callback.onFinished();
        } else {
            String photoStr = getIntent().getExtras().getString("PHOTO");
            Gson gson = new Gson();
            Photo photo = gson.fromJson(photoStr, Photo.class);
            new GetFlickrPhotoSizesTask(photo, getMaxScreenDim(), callback).execute();
        }
    }

    Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setScreenOrientation(bitmap.getWidth(), bitmap.getHeight());
            resolveScreenDimensions();
            progressBar.setVisibility(View.GONE);
            board.setVisibility(View.VISIBLE);
            adjustBoardDimensions(board, bitmap);
            board.setBitmap(bitmap);
        }

        @Override public void onBitmapFailed(Drawable errorDrawable) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(GameActivity.this, getString(R.string.unable_to_load_flicker_picture),Toast.LENGTH_LONG).show();
            finish();
        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    void setScreenOrientation(int bitmapWidth, int bitmapHeight) {
        if (bitmapWidth > bitmapHeight) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    void resolveScreenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    int getMaxScreenDim() {
        return (mScreenWidth > mScreenHeight) ? mScreenWidth : mScreenHeight;
    }

    void adjustBoardDimensions(PuzzleBoardView board, Bitmap bitmap) {
        float screenSideRatio = (float) mScreenWidth / mScreenHeight;
        float origPictureSideRatio = (float) bitmap.getWidth() / bitmap.getHeight();
        if (origPictureSideRatio > screenSideRatio) {
            mBoardWidth = mScreenWidth;
            mBoardHeight = (int) (mScreenWidth / origPictureSideRatio);
        } else {
            mBoardHeight = mScreenHeight;
            mBoardWidth = (int) (mScreenHeight * origPictureSideRatio);
        }
        PrefsHelper ph = PrefsHelper.get();
        int widthMultiple = (mBoardWidth > mBoardHeight) ? ph.getGridDimLong() : ph.getGridDimShort();
        int heightMultiple = (mBoardWidth > mBoardHeight) ? ph.getGridDimShort() : ph.getGridDimLong();
        mBoardWidth = mBoardWidth - (mBoardWidth % widthMultiple);
        mBoardHeight = mBoardHeight - (mBoardHeight % heightMultiple);

        board.setDimensions(mBoardWidth, mBoardHeight);
    }

    @Override
    public void onStop() {
        Picasso.with(this).cancelRequest(mTarget);
        super.onDestroy();
    }

    private class GetFlickrPhotoSizesTask extends AsyncTask<Void, Void, Void> {

        Photo photo;
        List<Size> sizes;
        Callback callback;
        int maxScreenDim;
        String result;

        public GetFlickrPhotoSizesTask(Photo photo, int maxScreenDim, Callback callback) {
            this.photo = photo;
            this.maxScreenDim = maxScreenDim;
            this.callback = callback;
        }

        @Override protected Void doInBackground(Void... params) {
            sizes = App.get().getFlickrApi().getSizes(photo.getId()).getSizes().getSize();
            result = photo.getFullPicUrl(maxScreenDim, sizes);
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFileUri = result;
            callback.onFinished();
        }
    }

    private interface Callback {
        void onFinished();
    }
}
