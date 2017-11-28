package williamroocka.tut.onboard;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import williamroocka.tut.onboard.base.BaseActivity;
import williamroocka.tut.onboard.managers.Session;
import williamroocka.tut.onboard.requests.AuthRequest;
import williamroocka.tut.onboard.utils.Constant;

public class RegisterActivity extends BaseActivity {
    private AutoCompleteTextView edtName;
    private AutoCompleteTextView edtIdNumber;
    private AutoCompleteTextView edtEmail;
    private AutoCompleteTextView edtContact;
    private AutoCompleteTextView edtAddress;
    private AutoCompleteTextView edtPassword;
    private AutoCompleteTextView edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Registration");
        final Session session = new Session(this);
        // image = (ImageView) findViewById(R.id.image);
        edtName = (AutoCompleteTextView) findViewById(R.id.name);
        edtIdNumber = (AutoCompleteTextView) findViewById(R.id.id_number);
        edtEmail = (AutoCompleteTextView) findViewById(R.id.email);
        edtPassword = (AutoCompleteTextView) findViewById(R.id.password);
        edtConfirmPassword = (AutoCompleteTextView) findViewById(R.id.confirm_password);

        if (savedInstanceState != null) {
            //if there is a bundle, use the saved image resource (if one is there)
            // thumbnail = (Bitmap) savedInstanceState.getParcelable("BitmapImage");
            // image.setImageBitmap(thumbnail);
            //textTargetUri.setText(savedInstanceState.getString("path_to_picture"));
        }

        final Button btnRegister = (Button) findViewById(R.id.btn_sign_up);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String name = edtName.getText().toString();
                final String id_number = edtIdNumber.getText().toString();
                final String email = edtEmail.getText().toString();
                final String password = edtPassword.getText().toString();
                final String confirmPassword = edtConfirmPassword.getText().toString();

                AuthRequest.register(session,
                        RegisterActivity.this,
                        name,
                        id_number,
                        email,
                        password,
                        confirmPassword,
                        requestHandler);
            }
        });


       /* btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                selectImage();
            }
        });*/
    }


    final Handler requestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            final Bundle data = message.getData();
            final boolean success = data.getBoolean(Constant.KEY_SUCCESS);
            if (success) {
                Toast.makeText(RegisterActivity.this,
                        "You have been successfully registered!",
                        Toast.LENGTH_SHORT)
                        .show();
                goToActivity(LoginActivity.class);
            }
            return false;
        }
    });
}
