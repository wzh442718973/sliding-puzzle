package zmuzik.slidingpuzzle2.mainscreen

import android.os.AsyncTask
import android.os.Environment
import zmuzik.slidingpuzzle2.FILE_PREFIX
import zmuzik.slidingpuzzle2.isPicture
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Zbynek Muzik on 2017-03-31.
 */

class UpdateCameraFilesTask(presenter: MainScreenPresenter) :
        AsyncTask<Void, Void, Void>() {

    val presenterWr = WeakReference<MainScreenPresenter>(presenter)
    lateinit var filesList: ArrayList<String>

    override fun doInBackground(vararg params: Void): Void? {
        val foundFiles = ArrayList<FileContainer>()
        val cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        scanDirectoryForPictures(cameraDir, foundFiles)
        //sort - most recent pictures first
        foundFiles.sortByDescending { it.lastModified }
        filesList = foundFiles.mapTo(ArrayList<String>()) { it.filePath }
        return null
    }

    private fun scanDirectoryForPictures(root: File?, filePaths: ArrayList<FileContainer>) {
        if (root == null) return
        val list = root.listFiles() ?: return

        list.filterNot { it.isHidden }.forEach {
            if (it.isDirectory) {
                scanDirectoryForPictures(it, filePaths)
            } else if (isPicture(it)) {
                filePaths.add(FileContainer(FILE_PREFIX + it.absolutePath, it.lastModified()))
            }
        }
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        presenterWr.get()?.updateCameraPictures(filesList)
    }

    inner class FileContainer constructor(val filePath: String, val lastModified: Long)
}