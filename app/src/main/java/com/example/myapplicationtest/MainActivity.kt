package com.example.myapplicationtest

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.myapplicationtest.databinding.ActivityMainBinding
import com.google.android.material.search.SearchView.TransitionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    var TAG = "LOADIMAGE"
    lateinit var binding: ActivityMainBinding
    val SPEECH_REQUEST_CODE = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBar.setOnMenuItemClickListener(this)

        // binding.searchView.inflateMenu(R.menu.menu_searchview)
        binding.searchView.setOnMenuItemClickListener(this)
        binding.searchView.getEditText().setOnEditorActionListener { v, actionId, event ->
            binding.searchBar.setText(binding.searchView.getText())
            binding.searchView.hide()
            false
        }



        val color = TypedValue().let {
            theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, it, true)
                getColor(it.resourceId)
        }

        binding.searchView.addTransitionListener { searchView, previousState, newState ->
            if (newState === TransitionState.SHOWING) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   // window.statusBarColor= android.R.attr.colorBackground
                    window.statusBarColor= color
                }
            } else if (newState === TransitionState.HIDING) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.mmd_theme_on_backgroud));
                }
            }
        }

        binding.floating.setOnClickListener {
            startActivity(Intent(this,MotionActivity::class.java))
        }
        loadImage()
    }

    private fun loadImage() {
        try {
            val profileUrl: String =
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm_FERoNLrDVgAEMAZx6Q1VpwZYZdN6K3Grw&usqp=CAU"
            Glide.with(this).load(profileUrl).centerCrop().circleCrop()
                .sizeMultiplier(0.50f) //optional
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "loadImage: ${e?.message}")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.let {
                            renderProfileImage(it)
                        }
                        return true
                    }

                }).submit()

        } catch (e: Exception) {
            Log.e(TAG, "loadImage: ${e.message}")
        }
    }

    private fun renderProfileImage(resource: Drawable) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.searchBar.menu.findItem(R.id.notification).icon = resource
        }
    }


    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.let { results -> results[0] }
            binding.searchBar.setText(spokenText)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.notification -> Log.d("MENU", getString(R.string.profile))
            R.id.voicesearch -> displaySpeechRecognizer()
            else -> Log.d("MENU", "")
        }
        return true
    }

}