package com.rafalskrzypczyk.chat.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.chat.databinding.FragmentChatBinding
import com.rafalskrzypczyk.chat.domain.Message
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate), ChatContract.View {
    @Inject
    lateinit var presenter: ChatContract.Presenter

    private lateinit var adapter: MessagesAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
    }

    override fun onViewBound() {
        super.onViewBound()

        with(binding){
            sectionMessageInput.btnSendMessage.setOnClickListener {
                presenter.sendMessage(sectionMessageInput.etNewMessage.text.toString())
                sectionMessageInput.etNewMessage.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        super.onDestroyView()
    }

    override fun setupMessagesReceiver(currentUserId: String) {
        adapter = MessagesAdapter(currentUserId)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = linearLayoutManager
    }

    override fun displayMessages(messages: List<Message>) {
        adapter.submitList(messages) {
            binding.rvMessages.scrollToPosition(0)
        }
    }

    override fun displayLoading() {

    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}