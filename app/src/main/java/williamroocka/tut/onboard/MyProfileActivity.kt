package williamroocka.tut.onboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_my_profile.*
import williamroocka.tut.onboard.managers.Session
import williamroocka.tut.onboard.models.Department
import williamroocka.tut.onboard.models.User

class MyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        title = "My Profile"
        var session: Session = Session(this@MyProfileActivity)
        var user: User = session.loggedInUser

        name.text = "Name: ${user.name}"
        email.text = "Email: ${user.email}"
        id_number.text = "Id Number: ${user.idNumber}"
        //var idNumber: String = user.idNumber
        //date_of_birth.setText(user.date_of_birth)
        gender.text ="Gender: ${user.gender}"
        if (user.department != null) {
            department.text = "Department: ${user.department.name}"
            department.visibility = View.VISIBLE
        } else {
            department.visibility = View.GONE
        }
    }
}
