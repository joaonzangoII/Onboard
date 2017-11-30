package williamroocka.tut.onboard.models

import java.io.Serializable

open class Department : Serializable {
    var id: Long = 0
    var name: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var radius: Float = 0F
    var created_at: String = ""
    var updated_at: String = ""
}
