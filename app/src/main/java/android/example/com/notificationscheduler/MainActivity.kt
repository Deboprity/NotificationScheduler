package android.example.com.notificationscheduler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.widget.*
import android.widget.SeekBar




class MainActivity : AppCompatActivity() {

    private var mScheduler: JobScheduler? = null
    //Switches for setting job options
    private var mDeviceIdleSwitch: Switch? = null
    private var mDeviceChargingSwitch: Switch? = null
    //Override deadline seekbar
    private var mSeekBar: SeekBar? = null

    companion object {
        private const val JOB_ID = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDeviceIdleSwitch = findViewById(R.id.idleSwitch)
        mDeviceChargingSwitch = findViewById(R.id.chargingSwitch)
        mSeekBar = findViewById(R.id.seekBar)

        val seekBarProgress: TextView = findViewById(R.id.seekBarProgress)

        mSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (i > 0){
                    seekBarProgress.text = "$i s"
                }else {
                    seekBarProgress.text = getString(R.string.txt_not_set)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    fun scheduleJob() {
        val networkOptions: RadioGroup = findViewById(R.id.networkOptions)
        val selectedNetworkID = networkOptions.checkedRadioButtonId
        var selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
        val seekBarInteger = mSeekBar?.progress
        val seekBarSet: Boolean = seekBarInteger!! > 0

        when (selectedNetworkID) {
            R.id.noNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
            R.id.anyNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY
            R.id.wifiNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED
        }

        mScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

        val serviceName = ComponentName(packageName, NotificationJobService::class.java.name)
        val builder = JobInfo.Builder(JOB_ID, serviceName)
        builder.setRequiredNetworkType(selectedNetworkOption)
            .setRequiresDeviceIdle(mDeviceIdleSwitch?.isChecked!!)
            .setRequiresCharging(mDeviceChargingSwitch?.isChecked!!)
        if (seekBarSet) {
            builder.setOverrideDeadline((seekBarInteger * 1000).toLong())
        }
        val constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE) || mDeviceChargingSwitch?.isChecked!! || mDeviceIdleSwitch?.isChecked!! || seekBarSet
        if(constraintSet) {
            val myJobInfo = builder.build()
            mScheduler?.schedule(myJobInfo)
            Toast.makeText(this, "Job Scheduled, job will run when " + "the constraints are met.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please set at least one constraint", Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelJobs() {
        if (mScheduler != null){
            mScheduler?.cancelAll()
            mScheduler = null
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
