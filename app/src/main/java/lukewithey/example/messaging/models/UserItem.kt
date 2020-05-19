package lukewithey.example.messaging.models

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import lukewithey.example.messaging.R

class UserItem(val user: User): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // Will be called in our list for our object later on
        viewHolder.itemView.user_row_textview.text = user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.user_row_imageview)
    }
}
