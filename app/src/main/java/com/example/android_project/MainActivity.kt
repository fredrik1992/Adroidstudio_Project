package com.example.android_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.android_project.model.Character
import com.example.android_project.viewmodel.MainViewModel
import com.squareup.picasso.Picasso

lateinit var activeChar: Character
const val MAX_NUM_CHARSID = 731 // max num om charids in api

class MainActivity : AppCompatActivity() {

    lateinit var btn_search: Button
    lateinit var btn_random: Button
    lateinit var btn_fight: Button
    lateinit var imgBtn_charInfo: ImageButton
    lateinit var charImageView: ImageView
    lateinit var charName: TextView

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        charImageView = findViewById(R.id.characterPic)
        charName = findViewById(R.id.characterName)


        val intent = getIntent()
        if (intent.hasExtra("activeChar")) {
            activeChar = intent.getSerializableExtra("activeChar") as Character
        }
        if (::activeChar.isInitialized){ // if activeChar exists change the viewModel
            viewModel.setCharacter(activeChar)
        }else{
            viewModel.randomCharacter()
        }


        //places an observer to update the UI every time the character in viewModel changes
        viewModel.getCharacter().observe(this, Observer { character ->
            charName.text = character.name
            Picasso.get().load(character.img?.url).into(charImageView)
            activeChar = character
        })

        initButtons()
        refreshChar()
    }

    private fun initButtons() {

        btn_search = findViewById(R.id.buttonSearchChar)
        btn_fight = findViewById(R.id.buttonFight)
        btn_random = findViewById(R.id.buttonRandomChar)
        imgBtn_charInfo = findViewById(R.id.characterPic)

        btn_fight.setOnClickListener { startFight() }
        btn_random.setOnClickListener {
            viewModel.randomCharacter()
        }
        btn_search.setOnClickListener { browseChars() }
        imgBtn_charInfo.setOnClickListener { getHeroInfo() }
    }

    private fun refreshChar() {
        var refresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        refresh.setOnRefreshListener {
            viewModel.randomCharacter()
            refresh.isRefreshing = false
        }
    }

    private fun getHeroInfo() {
        val intent = Intent(this, HeroInfoActivity::class.java).apply {
            putExtra("activeChar", activeChar)
        }
        startActivity(intent)

    }

    private fun browseChars() {
        val intent = Intent(this, BrowseActivity::class.java)
        startActivity(intent)
    }

    private fun startFight() {

        if (::activeChar.isInitialized) {
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra(
                    "activeChar",
                    activeChar
                ) // send selected character wich is the active hero
            }
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "Please select a hero first", Toast.LENGTH_SHORT)
                .show()
        }

    }

}