package com.rafalskrzypczyk.chat.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.chat.databinding.FragmentChatBinding
import com.rafalskrzypczyk.chat.domain.Message
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
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

            popoverNewMessages.setOnClickListener {
                rvMessages.smoothScrollToPosition(0)
                hideNewMessagesPopover()
            }

            swipeRefreshLayout.setOnRefreshListener{
                presenter.loadOlderMessages()
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val firstVisibleItemPosition = recyclerViewManager.findFirstVisibleItemPosition()

                    if (firstVisibleItemPosition == 0) hideNewMessagesPopover()
                }
            })
        }

    }

    override fun displayMessages(messages: List<Message>) {
        val firstVisibleItemPosition = recyclerViewManager.findFirstVisibleItemPosition()

        messagesAdapter.submitList(messages) {
            if (firstVisibleItemPosition == 0) {
                binding.rvMessages.scrollToPosition(0)
            } else if (firstVisibleItemPosition == -1) {
                if(binding.loading.root.isVisible)
                    QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, binding.rvMessages)
                return@submitList
            } else {
                showNewMessagesPopover()
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
        binding.rvMessages.visibility = View.INVISIBLE
        binding.loading.root.visibility = View.VISIBLE
    }

    override fun displayError(message: String) {
        with(binding.loading.root) { if(visibility == View.VISIBLE) visibility = View.GONE }
        with(binding.swipeRefreshLayout) { if(isRefreshing) isRefreshing = false }

        ErrorDialog(requireContext(), message).show()
    }

    private fun showNewMessagesPopover() {
        with(binding.popoverNewMessages) {
            if (visibility == View.VISIBLE) return
            QuizEditorAnimations.animateScaleIn(this)
        }
    }

    private fun hideNewMessagesPopover() {
        with(binding.popoverNewMessages) {
            if (visibility == View.GONE) return
            QuizEditorAnimations.animateScaleOut(this)
        }
    }
}