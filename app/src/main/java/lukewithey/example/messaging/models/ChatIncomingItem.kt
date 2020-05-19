package lukewithey.example.messaging.models

import android.util.Log
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_incoming_row.view.*
import lukewithey.example.messaging.R

private const val TAG= "ChatIncomingItem"

class ChatIncomingItem(val text: String): Item<GroupieViewHolder>() {


    override fun getLayout(): Int {
        return R.layout.chat_incoming_row
        Log.d(TAG, "getLayout called")
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_incoming_message.text = text
        Log.d(TAG, "bind called")


    }
}