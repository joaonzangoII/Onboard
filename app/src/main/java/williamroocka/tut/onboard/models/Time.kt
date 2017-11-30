package williamroocka.tut.onboard.models;

import java.io.Serializable;

open class Time : Serializable {
    var id: Long = 0
    var time_in: String = ""
    var time_out: String = ""
    var date: String = ""
    var duration: String = ""
}
