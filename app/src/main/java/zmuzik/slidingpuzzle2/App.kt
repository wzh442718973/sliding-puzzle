package zmuzik.slidingpuzzle2

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.android.startKoin
import org.koin.log.Logger
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        startKoin(this, listOf(appModule), logger = object : Logger {
            override fun debug(msg: String) {}

            override fun err(msg: String) {}

            override fun info(msg: String) {}
        })

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
