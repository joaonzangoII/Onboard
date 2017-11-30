package williamroocka.tut.onboard.models;

import java.io.Serializable;

open class User : Serializable {
    var id: Long = 0
    var name: String = ""
    var email: String = ""
    var date_of_birth: String = ""
    var idNumber: String = ""
    var gender: String = ""
    var approved: String = ""
    var token: String = ""
    var password_hash: String = ""
    var department: Department = Department()
    var created_at: String = ""
    var updated_at: String = ""
}
