package lukewithey.example.messaging

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

private const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        register_button.setOnClickListener {
            onRegister(it)
        }

        already_have_account_text_view.setOnClickListener {
            Log.d(TAG, "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        image_select_button.setOnClickListener{
            Log.d(TAG, "Try to load photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: Called")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was..
            Log.d(TAG, "OnActivityResult: Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            image_select_image_view.setImageBitmap(bitmap)
            image_select_button.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(this.resources, bitmap)
//            image_select_button.background = bitmapDrawable
        }
    }

    private fun onRegister(view: View) {
        //TODO move logic to view model?

        val email = register_email.text.toString()
        val password = register_password.text.toString()


        Log.d(TAG, "Register Button: starts")

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Email or password is empty", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            Log.d(TAG, "Fields are empty")
        } else {
            Log.d(TAG, "Email is $email")
            Log.d(TAG, "Password is $password")

            // Register firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success Email: $email Password: $password")

                        uploadImageToFirebaseStorage()
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "Failed to create user: ${it.message}")
                    Snackbar.make(
                        view,
                        "Failed to login because: ${it.message}",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null).show()
                }
        }

    }

    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, register_username.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "User saved to database")

                // Start Messenger Activity
                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // flags avoid going back to the register page onbackpressed CLEARS ACTIVITY STACK
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to save to database: ${it.message}")
                //TODO("Add UI call telling user there's an issue")
            }

    }
}
