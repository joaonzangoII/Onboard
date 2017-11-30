package williamroocka.tut.onboard

import android.os.Handler
import android.os.Message
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*

import williamroocka.tut.onboard.base.BaseActivity
import williamroocka.tut.onboard.managers.Session
import williamroocka.tut.onboard.requests.AuthRequest
import williamroocka.tut.onboard.utils.Constant

open class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setTitle("Registration")
        var session: Session = Session(this)
        // image = (ImageView) findViewById(R.id.image)
        if (savedInstanceState != null) {
            //if there is a bundle, use the saved image resource (if one is there)
            // thumbnail = (Bitmap) savedInstanceState.getParcelable("BitmapImage")
            // image.setImageBitmap(thumbnail)
            //textTargetUri.setText(savedInstanceState.getString("path_to_picture"))
        }

        btn_sign_up.setOnClickListener {
                val name: String? = name.text.toString()
                val id_number: String? = id_number.text.toString()
                val email: String? = email.text.toString()
                val password: String? = password.text.toString()
                val confirmPassword: String? = confirm_password.text.toString()

                AuthRequest.register(session,
                        this@RegisterActivity,
                        name,
                        id_number,
                        email,
                        password,
                        confirmPassword,
                        requestHandler)
            }

        /* btnUploadImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(final View view) {
                 selectImage()
             }
         })*/
    }

    var requestHandler = Handler(object : Handler.Callback {
        override fun handleMessage(message: Message): Boolean {
            val data: Bundle = message.getData()
            val success: Boolean = data.getBoolean(Constant.KEY_SUCCESS)
            if (success) {
                Toast.makeText(this@RegisterActivity,
                        "You have been successfully registered!",
                        Toast.LENGTH_SHORT)
                        .show()

                goToActivity(LoginActivity::class.java)
            } else {

            }
            return false
        }
    })
}
