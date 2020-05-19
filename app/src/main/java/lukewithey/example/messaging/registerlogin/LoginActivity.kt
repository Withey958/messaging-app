package lukewithey.example.messaging.registerlogin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import lukewithey.example.messaging.R


private const val TAG = "LoginActivity"

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            onLogin(it)
        }

        need_an_account.setOnClickListener {
            finish()
        }

    }

    private fun onLogin(view: View) {
        //TODO move logic out of activity

        val email = login_email.text.toString()
        val password = login_password.text.toString()

        Log.d(TAG, "Login attempt with email: $email & Password: $password")

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Email or password is empty", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            Log.d(TAG, "Fields are empty")
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "Login Successful")
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "Failed to login user: ${it.message}")
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