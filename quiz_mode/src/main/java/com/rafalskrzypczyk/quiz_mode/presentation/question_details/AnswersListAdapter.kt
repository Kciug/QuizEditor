package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel

class AnswersListAdapter(
    private val answers: MutableList<AnswerUIModel> = mutableListOf(),
    private val onAnswerChanged: (AnswerUIModel) -> Unit,
    private val onAnswerRemoved: (AnswerUIModel, Int) -> Unit
) : RecyclerView.Adapter<AnswersListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val answerText: EditText = itemView.findViewById(R.id.field_question_text)
        val correctSwitch: SwitchCompat = itemView.findViewById(R.id.switch_correct)

        fun bind(answer: AnswerUIModel) {
            answerText.setText(answer.answerText)
            correctSwitch.isChecked = answer.isCorrect

            itemView.setOnLongClickListener { view ->
                showDeleteBubble(view, answer)
                true
            }

            correctSwitch.setOnCheckedChangeListener { _, isChecked ->
                answer.isCorrect = isChecked
                onAnswerChanged(answer)
            }

            answerText.addTextChangedListener(
                afterTextChanged = {
                    answer.answerText = it.toString()
                    onAnswerChanged(answer)
                }
            )
        }

        private fun showDeleteBubble(view: View, answer: AnswerUIModel) {
            val inflater = LayoutInflater.from(view.context)
            val bubbleView = inflater.inflate(
                com.rafalskrzypczyk.core.R.layout.bubble_delete,
                view.rootView as ViewGroup,
                false
            )

            val popupWindow = PopupWindow(
                bubbleView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            val location = IntArray(2)
            view.getLocationOnScreen(location)

            val popupX = location[0] + (view.width / 2) - bubbleView.measuredWidth * 2
            val popupY = location[1] - bubbleView.measuredHeight - 50

            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, popupX, popupY)
            //popupWindow.showAsDropDown(view, 0, -view.height - 50)

            bubbleView.setOnClickListener {
                onAnswerRemoved(answer, adapterPosition)
                popupWindow.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_answer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(answers[position])
    }

    override fun getItemCount(): Int = answers.size

    fun updateData(newAnswers: List<AnswerUIModel>) {
        answers.clear()
        answers.addAll(newAnswers)
        notifyItemRangeInserted(0, newAnswers.size - 1)
    }

    fun itemAdded(addedAnswer: AnswerUIModel) {
        answers.add(addedAnswer)
        notifyItemInserted(answers.size)
    }

    fun itemRemoved(removedIndex: Int) {
        answers.removeAt(removedIndex)
        notifyItemRemoved(removedIndex)
    }
}