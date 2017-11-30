package williamroocka.tut.onboard.utils

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast

import williamroocka.tut.onboard.MyApplication

open class Function {
    companion object {
        lateinit var pDialog: ProgressDialog

        fun showToast(context: Context,
                      message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun setLoading(context: Context,
                       isLoading: Boolean) {
            setLoading(context, "Loading...", isLoading)
        }

        fun setLoading(context: Context,
                       message: String,
                       isLoading: Boolean) {
            if (isLoading) {
                pDialog = ProgressDialog(context)
                pDialog.show()
                pDialog.setMessage(message)
            } else {
                pDialog.dismiss()
            }
        }

        fun getApplicationContext(): Context = MyApplication.mInstance
    }
}
