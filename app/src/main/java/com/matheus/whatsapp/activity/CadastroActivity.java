package com.matheus.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.matheus.whatsapp.R;
import com.matheus.whatsapp.config.ConfiguracaoFirebase;
import com.matheus.whatsapp.helper.Base64custom;
import com.matheus.whatsapp.helper.UsuarioFirebase;
import com.matheus.whatsapp.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editPerfilNome);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    public void cadastrarUsuario(Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){

                    Toast.makeText(CadastroActivity.this,
                            "Sucesso ao cadastrar usuário!",
                            Toast.LENGTH_SHORT).show();
                    UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );
                    finish();

                    try {

                        String identificadorusuario = Base64custom.codificarBase64( usuario.getEmail() );
                        usuario.setId( identificadorusuario );
                        usuario.salvar();

                    }catch ( Exception e ){
                        e.printStackTrace();
                    }


                }else {

                    String excessao = "";
                    try {
                        throw  task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excessao = "Digite uma senha mais forte!";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        excessao = "Por favor, digite um e-mail válido!";
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        excessao = "Esta conta já foi cadastrada!";
                    }
                    catch (Exception e){
                        excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excessao,
                            Toast.LENGTH_SHORT).show();



                }
            }
        });
    }

    public void validarCadastrorUsuario(View view){

        //Recuperar textos dos campos
        String textNome = campoNome.getText().toString();
        String textEmail = campoEmail.getText().toString();
        String textSenha = campoSenha.getText().toString();

        if ( !textNome.isEmpty() ){//verifica nome
            if ( !textEmail.isEmpty() ){//verifica e-mail
                if ( !textSenha.isEmpty() ){//verifica senha

                    Usuario usuario = new Usuario();
                    usuario.setNome( textNome );
                    usuario.setEmail( textEmail );
                    usuario.setSenha( textSenha );

                    cadastrarUsuario( usuario );

                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o e-mail!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
