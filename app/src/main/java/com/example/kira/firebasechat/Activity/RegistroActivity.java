package com.example.kira.firebasechat.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kira.firebasechat.Entidades.Usuario;
import com.example.kira.firebasechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre, txtCorreo, txtContraseña, txtContraseñaRepetida;
    private Button btnRegistrar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        txtNombre = findViewById(R.id.idRegistroNombre);
        txtCorreo = findViewById(R.id.idRegistroCorreo);
        txtContraseña = findViewById(R.id.idRegistroContraseña);
        txtContraseñaRepetida = findViewById(R.id.idRegistroContraseñaRepetida);
        btnRegistrar = findViewById(R.id.idRegistroRegistrar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String correo = txtCorreo.getText().toString();
                final String nombre = txtNombre.getText().toString();
                if (isValidEmail(correo) && validarContraseña() && validarUsuario(nombre)){
                    String contraseña = txtContraseña.getText().toString();

                    mAuth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegistroActivity.this, "Se registro correctamente", Toast.LENGTH_SHORT).show();;
                                Usuario usuario = new Usuario();
                                usuario.setNombre(nombre);
                                usuario.setCorreo(correo);
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                DatabaseReference databaseReference = database.getReference("Usuarios/"+currentUser.getUid());
                                databaseReference.setValue(usuario);
                                finish();
                            } else {
                                Toast.makeText(RegistroActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegistroActivity.this, "Error al rellenar los campos", Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean validarContraseña (){
        String contraseña, contraseñaRepetida;
        contraseña = txtContraseña.getText().toString();
        contraseñaRepetida = txtContraseñaRepetida.getText().toString();
        if (contraseña.equals(contraseñaRepetida)) {
            if (contraseña.length()>=6 && contraseña.length()<= 16) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean validarUsuario (String usuario){
        return !usuario.isEmpty();
    }
}
