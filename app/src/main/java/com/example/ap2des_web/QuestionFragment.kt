package com.example.ap2des_web

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class QuestionFragment : Fragment() {

    interface Callback {
        fun onAnswerSelected(scoreDelta: Int)
    }

    private var callback: Callback? = null

    private val questions = arrayOf(
        "Você prefere jogar na defesa, meio ou ataque?",
        "Qual estilo de jogo você mais gosta?",
        "Você é mais técnico, rápido ou forte?",
        "Em que tipo de competição você se destaca?",
        "Qual é sua especialidade em campo?"
    )

    private val options = arrayOf(
        arrayOf("Defesa", "Meio", "Ataque"),
        arrayOf("Posse de bola", "Contra-ataque", "Bola parada"),
        arrayOf("Técnico", "Rápido", "Forte"),
        arrayOf("Liga", "Eliminatória", "Amistoso"),
        arrayOf("Passe", "Drible", "Finalização")
    )

    private var qIndex = 0
    private val total = questions.size

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) callback = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qIndex = arguments?.getInt("qIndex") ?: 0

        val progressText = view.findViewById<TextView>(R.id.progressText)
        val questionText = view.findViewById<TextView>(R.id.questionText)
        val radioGroup = view.findViewById<RadioGroup>(R.id.optionsGroup)
        val nextBtn = view.findViewById<Button>(R.id.nextBtn)

        // Update progress
        progressText.text = getString(R.string.question_progress_format, qIndex + 1, total)

        questionText.text = questions[qIndex]
        radioGroup.removeAllViews()
        for ((i, opt) in options[qIndex].withIndex()) {
            val rb = RadioButton(requireContext())
            rb.id = 100 + i
            rb.text = opt
            rb.setPadding(12, 24, 12, 24)
            radioGroup.addView(rb)
        }

        nextBtn.setOnClickListener {
            val checked = radioGroup.checkedRadioButtonId
            if (checked != -1) {
                val selected = view.findViewById<RadioButton>(checked).text.toString()
                val delta = when (selected) {
                    "Ataque", "Contra-ataque", "Rápido", "Finalização" -> 2
                    "Meio", "Posse de bola", "Técnico", "Passe" -> 1
                    else -> 0
                }
                callback?.onAnswerSelected(delta)
            } else {
                nextBtn.text = requireContext().getString(com.example.ap2des_web.R.string.choose_option)
            }
        }
    }

    companion object {
        fun create(index: Int): QuestionFragment {
            val f = QuestionFragment()
            val args = Bundle()
            args.putInt("qIndex", index)
            f.arguments = args
            return f
        }
    }
}
