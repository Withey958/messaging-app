package lukewithey.example.messaging.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*
import lukewithey.example.messaging.R
import lukewithey.example.messaging.models.ChatMessage
import lukewithey.example.messaging.views.LatestMessageRow
import lukewithey.example.messaging.models.User
import lukewithey.example.messaging.models.UserItem
import lukewithey.example.messaging.registerlogin.RegisterActivity

private const val TAG = "LatestMessageActivity"

class LatestMessageActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)

        recyclerview_latest_message.adapter = adapter
        recyclerview_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessage()
        fetchCurrentUser()
        verifyUserIsLoggedIn()

        }

    val latestMessagesMap = HashMap<String, ChatMessage>()


    private fun refreshRecyclerView() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessage() {
        val outgoingId = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/LatestMessages/$outgoingId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                adapter.add(
                    LatestMessageRow(
                        chatMessage
                    )
                )

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }


//    private fun setupDummyRows() {
//        val adapter = GroupAdapter<GroupieViewHolder>()
//
//
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//
//        recyclerview_latest_message.adapter = adapter
//
//    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            launchRegisterActivity()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object:ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG, "Currentuser ${currentUser?.username}")
            }
        })
    }


    private fun launchRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                launchRegisterActivity()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}




