package com.matheus.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.matheus.whatsapp.R;
import com.matheus.whatsapp.helper.UsuarioFirebase;
import com.matheus.whatsapp.model.Mensagem;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyviewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;
        if ( viewType == TIPO_REMETENTE ){

            item = LayoutInflater.from( parent.getContext()).inflate(R.layout.adapter_mensagem_remetente, parent, false );

        } else if (viewType == TIPO_DESTINATARIO) {

            item = LayoutInflater.from( parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario, parent, false );

        }

        return new MyviewHolder( item );

    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        Mensagem mensagem = mensagens.get( position );
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();

        if ( imagem != null ){
            Uri url = Uri.parse( imagem );
            Glide.with( context ).load( url ).into( holder.imagem );

            //Esconder o texto
            holder.mensagem.setVisibility( View.GONE );

        }else {
            holder.mensagem.setText( msg );

            //Esconder a imagem
            holder.imagem.setVisibility( View.GONE );

        }



    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagens.get( position );

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        if ( idUsuario.equals( mensagem.getIdUsuario() ) ){
            return TIPO_REMETENTE;
        }

        return TIPO_DESTINATARIO;
    }

    public class  MyviewHolder extends RecyclerView.ViewHolder {

        TextView mensagem;
        ImageView imagem;


        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            mensagem = itemView.findViewById(R.id.textMensagemTexto );
            imagem = itemView.findViewById(R.id.imageMensagemFoto );

        }
    }
}
