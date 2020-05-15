package lukewithey.example.messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_button.setOnClickListener {
            onRegister(it)
        }

        already_have_account_text_view.setOnClickListener {
            Log.d(TAG, "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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
}
