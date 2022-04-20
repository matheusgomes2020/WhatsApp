package com.matheus.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.matheus.whatsapp.R;
import com.matheus.whatsapp.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView texViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuariodestinatario;

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


        }

    }
}