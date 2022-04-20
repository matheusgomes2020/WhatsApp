package com.matheus.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.database.DatabaseReference;
import com.matheus.whatsapp.R;
import com.matheus.whatsapp.config.ConfiguracaoFirebase;
import com.matheus.whatsapp.helper.Base64custom;
import com.matheus.whatsapp.helper.UsuarioFirebase;
import com.matheus.whatsapp.model.Mensagem;
import com.matheus.whatsapp.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView texViewNome;
    private CircleImageView circleImageViewFoto;
    private EditText editMensagem;
    private Usuario usuariodestinatario;

    //Identificador usuarios remetente e destinatário
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //Configurações iniciais
        texViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);

        //Recuperar dados usuário remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();

        //Recuperar dados usuário destinatário
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ){

            usuariodestinatario = (Usuario) bundle.getSerializable( "chatContato" );
            texViewNome.setText( usuariodestinatario.getNome() );

            String foto = usuariodestinatario.getFoto();
            if ( foto != null ){
                Uri url = Uri.parse( usuariodestinatario.getFoto() );
                Glide.with( ChatActivity.this )
                        .load( url )
                        .into( circleImageViewFoto );
            }else {
                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }

            //Recuperar dados usuário destinatário
            idUsuarioDestinatario = Base64custom.codificarBase64( usuariodestinatario.getEmail() );


        }

    }

    public void enviarMensagem( View view ){

        String textoMensagem = editMensagem.getText().toString();

        if ( !textoMensagem.isEmpty() ){

            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario( idUsuarioRemetente );
            mensagem.setMensagem( textoMensagem );

            //Salvar imagem para o remetente
            salvarMensagem( idUsuarioRemetente, idUsuarioDestinatario, mensagem );


        }else {
            Toast.makeText(ChatActivity.this,
                    "Digite uma mensagem para enviar!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void salvarMensagem( String idRemetente, String idDestinatario, Mensagem msg ){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child( idRemetente )
                .child( idDestinatario )
                .push()
                .setValue( msg );

        //Limpar texto
        editMensagem.setText("");

    }

}