package ru.skillbranch.devintensive

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.models.Bender

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var benderImage: ImageView
    lateinit var textTxt: TextView
    lateinit var messageEt: EditText
    lateinit var sendBtn: ImageView
    lateinit var benderObj: Bender

    override fun onClick(v: View?) {
        if (v?.id == R.id.iv_send) {
            if (benderObj.question.validate(messageEt.text.toString())) {
                val (phase, color) = benderObj.listenAnswer(messageEt.text.toString().toLowerCase())
                messageEt.setText("")
                val (r, g, b) = color
                benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
                textTxt.text = phase
            } else {
                val str_ = when(benderObj.question){
                    Bender.Question.NAME -> "Имя должно начинаться с заглавной буквы"
                    Bender.Question.PROFESSION -> "Профессия должна начинаться со строчной буквы"
                    Bender.Question.MATERIAL -> "Материал не должен содержать цифр"
                    Bender.Question.BDAY -> "Год моего рождения должен содержать только цифры"
                    Bender.Question.SERIAL -> "Серийный номер содержит только цифры, и их 7"
                    else -> "На этом все, вопросов больше нет"
                } + "\n" + benderObj.question.question
                textTxt.text = str_
                messageEt.setText("")
            }
        }
    }

    private fun initialize() {
        setContentView(R.layout.activity_main)
        benderImage = iv_bender
        textTxt = tv_text
        messageEt = et_message
        sendBtn = iv_send
    }

    private fun loadFromBundle(savedInstanceState: Bundle?) {
        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name // после elvis - default-ы
        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name
        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))

        val(r, g, b) = benderObj.status.color
        benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
        textTxt.text = benderObj.askQuestion()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        loadFromBundle(savedInstanceState)

        sendBtn.setOnClickListener(this)
        messageEt.setRawInputType(InputType.TYPE_CLASS_TEXT)
        messageEt.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
                sendBtn.performClick()
            false
            }
        Log.d("M_MainActivity","onCreate")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity","onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity","onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity","onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity","onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity","onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        // Extra store info
        outState?.putString("STATUS", benderObj.status.name)
        outState?.putString("QUESTION", benderObj.question.name)
    }
}
