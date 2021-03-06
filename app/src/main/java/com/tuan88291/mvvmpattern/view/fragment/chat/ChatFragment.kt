package com.tuan88291.mvvmpattern.view.fragment.chat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.tuan88291.mvvmpattern.BaseFragment
import com.tuan88291.mvvmpattern.R
import com.tuan88291.mvvmpattern.data.local.model.DataChat
import com.tuan88291.mvvmpattern.databinding.AboutFragmentBinding
import com.tuan88291.mvvmpattern.view.activity.videocall.VideoCall
import com.tuan88291.mvvmpattern.view.activity.voicecall.VoiceCall
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatFragment : BaseFragment() {
    private var binding: AboutFragmentBinding? = null
    private val chatViewModel: ChatViewModel by viewModel()
    override fun setView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.about_fragment, container, false)
        return binding!!.getRoot()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(chatViewModel)
    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.list?.setmId(Build.MODEL)
        chatViewModel.getTyping().observe(this, Observer<String> { this.onTyping(it) })
        chatViewModel.getLoading().observe(this, Observer<Boolean> { this.loading(it) })
        chatViewModel.getDataChat().observe(this, Observer<DataChat> { this.processData(it) })
        chatViewModel.getAllDataChat().observe(this, Observer<MutableList<DataChat>> { this.processAllData(it) })
        binding?.send?.setOnClickListener {
            if (binding?.input?.text?.toString()!! == "") return@setOnClickListener
            chatViewModel.sendMsg(binding?.input?.text?.toString()!!)
            binding?.input?.setText("")
        }
        binding?.input?.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                chatViewModel.emitTyping()
            }

        })
        binding?.list?.clickCall = {item: DataChat, isVideo: Boolean ->
            if (isVideo) {
                val intent = Intent(mContext(), VideoCall::class.java)
                intent.putExtra("model", item.name)
                startActivity(intent)
            } else {
                val intent = Intent(mContext(), VoiceCall::class.java)
                intent.putExtra("model", item.name)
                startActivity(intent)
            }
        }
        mContext()?.setUpTyping()
    }

    private fun processData(item: DataChat) {
        binding?.list?.setData(item)
    }
    private fun processAllData(data: MutableList<DataChat>) {
        binding?.list?.addAllData(data)
    }
    private fun loading(load: Boolean) {
        binding?.loading?.visibility = if (load) View.VISIBLE else View.GONE
    }
    private fun onTyping(typings: String) {
        if (typings !== "") {
            mContext()?.setTyping(typings)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(chatViewModel)
    }
}
