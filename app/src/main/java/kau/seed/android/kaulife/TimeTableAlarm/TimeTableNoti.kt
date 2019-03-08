package kau.seed.android.kaulife.TimeTableAlarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.orm.SugarRecord
import kau.seed.android.kaulife.R
import kau.seed.android.kaulife.SplashActivity
import kau.seed.android.kaulife.TimeTableAlarm.Class.ClassData
import java.util.*

class TimeTableNoti (base: Context?) : ContextWrapper(base){
    var context = base
    lateinit var kauIntent: Intent
    lateinit var builder: NotificationCompat.Builder
    lateinit var pendingIntent: PendingIntent
    lateinit var mNotificationManager : NotificationManager
    lateinit var vibrate : LongArray
    internal var exist : Boolean = false
    var minute = 0

    fun initView(){
        exist = packageList
        builder = NotificationCompat.Builder(this, "default")
        if (exist) { //유저 핸드폰 내에 KAU ID 어플리케이션이 Exist할 때만 KAUintent를 실행해 줌, 앱이 없다면 알림 클릭 시 아무런 동작도 하지 않는다.
            kauIntent = packageManager.getLaunchIntentForPackage("kr.co.symtra.kauid")
            pendingIntent = PendingIntent.getActivity(this, 0, kauIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            var intent = Intent (this, SplashActivity::class.java). apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager//오레오 버전은 notification 채널을 등록해 줘야 함
        vibrate = longArrayOf(0, 100, 200, 300)

        var c = Calendar.getInstance()
        minute = c.get(Calendar.MINUTE)
    }

    fun generateAlarm(id : Long?){
        var data = SugarRecord.findById(ClassData::class.java, id)

        builder.setSmallIcon(R.drawable.amblem)
        builder.setContentTitle("출첵 !")
        builder.setContentText("${data.subject}, 출석체크 하려면 클릭!")//~~수업 내용은 추 후 수업 내용에 따라 달라져야 하므로 변수를 집어넣어 줄 것이다. aboutview 내부에서 변경되게끔!
        builder.setVibrate(vibrate)
        builder.setTicker("출석하세요 !")
        builder.setWhen(System.currentTimeMillis())
        builder.priority = Notification.PRIORITY_MAX
        builder.setAutoCancel(true) //알림을 클릭했을 때 알림이 사라지도록
        builder.setFullScreenIntent(pendingIntent, true);
        builder.setContentIntent(pendingIntent) //알림을 클릭했을 때 pendingIntent가 실행되도록 함

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//O는 오레오 버전임을 명시, 오레오 버전은 알림 채널이 명시돼야 한다.
            mNotificationManager.createNotificationChannel(NotificationChannel("default", "NotiChannel", NotificationManager.IMPORTANCE_DEFAULT))
        }
        mNotificationManager.notify(0, builder.build())

    }

    //KAU ID 어플이 유저의 스마트 폰에 존재하는 지 검사
    private val packageList: Boolean
        get() {
            var isExist = false

            val pkgMgr= packageManager
            val mApps: List<ResolveInfo>
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            mApps = pkgMgr.queryIntentActivities(mainIntent, 0)

            try {
                for (i in mApps.indices) {
                    if (mApps[i].activityInfo.packageName.startsWith("kr.co.symtra.kauid")) {
                        isExist = true
                        break
                    }
                }
            } catch (e: Exception) {
                isExist = false
            }

            return isExist
        }
}