package lukewithey.example.messaging.views

import android.util.Log
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_incoming_row.view.*
import lukewithey.example.messaging.R
import lukewithey.example.messaging.models.User

private const val TAG= "ChatIncomingItem"

class ChatIncomingItem(val text: String, val user: User): Item<GroupieViewHolder>() {


    override fun getLayout(): Int {
        Log.d(TAG, "getLayout called")
        return R.layout.chat_incoming_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_incoming_message.text = text
        Log.d(TAG, "bind called")

        // load user image
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_incoming_row
        Picasso.get().load(uri).into(targetImageView)


    }
}