package com.example.a7minuteworkoutapp
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import com.example.a7minuteworkoutapp.databinding.ActivityExerciseBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var player:MediaPlayer? = null
    private var tts: TextToSpeech? = null

    private var restTimer:CountDownTimer? = null
    private var restProgress = 0

    private var exerciseTimer:CountDownTimer?= null
    private var exerciseProgress = 0

    private var exerciseTimerDuration:Long = 30
    private var exerciseList:ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var binding:ActivityExerciseBinding? = null

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        tts = TextToSpeech(this,this)

        setSupportActionBar(binding?.toolbarExercise)
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }
        exerciseList = Constants.defaultExerciseList()
        setUpRestView()
    }

    private fun setUpRestView(){
        try {
            val soundURI = Uri.parse(
                "android.resource://com.example.a7minuteworkoutapp/"+
                        R.raw.press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }
        catch(e:Exception){
            e.printStackTrace()
        }
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.tvImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE

        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE
            if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress
        restTimer = object:CountDownTimer(10000,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress //Indicates progress bar progress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }
            override fun onFinish() {
                currentExercisePosition++
                setUpExerciseView()
            }
        }.start()
    }
    private fun setUpExerciseView(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.tvImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.tvImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text=(exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar(){
        binding?.exerciseProgressBar?.progress = exerciseProgress
        exerciseTimer = object:CountDownTimer(exerciseTimerDuration * 1000,1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.exerciseProgressBar?.progress = exerciseTimerDuration.toInt() - exerciseProgress
                binding?.tvExerciseTimer?.text = (exerciseTimerDuration.toInt() - exerciseProgress).toString()
            }
            override fun onFinish() {
               if(currentExercisePosition <exerciseList?.size!! - 1){
                    setUpRestView()
               }else{
                  speakOut("Congratulations! You have completed the 7 minutes workout.")
                    }
            }
        }.start()
    }
    override fun onDestroy() {
        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        if(tts != null){
            tts?.stop()
            tts?.shutdown()
        }
        if(player != null){
            player?.stop()
        }
        super.onDestroy()
        binding = null
    }
    private fun speakOut(text:String){
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH,null,"" )
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The Language specified is not supported!")
            }
        }else{
            Log.e("TTS","Initialization Failed!")
        }
    }
 }


