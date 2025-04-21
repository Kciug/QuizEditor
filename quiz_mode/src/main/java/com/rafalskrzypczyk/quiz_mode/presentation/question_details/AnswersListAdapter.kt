package com.rafalskrzypczyk.quiz_mode.presentation.question_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.delete_bubble_manager.DeleteBubbleManager
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.generic.GenericDiffCallback
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.AnswerUIModel

class AnswersListAdapter(
    private val keyboardController: KeyboardController,
    private val onAnswerChanged: (AnswerUIModel) -> Unit,
    private val onAnswerRemoved: (AnswerUIModel) -> Unit
) : ListAdapter<AnswerUIModel, AnswersListAdapter.ViewHolder>(
    GenericDiffCallback<AnswerUIModel>(
        itemsTheSame = { oldItem, newItem ->
            oldItem.id == newItem.id
        },
        contentsTheSame = { oldItem, newItem ->
            oldItem == newItem
        }
    )
) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val answerText: EditText = itemView.findViewById(R.id.field_question_text)
        val correctSwitch: SwitchCompat = itemView.findViewById(R.id.switch_correct)
        val tvIsCorrect: TextView = itemView.findViewById(R.id.tv_is_correct)

        val deleteBubbleManager = DeleteBubbleManager(itemView.context)

        fun bind(answer: AnswerUIModel) {
            answerText.setText(answer.answerText)
            correctSwitch.isChecked = answer.isCorrect

            itemView.setOnLongClickListener { view ->
                deleteBubbleManager.showDeleteBubble(view) {
                    onAnswerRemoved(answer)
                }
                true
            }

            changeIsCorrect(answer.isCorrect)

            correctSwitch.setOnCheckedChangeListener { _, isChecked ->
                answer.isCorrect = isChecked
                onAnswerChanged(answer)

                QuizEditorAnimations.animateScaleOut(tvIsCorrect) {
                    changeIsCorrect(isChecked)
                    QuizEditorAnimations.animateScaleIn(tvIsCorrect)
                }
            }

            answerText.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            answerText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    keyboardController.hideKeyboard(answerText)
                    true
                } else false
            }
            answerText.addTextChangedListener(
                afterTextChanged = {
                    answer.answerText = it.toString()
                    onAnswerChanged(answer)
                }
            )
        }

        private fun changeIsCorrect(isCorrect: Boolean) {
            if(isCorrect) {
                tvIsCorrect.setText(R.string.label_correct_answer)
                tvIsCorrect.setTextColor(itemView.context.getColor(com.rafalskrzypczyk.core.R.color.green))
            } else {
                tvIsCorrect.setText(R.string.label_incorrect_answer)
                tvIsCorrect.setTextColor(itemView.context.getColor(com.rafalskrzypczyk.core.R.color.red))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_answer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}