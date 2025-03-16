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

    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var recyclerViewManager: LinearLayoutManager

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
        messagesAdapter = MessagesAdapter(currentUserId)
        recyclerViewManager = LinearLayoutManager(requireContext())
        recyclerViewManager.reverseLayout = true

        with(binding.rvMessages) {
            adapter = messagesAdapter
            layoutManager = recyclerViewManager
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//
//                    val lastVisibleItemPosition = recyclerViewManager.findLastVisibleItemPosition()
//
//                    if (lastVisibleItemPosition == messagesAdapter.itemCount.minus(1)) {
//                        presenter.loadOlderMessages()
//                    }
//                }
//            })
        }
        binding.swipeRefreshLayout.setOnRefreshListener{
            presenter.loadOlderMessages()
        }
    }

    override fun displayMessages(messages: List<Message>) {
        val shouldScroll = recyclerViewManager.findFirstVisibleItemPosition() == 0

        messagesAdapter.submitList(messages) {
            if (shouldScroll) {
                binding.rvMessages.scrollToPosition(0)
            }
        }
    }

    override fun displayOlderMessages(messages: List<Message>) {
        val previousItemCount = messagesAdapter.itemCount
        messagesAdapter.submitList(messages) {
            val newItemCount = messagesAdapter.itemCount
            val difference = newItemCount - previousItemCount

            if (difference > 0) {
                val newScrollPosition = newItemCount - difference
                recyclerViewManager.scrollToPosition(newScrollPosition)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun displayOlderMessagesLoading() {
    }

    override fun displayLoading() {

    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}